package org.realtors.rets.util.transaction;

import org.realtors.rets.client.GetObjectRequest;
import org.realtors.rets.client.GetObjectResponse;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.util.RetsObjectType;
import org.realtors.rets.util.RetsSearchType;
import org.realtors.rets.util.RetsTransaction;


public class GetObject implements RetsTransaction<GetObjectResponse> {
	private final GetObjectRequest request;

	public GetObject(GetObjectRequest request) {
		this.request = request;
	}

	public GetObject(RetsSearchType searchType, RetsObjectType objectType, String key, ObjectId objectId) {
		this(new GetObjectRequest(searchType.getName(), objectType.getName()));
		this.request.addObject(key, objectId.getName());
	}

	public GetObjectResponse execute(RetsSession session) throws Exception {
		return session.getObject(this.request);
	}
}
