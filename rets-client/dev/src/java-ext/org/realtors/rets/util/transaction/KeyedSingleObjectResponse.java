package org.realtors.rets.util.transaction;

import java.util.List;

import org.realtors.rets.client.SingleObjectResponse;

/**
 * Wraps a RETS SingleObjectResponse with the keys that
 * were used to get that object from the RETS server.
 */
public class KeyedSingleObjectResponse {
	
	private final SingleObjectResponse response;
	private final List<String> keyNames;
	private final List<String> keys;
	
	public List<String> getKeyNames() {
		return this.keyNames;
	}

	public KeyedSingleObjectResponse(SingleObjectResponse response, List<String> keyNames, List<String> keys) {
		super();
		this.response = response;
		this.keyNames = keyNames;
		this.keys = keys;
	}
	
	public List<String> getKeys() {
		return this.keys;
	}
	
	public SingleObjectResponse getResponse() {
		return this.response;
	}

}
