package org.realtors.rets.util.transaction;

import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.MetadataException;
import org.realtors.rets.util.MetadataCollector;
import org.realtors.rets.util.RetsClient;
import org.realtors.rets.util.RetsTransaction;


public class GetIncrementalMetadata implements RetsTransaction<Metadata> {
	private RetsClient retsClient;

    public GetIncrementalMetadata(RetsClient retsClient) {
		this.retsClient = retsClient;
	}

	public Metadata execute(RetsSession session) throws Exception {
		try {
			return new Metadata(new MetadataCollector(this.retsClient));
		} catch (MetadataException e) {
			throw new RetsException("Problem collecting Metadata.", e);
		}
	}
}
