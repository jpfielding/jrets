package org.realtors.rets.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.realtors.rets.common.util.CaseInsensitiveTreeMap;

/** 
 * Requires http-client > 3.0 
 * 
 * provides:
 * -multithreaded access to a session
 * -gzip aware downloads
 * -digest auth caching (only functional when qop and nc are not honored)
 * -rets-ua-auth
 */
public class CommonsHttpClient extends RetsHttpClient {
	private static final int DEFAULT_TIMEOUT = 300000;
	private static final String RETS_VERSION = "RETS-Version";
	private static final String RETS_SESSION_ID = "RETS-Session-ID";
	private static final String RETS_REQUEST_ID = "RETS-Request-ID";
	private static final String USER_AGENT = "User-Agent";
	private static final String RETS_UA_AUTH_HEADER = "RETS-UA-Authorization";
	private static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String CONTENT_ENCODING = "Content-Encoding";
	public static final String DEFLATE_ENCODINGS = "gzip,deflate";
	
	private static final Log WIRE_LOG = WireLogInputStream.getWireLog();
	
	private final ConcurrentHashMap<String, String> defaultHeaders;
	private final HttpClient httpClient;
	
	// method choice improvement
	private final String userAgentPassword;

	public CommonsHttpClient() {
		this(DEFAULT_TIMEOUT, null, true);
	}
	public CommonsHttpClient(int timeout, String userAgentPassword, boolean gzip) {
		this.defaultHeaders = new ConcurrentHashMap<String, String>();
		this.userAgentPassword = userAgentPassword;
		MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
		// allows for multi threaded requests from a single client
		this.httpClient = new HttpClient(multiThreadedHttpConnectionManager);
		// ask the server if we can use gzip
		if( gzip ) this.addDefaultHeader(ACCEPT_ENCODING, DEFLATE_ENCODINGS);
		// timeouts
		this.httpClient.getParams().setConnectionManagerTimeout(timeout);
		this.httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
		this.httpClient.getParams().setSoTimeout(timeout);
		// set to rfc 2109 as it puts the ASP (IIS) cookie _FIRST_, this is critical for interealty
		this.httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
	}
	
	public HttpClient getHttpClient(){
		return this.httpClient;
	}

	//----------------------method implementations
 	@Override
	public void setUserCredentials(String userName, String password) {
		this.httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME), new UsernamePasswordCredentials(userName, password));
	}
	@Override
	public RetsHttpResponse doRequest(String httpMethod, RetsHttpRequest request) throws RetsException {
		return "GET".equals(StringUtils.upperCase(httpMethod)) ? this.doGet(request) : this.doPost(request);
	}

	//----------------------method implementations
	public RetsHttpResponse doGet(RetsHttpRequest request) throws RetsException {
		String url = request.getUrl();
		String args = request.getHttpParameters();
		if (args != null) {
			url = url + "?" + args;
		}
		HttpMethod method = new GetMethod(url);
		WIRE_LOG.debug(String.format("\n>>>\nGET %s",url));
		return execute(method, request.getHeaders());
	}

	public RetsHttpResponse doPost(RetsHttpRequest request) throws RetsException {
		String url = request.getUrl();
		String body = request.getHttpParameters();
		if (body == null) body = "";  // commons-httpclient 3.0 refuses to accept null entity (body)
		PostMethod method = new PostMethod(url);
		method.setRequestEntity(new StringRequestEntity(body));
		method.setRequestHeader("Content-Type", "application/x-www-url-encoded");
		WIRE_LOG.debug(String.format("\n>>>\nPOST %s\nArgs: %s",url,body));
		return execute(method, request.getHeaders());
	}

	private RetsHttpResponse execute(final HttpMethod method, Map<String,String> headers) throws RetsException {
		try {
			// add the default headers
			if (this.defaultHeaders != null) {
				for (Map.Entry<String,String> entry : this.defaultHeaders.entrySet()) {
					method.setRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			// add our request headers from rets
			if (headers != null) {
				for (Map.Entry<String,String> entry : headers.entrySet()) {
					method.setRequestHeader(entry.getKey(), entry.getValue());
				}
			}
			
			// optional ua-auth stuff here
			if( this.userAgentPassword != null ){
			    method.setRequestHeader(RETS_UA_AUTH_HEADER, this.calculateUaAuthHeader(method,getCookies()));
			}
			// try to execute the request
			final int responseCode = this.httpClient.executeMethod(method);
			if (responseCode != HttpStatus.SC_OK) {
				throw new InvalidHttpStatusException(responseCode, method.getStatusText());
			}
			return new CommonsHttpClientResponse(responseCode, method, getCookies());
		} catch (Exception e) {
			throw new RetsException(e);
		}
	}

	@Override
	public synchronized void addDefaultHeader(String key, String value) {
		this.defaultHeaders.put(key, value);
		if( value == null ) this.defaultHeaders.remove(key);
	}
	
	private Map<String,String> getCookies() {
		Map<String,String> cookieMap = new CaseInsensitiveTreeMap();
		for (Cookie cookie : this.httpClient.getState().getCookies()) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
		return cookieMap;
	}

	private String calculateUaAuthHeader(HttpMethod method, Map<String, String> cookies ) {
		final String userAgent = this.getHeaderValue(method, USER_AGENT);
		final String requestId = this.getHeaderValue(method, RETS_REQUEST_ID);
		final String sessionId = cookies.get(RETS_SESSION_ID);
		final String retsVersion = this.getHeaderValue(method, RETS_VERSION);
		String secretHash = DigestUtils.md5Hex(String.format("%s:%s",userAgent,this.userAgentPassword));
    	String pieces = String.format("%s:%s:%s:%s",secretHash,StringUtils.trimToEmpty(requestId),StringUtils.trimToEmpty(sessionId),retsVersion);
		return String.format("Digest %s", DigestUtils.md5Hex(pieces));
	}
	
    private String getHeaderValue(HttpMethod method, String key){
    	Header requestHeader = method.getRequestHeader(key);
    	if( requestHeader == null ) return null;
		return requestHeader.getValue();
    }
}
