package org.realtors.rets.ext.util.transaction.metadata;

import org.realtors.rets.client.RetsSession;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.ext.util.RetsClient;
import org.realtors.rets.ext.util.RetsTransaction;


public class MetadataRetsTransaction implements RetsTransaction<Metadata> {
    private RetsClient retsClient;

    public MetadataRetsTransaction(RetsClient retsClient) {
        this.retsClient = retsClient;
    }

    public Metadata execute(RetsSession session) throws Exception {
        RetsTransaction<Metadata> metadataTransaction = this.createGetMetadataTransaction();
        return metadataTransaction.execute(session);
    }

    private RetsTransaction<Metadata> createGetMetadataTransaction() {
    	return this.retsClient.getRetsProfile().getMetadataProvider().apply(this.retsClient);
    }
}
