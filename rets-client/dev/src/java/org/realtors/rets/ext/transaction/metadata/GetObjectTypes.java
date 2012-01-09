package org.realtors.rets.ext.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MObject;
import org.realtors.rets.ext.RetsMetadataTransaction;
import org.realtors.rets.ext.RetsSearchType;


public class GetObjectTypes implements RetsMetadataTransaction<MObject[]> {
	private GetResource getResource;

	public GetObjectTypes(RetsSearchType resource) {
		this.getResource = new GetResource(resource);
	}


	public MObject[] execute(Metadata metadata) throws Exception {
		return this.getResource.execute(metadata).getMObjects();
	}

}
