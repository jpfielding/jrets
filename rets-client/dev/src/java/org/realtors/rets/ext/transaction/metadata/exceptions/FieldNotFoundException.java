package org.realtors.rets.ext.transaction.metadata.exceptions;

import org.realtors.rets.ext.RetsField;

public class FieldNotFoundException extends RuntimeException {

	public FieldNotFoundException(RetsField field) {
		super(String.format("No such field (%s) found in RETS metadata", field));
	}

}
