package org.realtors.rets.util.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.realtors.rets.client.GetMetadataRequest;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MSystem;
import org.realtors.rets.util.RetsClient;
import org.realtors.rets.util.RetsMetadataFormat;
import org.realtors.rets.util.RetsProfile;
import org.realtors.rets.util.RetsTransaction;

import com.google.common.io.Closeables;


public class CachedMetadataTransaction implements RetsTransaction<Metadata>{
	private RetsClient client;
	
	public CachedMetadataTransaction(RetsClient client){
		this.client = client;
	}
	
	public Metadata execute(RetsSession session) throws Exception {
		// only read the cache if we support it
		Logger.getLogger(this.getClass()).debug(String.format("[%s] reading the latest version info", this.client));
		final Metadata latest = getMetadataHead(session);
		final File file = this.getFile(latest.getSystem());
		file.getParentFile().mkdirs();
		Metadata metadata = this.readCachedMetadata(file);
		if( metadata != null ) return metadata;
		Logger.getLogger(this.getClass()).debug(String.format("[%s] reading the latest metadata", this.client));
		metadata = new GetMetadata(this.client.getRetsProfile().getMetadataFormat()).execute(session);
		if( this.client.getRetsProfile().getMetadataCachePath() != null ) this.writeCachedMetadata(metadata);
		return metadata;
	}
	private File getFile(MSystem system) {
		RetsProfile profile = this.client.getRetsProfile();
		String systemID = system.getSystemID();
		int version = system.getVersion();
		String provider = "FULL";
		RetsMetadataFormat format = profile.getMetadataFormat();
		String userAgent = profile.getUserAgent().replaceAll("/","_");
		String username = profile.getCredentials().getUsername();
		String strict = profile.isRetsStrict()? "strict":"non-strict";
		DateTime systemDate = new DateTime(system.getDate());
		String date = systemDate==null ? "nodate" : systemDate.toString("yyyyMMdd_HHmmss");
		return new File(this.client.getRetsProfile().getMetadataCachePath(),String.format("%s-%s-%s-%s-%s-%s-%s-%s.ser",systemID,version,date,provider,format,strict,userAgent,username));
	}
	private Metadata readCachedMetadata(File file) throws Exception {
		// if caching is on, try to pull it
		if( this.client.getRetsProfile().getMetadataCachePath() == null || !file.exists() ) return null;
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
		try {
			Logger.getLogger(this.getClass()).debug(String.format("RetsClient [%s] using version metadata: %s", this.client, file.getAbsolutePath()));
			return (Metadata) objectInputStream.readObject();
		}catch(Exception e){
			Logger.getLogger(this.getClass()).warn(String.format("RetsClient [%s] error loading cached metadata %s", this.client, file.getCanonicalPath()), e);
			if( file.exists() ) file.delete();
			return null;
		} finally {
			Closeables.closeQuietly(objectInputStream);
		}
	}
	public void writeCachedMetadata(Metadata metadata) {
		if( this.client.getRetsProfile().getMetadataCachePath() == null || metadata == null ) return;
		ObjectOutputStream objectOutputStream = null; 
		File file = this.getFile(metadata.getSystem());
		try {
			if( file.exists() ) file.delete();
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
			// write out our existing metadata
			Logger.getLogger(this.getClass()).debug(String.format("RetsClient [%s] writing metadata cache to %s", this.client, file.getAbsolutePath()));
			objectOutputStream.writeObject(metadata);
			objectOutputStream.flush();
		}catch (Exception e){
			Logger.getLogger(this.getClass()).warn(String.format("RetsClient [%s] error writing metadata cache to %s", this.client, file.getAbsolutePath()),e);
		} finally {
			Closeables.closeQuietly(objectOutputStream);
		}
	}
	
	private Metadata getMetadataHead(RetsSession session) throws Exception{
		GetMetadataRequest request = new GetMetadataRequest("SYSTEM", "0");
		this.client.getRetsProfile().getMetadataFormat().setMetadataResultFormat(request);
		MetaObject[] metadata = session.getMetadata(request).getMetadata();
		if(metadata.length != 1 || !(metadata[0] instanceof MSystem))
			throw new IllegalArgumentException("1 MetaObject of type MSystem was expected.");
		MSystem system = (MSystem) metadata[0];
		return new Metadata(system);
	}
}
