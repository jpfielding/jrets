package org.realtors.rets.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.realtors.rets.client.GetMetadataRequest;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.common.metadata.MetaCollector;
import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.MetadataException;
import org.realtors.rets.common.metadata.MetadataType;

public class MetadataCollector implements MetaCollector {
	private RetsClient retsClient;

    /** should we take an initial populating session? */
	public MetadataCollector(RetsClient retsClient) {
		this.retsClient = retsClient;
	}

	public MetaObject[] getMetadata(MetadataType type, String path) throws MetadataException {
        return this.getMetadata(type, path, "0");
	}

	public MetaObject[] getMetadataRecursive(MetadataType type, String path) throws MetadataException {
		return this.getMetadata(type, path, "*");
	}

	private MetaObject[] getMetadata(MetadataType type, String path, String sfx) throws MetadataException {
		String typeName = type.name();
		if(StringUtils.isBlank(path))
			return this.getMetadata(typeName, sfx);
		return this.getMetadata(typeName, path, sfx);
	}

	private MetaObject[] getMetadata(String typeName, String sfx) throws MetadataException {
		try {
			return this.getMetadata(new GetMetadataRequest(typeName, sfx));
		} catch (RetsException e) {
			throw new MetadataException("Problem constructing GetMetadataRequest.", e);
		}
	}

	private MetaObject[] getMetadata(String typeName, String path, String sfx) throws MetadataException {
		List pathComponents = new LinkedList(Arrays.asList(path.split(":")));
		
        if(!"0".equals(sfx) || this.retsClient.getRetsProfile().isUseZeroMetadataPathSuffix())
		    pathComponents.add(sfx);
		
		String[] ids = (String[]) pathComponents.toArray(new String[pathComponents.size()]);
		try {
			return this.getMetadata(new GetMetadataRequest(typeName, ids));
		} catch (RetsException e) {
			throw new MetadataException("Problem constructing GetMetadataRequest.", e);
		}
	}
	
	private MetaObject[] getMetadata(final GetMetadataRequest request) throws MetadataException {
		this.retsClient.getRetsProfile().getMetadataFormat().setMetadataResultFormat(request);
		RetsTransaction<MetaObject[]> transaction = new RetsTransaction<MetaObject[]>() {
			public MetaObject[] execute(RetsSession session) throws Exception {
				return session.getMetadata(request).getMetadata();
			}
		};
		
		return this.getMetadata(transaction);
	}

    private MetaObject[] getMetadata(RetsTransaction transaction) throws MetadataException {
        try {
			return (MetaObject[]) this.retsClient.executeRetsTransaction(transaction);
		} catch (Exception e) {
			throw new MetadataException(e);
		}
    }

}