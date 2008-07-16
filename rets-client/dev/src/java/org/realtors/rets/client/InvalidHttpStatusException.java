package org.realtors.rets.client;

import org.apache.commons.httpclient.HttpStatus;

public class InvalidHttpStatusException extends RetsException {
	public InvalidHttpStatusException(int code) {
		super("Status code (" + code + ") " + HttpStatus.getStatusText(code));
	}
	public InvalidHttpStatusException(int code, String message) {
		super("Status code (" + code + ") " + HttpStatus.getStatusText(code) +" '"+message+"'");
	}
}