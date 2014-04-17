package org.realtors.rets.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.realtors.rets.common.util.CaseInsensitiveTreeMap;

public class CommonsHttpClientResponse implements RetsHttpResponse {

	private HttpResponse response;
	private Map<String,String> headers;
	private Map<String,String> cookies;

	public CommonsHttpClientResponse(HttpResponse response, Map<String,String> cookies) {
		this.response = response;
		this.cookies = new CaseInsensitiveTreeMap<String,String>(cookies);

		this.headers = new CaseInsensitiveTreeMap<String,String>();
		for (Header header : this.response.getAllHeaders()) {
			this.headers.put(header.getName(), header.getValue());
		}
	}

	public int getResponseCode() {
		return this.response.getStatusLine().getStatusCode();
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

	public String getCharset() {
		String contentType = StringUtils.trimToEmpty(this.getHeader(CommonsHttpClient.CONTENT_TYPE)).toLowerCase();
		String[] split = StringUtils.split(contentType, ";");
		if (split == null) return null;
		for (String s : split) {
		    String trimmed = s.trim();
		    if (trimmed.toLowerCase().contains("charset=")) {
		        return StringUtils.substringAfter(trimmed, "charset=");
		    }
		}
		return null;
	}

	/**  using this mess to provide logging, gzipping and httpmethod closing */
	public InputStream getInputStream() throws RetsException {
		try {
			// get our underlying stream
			InputStream inputStream = this.response.getEntity().getContent();
			// gzipped aware checks
			String contentEncoding = StringUtils.trimToEmpty(this.getHeader(CommonsHttpClient.CONTENT_ENCODING)).toLowerCase();
			boolean gzipped = ArrayUtils.contains(CommonsHttpClient.DEFLATE_ENCODINGS.split(","),contentEncoding);
			if( gzipped ) inputStream = new GZIPInputStream(inputStream);
			return inputStream;
		} catch (IOException e) {
			throw new RetsException(e);
		}
	}

}
