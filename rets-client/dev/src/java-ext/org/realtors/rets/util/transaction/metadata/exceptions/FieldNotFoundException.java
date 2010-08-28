package org.realtors.rets.util.transaction.metadata.exceptions;

import org.realtors.rets.util.RetsField;

public class FieldNotFoundException extends RuntimeException {

	public FieldNotFoundException(RetsField field) {
		super(String.format("No such field (%s) found in RETS metadata", field));
	}

}
