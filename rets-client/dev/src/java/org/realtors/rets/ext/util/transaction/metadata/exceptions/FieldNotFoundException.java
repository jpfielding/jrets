package org.realtors.rets.ext.util.transaction.metadata.exceptions;

import org.realtors.rets.ext.util.RetsField;

public class FieldNotFoundException extends RuntimeException {

	public FieldNotFoundException(RetsField field) {
		super(String.format("No such field (%s) found in RETS metadata", field));
	}

}
