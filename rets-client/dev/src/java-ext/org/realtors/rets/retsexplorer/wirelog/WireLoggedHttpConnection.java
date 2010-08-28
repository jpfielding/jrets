package org.realtors.rets.retsexplorer.wirelog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;

import com.google.common.io.NullOutputStream;

public class WireLoggedHttpConnection extends HttpConnection {
	
	private static final byte[] CRLF = "\r\n".getBytes(Charset.defaultCharset());
	
	private final PrintStream requestBranch;
	private final PrintStream responseBranch;
	private final HttpConnection wrappedConnection;
	
	public WireLoggedHttpConnection(HttpConnection wrapped, OutputStream requestBranch, OutputStream responseBranch) {
		super(wrapped.getHost(), wrapped.getPort());
		this.wrappedConnection = wrapped;
		if (requestBranch instanceof PrintStream) this.requestBranch = (PrintStream)requestBranch;
		else this.requestBranch = new PrintStream((requestBranch==null) ? new NullOutputStream() : requestBranch);
		if (responseBranch instanceof PrintStream) this.responseBranch = (PrintStream)responseBranch;
		else this.responseBranch = new PrintStream((responseBranch==null) ? new NullOutputStream() : responseBranch);
	}
	
	public HttpConnection getWrappedConnection() {
		return this.wrappedConnection;
	}

	// Flush hook
	@Override
	public void flushRequestOutputStream() throws IOException {
		try {
			this.wrappedConnection.flushRequestOutputStream();
		} finally {
			try {
				this.requestBranch.flush();
			} catch (Exception shhh) {
				// Quiet
			}	
		}
		
	}
	
	// Hook for writes that occur via the connection
	@Override
	public void write(byte[] data, int offset, int length) throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.write(data, offset, length);
		} finally {
			try {
				this.requestBranch.write(data, offset, length);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	// Hooks for reads that occur via the connection
	@Override
	@Deprecated
	public String readLine() throws IOException, IllegalStateException {
		String read = null;
		try {
			read = this.wrappedConnection.readLine("US-ASCII");
		} finally {
			try {
				if (read != null) {
					this.responseBranch.print(read);
					this.responseBranch.write(CRLF);
				}
			} catch (Exception shhh) {
				// Quiet
			}
		}
		return read;
	}
	
	@Override
	public String readLine(String charset) throws IOException,IllegalStateException {
		String read = null;
		try {
			read = this.wrappedConnection.readLine(charset);
		} finally {
			try {
				if (read != null) {
					this.responseBranch.print(read);
					this.responseBranch.write(CRLF);
				}
			} catch (Exception shhh) {
				// Quiet
			}
		}
		return read;
	}
	
	// Hook for reads that occur directly from response stream
	@Override
	public InputStream getResponseInputStream() throws IOException,IllegalStateException {
		// NOTE somehow we may need to ensure this gets drained, but attempts on close and finalize b0rk some servers
		return new TeeInputStream(this.wrappedConnection.getResponseInputStream(), this.responseBranch);
	}

	// TODO should this wrap too?
	@Override
	public void setLastResponseInputStream(InputStream inStream) {
		this.wrappedConnection.setLastResponseInputStream(inStream);
	}
	@Override
	public InputStream getLastResponseInputStream() {
		return this.wrappedConnection.getLastResponseInputStream();
	}
	
	@Override
	public void releaseConnection() {
		this.wrappedConnection.releaseConnection();
	}
	
	// All  methods from here down simply delegate to this.wrapped
	@Override
	public void close() {
		this.wrappedConnection.close();
	}
	@Override
	public boolean closeIfStale() throws IOException {
		return this.wrappedConnection.closeIfStale();
	}
	@Override
	public String getHost() {
		return this.wrappedConnection.getHost();
	}
	@Override
	public HttpConnectionManager getHttpConnectionManager() {
		return this.wrappedConnection.getHttpConnectionManager();
	}
	@Override
	public InetAddress getLocalAddress() {
		return this.wrappedConnection.getLocalAddress();
	}
	@Override
	public HttpConnectionParams getParams() {
		return this.wrappedConnection.getParams();
	}
	@Override
	public int getPort() {
		return this.wrappedConnection.getPort();
	}
	@Override
	public Protocol getProtocol() {
		return this.wrappedConnection.getProtocol();
	}
	@Override
	public String getProxyHost() {
		return this.wrappedConnection.getProxyHost();
	}
	@Override
	public int getProxyPort() {
		return this.wrappedConnection.getProxyPort();
	}
	@Override
	public int getSendBufferSize() throws SocketException {
		return this.wrappedConnection.getSendBufferSize();
	}
	@SuppressWarnings("deprecation")
	@Override
	public int getSoTimeout() throws SocketException {
		return this.wrappedConnection.getSoTimeout();
	}
	@SuppressWarnings("deprecation")
	@Override
	public String getVirtualHost() {
		return this.wrappedConnection.getVirtualHost();
	}
	@Override
	public boolean isOpen() {
		return this.wrappedConnection.isOpen();
	}
	@Override
	public boolean isProxied() {
		return this.wrappedConnection.isProxied();
	}
	@Override
	public boolean isResponseAvailable() throws IOException {
		return this.wrappedConnection.isResponseAvailable();
	}
	@Override
	public boolean isResponseAvailable(int timeout) throws IOException {
		return this.wrappedConnection.isResponseAvailable(timeout);
	}
	@Override
	public boolean isSecure() {
		return this.wrappedConnection.isSecure();
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean isStaleCheckingEnabled() {
		return this.wrappedConnection.isStaleCheckingEnabled();
	}
	@Override
	public boolean isTransparent() {
		return this.wrappedConnection.isTransparent();
	}
	@Override
	public void open() throws IOException {
		this.wrappedConnection.open();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void setConnectionTimeout(int timeout) {
		this.wrappedConnection.setConnectionTimeout(timeout);
	}
	@Override
	public void setHost(String host) throws IllegalStateException {
		this.wrappedConnection.setHost(host);
	}
	@Override
	public void setHttpConnectionManager(
			HttpConnectionManager httpConnectionManager) {
		this.wrappedConnection.setHttpConnectionManager(httpConnectionManager);
	}
	@Override
	public void setLocalAddress(InetAddress localAddress) {
		this.wrappedConnection.setLocalAddress(localAddress);
	}
	@Override
	public void setParams(HttpConnectionParams params) {
		this.wrappedConnection.setParams(params);
	}
	@Override
	public void setPort(int port) throws IllegalStateException {
		this.wrappedConnection.setPort(port);
	}
	@Override
	public void setProtocol(Protocol protocol) {
		this.wrappedConnection.setProtocol(protocol);
	}
	@Override
	public void setProxyHost(String host) throws IllegalStateException {
		this.wrappedConnection.setProxyHost(host);
	}
	@Override
	public void setProxyPort(int port) throws IllegalStateException {
		this.wrappedConnection.setProxyPort(port);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void setSendBufferSize(int sendBufferSize)
			throws SocketException {
		this.wrappedConnection.setSendBufferSize(sendBufferSize);
	}
	@Override
	public void setSocketTimeout(int timeout) throws SocketException,
			IllegalStateException {
		this.wrappedConnection.setSocketTimeout(timeout);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void setSoTimeout(int timeout) throws SocketException,
			IllegalStateException {
		this.wrappedConnection.setSoTimeout(timeout);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void setStaleCheckingEnabled(boolean staleCheckEnabled) {
		this.wrappedConnection.setStaleCheckingEnabled(staleCheckEnabled);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void setVirtualHost(String host) throws IllegalStateException {
		this.wrappedConnection.setVirtualHost(host);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void shutdownOutput() {
		this.wrappedConnection.shutdownOutput();
	}
	@Override
	public void tunnelCreated() throws IllegalStateException, IOException {
		this.wrappedConnection.tunnelCreated();
	}

	@Override
	public OutputStream getRequestOutputStream() throws IOException, IllegalStateException {
		return new TeeOutputStream(this.wrappedConnection.getRequestOutputStream(),this.requestBranch);
	}

	@Override
	public void print(String data, String charset) throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.print(data,charset);
		} finally {
			try {
				this.requestBranch.print(data);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	@Override
	public void print(String data) throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.print(data,"US-ASCII");
		} finally {
			try {
				this.requestBranch.print(data);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	@Override
	public void printLine() throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.printLine();
		} finally {
			try {
				this.requestBranch.write(CRLF);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	@Override
	public void printLine(String data, String charset) throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.printLine(data, charset);
		} finally {
			try {
				this.requestBranch.print(data);
				this.requestBranch.write(CRLF);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	@Override
	public void printLine(String data) throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.printLine(data,"US-ASCII");
		} finally {
			try {
				this.requestBranch.print(data);
				this.requestBranch.write(CRLF);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	@Override
	public void write(byte[] data) throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.write(data);
		} finally {
			try {
				this.requestBranch.write(data);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	@Override
	public void writeLine(byte[] data) throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.writeLine(data);
		} finally {
			try {
				this.requestBranch.write(data);
				this.requestBranch.write(CRLF);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}

	@Override
	public void writeLine() throws IOException, IllegalStateException {
		try {
			this.wrappedConnection.writeLine();
		} finally {
			try {
				this.requestBranch.write(CRLF);
			} catch (Exception shhh) {
				// Quiet
			}
		}
	}
	
}

