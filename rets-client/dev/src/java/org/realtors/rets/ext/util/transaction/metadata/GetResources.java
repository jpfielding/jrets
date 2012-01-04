package org.realtors.rets.ext.util.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.ext.util.RetsMetadataTransaction;


public class GetResources implements RetsMetadataTransaction<MResource[]> {
	public MResource[] execute(Metadata metadata) throws Exception {
		return metadata.getSystem().getMResources();
	}
}
