package org.realtors.rets.client;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.RFC2109SpecProvider;
import org.apache.http.message.BasicHeader;
import org.realtors.rets.common.util.CaseInsensitiveTreeMap;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

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
	public static final String CONTENT_TYPE = "Content-Type";
	
	public static Builder custom() {
		return new Builder();
	}
	
	public static class Builder {
		
		public List<Header> headers;
		
		protected HttpClientBuilder httpClientBuilder;
		
		Builder() {
			this.headers = Lists.newArrayList();
			this.httpClientBuilder = HttpClients.custom();
		}
		
		public Builder setTimeouts(int timeout) {
			this.socketConfigBuilder.get().setSoTimeout(timeout);
			this.requestConfigBuilder.get().setConnectTimeout(timeout);
			return this;
		}
		
		public Supplier<CookieStore> cookieStoreBuilder = (() -> new BasicCookieStore());
		
		public Supplier<RequestConfig.Builder> requestConfigBuilder = (() -> RequestConfig.custom().setConnectTimeout(DEFAULT_TIMEOUT));
		
		public Supplier<SocketConfig.Builder> socketConfigBuilder = (() -> SocketConfig.custom().setSoTimeout(DEFAULT_TIMEOUT));
		
		public Supplier<RegistryBuilder> cookieSpec = (() -> 
				RegistryBuilder.<CookieSpecProvider>create().register(CookieSpecs.DEFAULT, new RFC2109SpecProvider()));
		
		public Supplier<HttpClientConnectionManager> connectionManager = new Supplier() {
			@Override
			public HttpClientConnectionManager get() {
				PoolingHttpClientConnectionManager connectionPool = new PoolingHttpClientConnectionManager();
		        connectionPool.setDefaultMaxPerRoute(Integer.MAX_VALUE);
		        connectionPool.setMaxTotal(Integer.MAX_VALUE);
		        return connectionPool;
			}
		};
		
		public Supplier<CredentialsProvider> credentialsProvider = (() -> new SystemDefaultCredentialsProvider());
		
		public CredentialsProvider setUserCredentials(String userName, String password) {
			this.credentialsProvider.get()
				.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
			return this.credentialsProvider.get();
		}
		
		public HttpClientBuilder getHttpClientSupplier() {
			return this.httpClientBuilder
				.setConnectionManager(this.connectionManager.get())
	    		.setDefaultSocketConfig(this.socketConfigBuilder.get().build())
	    		.setDefaultRequestConfig(this.requestConfigBuilder.get().build())
	    		.setDefaultCookieStore(this.cookieStoreBuilder.get())
	    		.setDefaultCredentialsProvider(this.credentialsProvider.get())
	    		.setDefaultCookieSpecRegistry(this.cookieSpec.get().build())
	    		.setDefaultHeaders(this.headers);
		}
		
		public CloseableHttpClient build() {
			return getHttpClientSupplier().build();
		}
	}
	
	private Builder httpClientBuilder;
	
	private CloseableHttpClient httpClient;
	
	// method choice improvement
	private final String userAgentPassword;
	
	public CommonsHttpClient() {
		this(custom(), null);
	}
	
	public CommonsHttpClient(int timeout, String userAgentPassword, boolean gzip) {
		this.httpClientBuilder = custom();
		this.httpClientBuilder.setTimeouts(timeout);
		if(gzip) {
			this.httpClientBuilder.headers.add(new BasicHeader(ACCEPT_ENCODING, DEFLATE_ENCODINGS));
		}
		
		this.userAgentPassword = userAgentPassword;
        this.httpClient = this.httpClientBuilder.build();
	}
	
	public CommonsHttpClient(Builder builder, String userAgentPassword) {
		this.httpClientBuilder = builder;
		this.userAgentPassword = userAgentPassword;
        this.httpClient = this.httpClientBuilder.build();
	}
	
	public CloseableHttpClient getHttpClient(){
		return this.httpClient;
	}

	//----------------------method implementations
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
		HttpGet method = new HttpGet(url);
		return execute(method, request.getHeaders());
	}

	public RetsHttpResponse doPost(RetsHttpRequest request) throws RetsException {
		String url = request.getUrl();
		String body = request.getHttpParameters();
		if (body == null) body = "";  // commons-httpclient 3.0 refuses to accept null entity (body)
		HttpPost method = new HttpPost(url);
		method.setEntity(new StringEntity(body, ContentType.DEFAULT_TEXT));
		
		method.setHeader("Content-Type", "application/x-www-form-urlencoded");
		return execute(method, request.getHeaders());
	}

	protected RetsHttpResponse execute(final HttpRequestBase method, Map<String,String> headers) throws RetsException {
		try {
			// add our request headers from rets
			if (headers != null) {
				for (Map.Entry<String,String> entry : headers.entrySet()) {
					method.setHeader(entry.getKey(), entry.getValue());
				}
			}
			// optional ua-auth stuff here
			if( this.userAgentPassword != null ){
			    method.setHeader(RETS_UA_AUTH_HEADER, calculateUaAuthHeader(method,getCookies()));
			}
			// try to execute the request
			HttpResponse response = this.httpClient.execute(method);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != HttpStatus.SC_OK) {
				throw new InvalidHttpStatusException(status);
			}
			return new CommonsHttpClientResponse(response, getCookies());
		} catch (Exception e) {
			throw new RetsException(e);
		}
	}
	
	protected Map<String,String> getCookies() {
		Map<String,String> cookieMap = new CaseInsensitiveTreeMap();
		if(this.httpClientBuilder.cookieStoreBuilder.get() != null) {
			return cookieMap;
		}
		for (Cookie cookie : this.httpClientBuilder.cookieStoreBuilder.get().getCookies()) {
			cookieMap.put(cookie.getName(), cookie.getValue());
		}
		return cookieMap;
	}

	protected String calculateUaAuthHeader(HttpRequestBase method, Map<String, String> cookies ) {
		final String userAgent = this.getHeaderValue(method, USER_AGENT);
		final String requestId = this.getHeaderValue(method, RETS_REQUEST_ID);
		final String sessionId = cookies.get(RETS_SESSION_ID);
		final String retsVersion = this.getHeaderValue(method, RETS_VERSION);
		String secretHash = DigestUtils.md5Hex(String.format("%s:%s",userAgent,this.userAgentPassword));
    	String pieces = String.format("%s:%s:%s:%s",secretHash,StringUtils.trimToEmpty(requestId),StringUtils.trimToEmpty(sessionId),retsVersion);
		return String.format("Digest %s", DigestUtils.md5Hex(pieces));
	}
	
	protected String getHeaderValue(HttpRequestBase method, String key){
    	Header requestHeader = method.getFirstHeader(key);
    	if( requestHeader == null ) return null;
		return requestHeader.getValue();
    }
	
	protected void updateClientConfig() {
		this.httpClient = this.httpClientBuilder.build();
	}

	@Override
	public void addDefaultHeader(String name, String value) {
		this.httpClientBuilder.headers.add(new BasicHeader(name, value));
		updateClientConfig();
	}

	@Override
	public void setUserCredentials(String userName, String password) {
		this.httpClientBuilder.setUserCredentials(userName, password);
		updateClientConfig();
	}
}
