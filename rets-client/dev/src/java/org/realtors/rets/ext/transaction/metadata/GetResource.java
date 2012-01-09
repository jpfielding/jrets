package org.realtors.rets.ext.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.ext.RetsMetadataTransaction;
import org.realtors.rets.ext.RetsSearchType;


public class GetResource implements RetsMetadataTransaction<MResource> {
	private RetsSearchType searchType;

	public GetResource(RetsSearchType searchType) {
		this.searchType = searchType;
	}

	protected String getResourceType() {
		return this.searchType.getName();
	}

	public MResource execute(Metadata metadata) throws Exception {
		return metadata.getResource(this.getResourceType());
	}
}
