package org.realtors.rets.ext.util.transaction.metadata.exceptions;

import org.realtors.rets.ext.util.ResourceClass;

/** Thrown to signify that no resource class exists for a given name */
public class ResourceClassNotFoundException extends RuntimeException {

	public ResourceClassNotFoundException(ResourceClass resourceClass) {
		super(String.format("No such resource class exists: %s", resourceClass));
	}

}
