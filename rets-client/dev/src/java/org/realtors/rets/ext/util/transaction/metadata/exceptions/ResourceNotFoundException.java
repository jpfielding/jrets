package org.realtors.rets.ext.util.transaction.metadata.exceptions;

/** Thrown to signify that no such resource exists */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String className) {
		super(String.format("No such resource exists: %s", className));
	}

}
