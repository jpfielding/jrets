package org.realtors.rets.ext.transaction.metadata.exceptions;

import org.realtors.rets.ext.RetsField;

/** Thrown to signify that a field was expected to have a LookupName but did not */
public class NoLookupNameException extends RuntimeException {

	public NoLookupNameException(RetsField field) {
		super(String.format("Field (%s) has no LookupName", field));
	}

}
