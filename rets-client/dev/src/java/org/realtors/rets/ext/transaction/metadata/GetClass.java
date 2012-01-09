package org.realtors.rets.ext.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.ext.ResourceClass;
import org.realtors.rets.ext.RetsMetadataTransaction;
import org.realtors.rets.ext.transaction.metadata.exceptions.ResourceNotFoundException;


public class GetClass implements RetsMetadataTransaction<MClass> {
	private GetResource getResource;
	private String className;

    public GetClass(ResourceClass resourceClass) {
        this.getResource = new GetResource(resourceClass.getResource());
		this.className = resourceClass.getClassName();
    }

    public MClass execute(Metadata metadata) throws Exception {
		MResource resource = this.getResource.execute(metadata);
		if (resource == null) throw new ResourceNotFoundException(this.className);
		return resource.getMClass(this.className);
	}
}
