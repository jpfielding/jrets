package org.realtors.rets.ext.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MEditMask;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.ext.RetsMetadataTransaction;
import org.realtors.rets.ext.RetsSearchType;


public class GetEditMask implements RetsMetadataTransaction<MEditMask> {
	private GetResource getResource;
    private String editMaskId;

    public GetEditMask(RetsSearchType searchType, String editMaskId) {
		this.getResource = new GetResource(searchType);
        this.editMaskId = editMaskId;
    }

    public MEditMask execute(Metadata metadata) throws Exception {
        MResource resource = this.getResource.execute(metadata);
        return resource.getMEditMask(this.editMaskId);
    }
}
