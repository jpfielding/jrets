package org.realtors.rets.ext.retsexplorer.wirelog;

import java.io.OutputStream;

import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

public class WireLoggedHttpConnectionManager implements HttpConnectionManager {
	
	private final OutputStream requestBranch;
	private final OutputStream responseBranch;
	private final HttpConnectionManager delegate;
	
	public WireLoggedHttpConnectionManager(HttpConnectionManager mgr, OutputStream requestBranch, OutputStream responseBranch) {
		this.requestBranch = requestBranch;
		this.responseBranch = responseBranch;
		this.delegate = mgr;
	}

	// Wraps a connection with a teed connection
	private HttpConnection wrap(HttpConnection conn) {
		return new WireLoggedHttpConnection(conn, this.requestBranch, this.responseBranch);
	}

	// Wrap all managed connections
	public HttpConnection getConnection(HostConfiguration hostConfiguration) {
		return wrap(this.delegate.getConnection(hostConfiguration));
	}
	public void releaseConnection(HttpConnection conn) {
		this.delegate.releaseConnection(((WireLoggedHttpConnection)conn).getWrappedConnection());
	}
	public HttpConnection getConnection(HostConfiguration hostConfiguration, long timeout) throws HttpException {
		return wrap(this.delegate.getConnection(hostConfiguration));
	}
	public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout) throws ConnectionPoolTimeoutException {
		return wrap(this.delegate.getConnectionWithTimeout(hostConfiguration, timeout));
	}

	// These 4 methods simply delegate to this.hooked
	public void closeIdleConnections(long idleTimeout) {
		this.delegate.closeIdleConnections(idleTimeout);
	}
	public HttpConnectionManagerParams getParams() {
		return this.delegate.getParams();
	}
	public void setParams(HttpConnectionManagerParams params) {
		this.delegate.setParams(params);
	}
	
	
	
}