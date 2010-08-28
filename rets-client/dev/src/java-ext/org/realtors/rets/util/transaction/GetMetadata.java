package org.realtors.rets.util.transaction;

import org.realtors.rets.client.GetMetadataRequest;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MSystem;
import org.realtors.rets.util.RetsMetadataFormat;
import org.realtors.rets.util.RetsTransaction;


public class GetMetadata implements RetsTransaction<Metadata> {
	private final RetsMetadataFormat format;

	public GetMetadata(RetsMetadataFormat format) {
		this.format = format;
	}

	public Metadata execute(RetsSession session) throws Exception {
		GetMetadataRequest request = new GetMetadataRequest("SYSTEM", "*");
		this.format.setMetadataResultFormat(request);
		MetaObject[] metadata = session.getMetadata(request).getMetadata();
		if(metadata.length != 1 || !(metadata[0] instanceof MSystem))
			throw new IllegalArgumentException("1 MetaObject of type MSystem was expected.");
		MSystem system = (MSystem) metadata[0];
		return new Metadata(system);
	}
}