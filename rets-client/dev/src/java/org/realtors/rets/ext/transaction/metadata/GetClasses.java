package org.realtors.rets.ext.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.ext.RetsMetadataTransaction;
import org.realtors.rets.ext.RetsSearchType;


public class GetClasses implements RetsMetadataTransaction<MClass[]> {
	private GetResource getResource;

	public GetClasses(RetsSearchType searchType) {
		this.getResource = new GetResource(searchType);
	}

	public MClass[] execute(Metadata metadata) throws Exception {
		MResource resource = this.getResource.execute(metadata);
		return resource.getMClasses();
	}
}
