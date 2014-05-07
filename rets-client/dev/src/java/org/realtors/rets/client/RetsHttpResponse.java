package org.realtors.rets.client;

import java.util.Map;
import java.io.Closeable;
import java.io.InputStream;

public interface RetsHttpResponse extends Closeable {
	public int getResponseCode() throws RetsException;

	public Map getHeaders() throws RetsException;

	public String getHeader(String hdr) throws RetsException;

	public String getCookie(String cookie) throws RetsException;

	public String getCharset() throws RetsException;
	
	public InputStream getInputStream() throws RetsException;

	public Map getCookies() throws RetsException;

}