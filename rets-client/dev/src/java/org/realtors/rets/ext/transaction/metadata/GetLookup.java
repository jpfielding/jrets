package org.realtors.rets.ext.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.ext.RetsMetadataTransaction;
import org.realtors.rets.ext.RetsSearchType;


public class GetLookup implements RetsMetadataTransaction<MLookup> {
	private GetResource getResource;
	private String lookupName;

	public GetLookup(RetsSearchType searchType, String lookupName) {
		this.getResource = new GetResource(searchType);
		this.setLookupName(lookupName);
	}

	protected GetLookup(RetsSearchType searchType) {
		this(searchType, null);
	}

	protected final void setLookupName(String lookupName) {
		this.lookupName = lookupName;
	}

	public MLookup execute(Metadata metadata) throws Exception {
		MResource resource = this.getResource.execute(metadata);
		return resource.getMLookup(this.lookupName);
	}
}
