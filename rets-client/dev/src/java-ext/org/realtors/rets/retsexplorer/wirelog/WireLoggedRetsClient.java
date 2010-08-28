package org.realtors.rets.retsexplorer.wirelog;

import java.io.OutputStream;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.realtors.rets.client.CommonsHttpClient;
import org.realtors.rets.client.RetsHttpClient;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.util.RetsClient;
import org.realtors.rets.util.RetsProfile;

public class WireLoggedRetsClient extends RetsClient {

	private final OutputStream requestBranch;
	private final OutputStream responseBranch;
	
	public WireLoggedRetsClient(RetsProfile retsProfile, OutputStream requestBranch, OutputStream responseBranch) {
		super(retsProfile);
		this.requestBranch = requestBranch;
		this.responseBranch = responseBranch;
	}
	
	/**
	 * @throws IllegalArgumentException if retsClient is not a CommonsHttpClient
	 */
	@Override
	protected RetsSession createRetsSession(RetsHttpClient retsClient) {
		if (!(retsClient instanceof CommonsHttpClient)) {
			throw new IllegalArgumentException("Client must be instance of CommonsHttpClient");
		}
		CommonsHttpClient client = (CommonsHttpClient)retsClient;
		HttpConnectionManager mgr = client.getHttpClient().getHttpConnectionManager();
		mgr = new WireLoggedHttpConnectionManager(mgr, this.requestBranch, this.responseBranch);
		client.getHttpClient().setHttpConnectionManager(mgr);
		return super.createRetsSession(retsClient);
	}

}
