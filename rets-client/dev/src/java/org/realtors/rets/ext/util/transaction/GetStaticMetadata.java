package org.realtors.rets.ext.util.transaction;

import java.net.URL;

import org.realtors.rets.client.GetMetadataResponse;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MSystem;
import org.realtors.rets.ext.util.RetsClient;
import org.realtors.rets.ext.util.RetsMetadataFormat;
import org.realtors.rets.ext.util.RetsTransaction;


public class GetStaticMetadata implements RetsTransaction<Metadata> {

    private final RetsClient client;

	public GetStaticMetadata(RetsClient client) {
		this.client = client;
    }

	public Metadata execute(RetsSession session) throws Exception {
		URL resource = Thread.currentThread().getContextClassLoader().getResource("rets.metadata.xml");
		boolean isCompact = this.client.getRetsProfile().getMetadataFormat() == RetsMetadataFormat.Compact;
		boolean retsStrict = this.client.getRetsProfile().isRetsStrict();
		MetaObject[] metadata = new GetMetadataResponse(resource.openStream(),isCompact,retsStrict).getMetadata();
		if(metadata.length != 1 || !(metadata[0] instanceof MSystem))
			throw new IllegalArgumentException("1 MetaObject of type MSystem was expected.");
		MSystem system = (MSystem) metadata[0];
		return new Metadata(system);
	}
}
