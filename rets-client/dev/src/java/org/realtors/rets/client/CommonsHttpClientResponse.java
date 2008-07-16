package org.realtors.rets.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.realtors.rets.common.util.CaseInsensitiveTreeMap;

public class CommonsHttpClientResponse implements RetsHttpResponse {
	private static final Log WIRE_LOG = WireLogInputStream.getWireLog();

	private int responseCode;
	private HttpMethod method;
	private Map<String,String> headers;
	private Map<String,String> cookies;

	public CommonsHttpClientResponse(int responseCode, HttpMethod method, Map<String,String> cookies) {
		this.responseCode = responseCode;
		this.method = method;
		this.cookies = new CaseInsensitiveTreeMap<String,String>(cookies);

		this.headers = new CaseInsensitiveTreeMap<String,String>();
		for (Header header : this.method.getResponseHeaders()) 
			this.headers.put(header.getName(), header.getValue());
	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public Map<String,String> getHeaders() {
		return this.headers;
	}

	public String getHeader(String header) {
		return this.headers.get(header);
	}

	public Map<String,String> getCookies() throws RetsException {
		return this.cookies;
	}

	public String getCookie(String cookie) throws RetsException {
		return this.cookies.get(cookie);
	}

	/**  using this mess to provide logging, gzipping and httpmethod closing */
	public InputStream getInputStream() throws RetsException {
		try {
			// get our underlying stream
			InputStream inputStream = this.method.getResponseBodyAsStream();
			// gzipped aware checks
			String contentEncoding = StringUtils.trimToEmpty(this.getHeader(CommonsHttpClient.CONTENT_ENCODING)).toLowerCase();
			boolean gzipped = ArrayUtils.contains(CommonsHttpClient.DEFLATE_ENCODINGS.split(","),contentEncoding);
			if( gzipped ) inputStream = new GZIPInputStream(inputStream);
			// wire logging
			if( WIRE_LOG.isDebugEnabled() ) inputStream = new WireLogInputStream(inputStream);
			
			final InputStream in = inputStream;
			// the http method close wrapper (necessary)
			return new InputStream(){
				@Override
				public int read() throws IOException {
					return in.read();
				}
				@Override
				public int read(byte[] b) throws IOException {
					return in.read(b);
				}
				@Override
				public int read(byte[] b, int off, int len) throws IOException {
					return in.read(b, off, len);
				}
				@Override
				public void close() throws IOException {
					// connection release _AFTER_ the input stream has been read
					CommonsHttpClientResponse.this.method.releaseConnection();
				}
			};
		} catch (IOException e) {
			throw new RetsException(e);
		}
	}

}
