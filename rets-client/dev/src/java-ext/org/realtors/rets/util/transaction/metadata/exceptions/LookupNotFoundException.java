package org.realtors.rets.util.transaction.metadata.exceptions;

import org.realtors.rets.util.ResourceClass;

/** Thrown to signify that no Lookup was defined for a given LookupName */
public class LookupNotFoundException extends RuntimeException {

	public LookupNotFoundException(ResourceClass resourceClass, String lookupName) {
		super(String.format("No lookup of name %s exists in resource class %s", lookupName, resourceClass));
	}

}
