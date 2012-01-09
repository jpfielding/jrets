package org.realtors.rets.ext;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.realtors.rets.client.RetsVersion;
import org.realtors.rets.ext.transaction.MetadataProvider;


public class RetsProfile implements Serializable {
	
	private Serializable id;
	
	private String name;

	private String loginUrl;
	private RetsCredentials credentials;
	private String userAgent;
	private String userAgentPassword;
	private String hostHeaderOverride;
	private boolean gzipEnabled;
	private RetsVersion retsVersion;
	
	private long sessionTimeout;
	private int sessionConcurrentLimit;

	private long metadataTimeout;
	private MetadataProvider metadataProvider;
	private RetsMetadataFormat metadataFormat;
	private String metadataCachePath;
	private boolean useZeroMetadataPathSuffix;
	
	private boolean retsStrict;
	private int retryCount;
	private long retryDelay;

	/** used as timeout for sockets (RETS HTTP), locks (java.concurrency), etc. */
    private int waitTimeout;
    private String httpMethodDefault;


	public Serializable getId() {
		return this.id;
	}
	public void setId(Serializable id) {
		this.id = id;
	}
	public boolean isGzipEnabled() {
		return this.gzipEnabled;
	}
	public void setGzipEnabled(boolean gzipEnabled){
		this.gzipEnabled = gzipEnabled;
	}
	public String getLoginUrl() {
		return this.loginUrl;
	}
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
    public RetsCredentials getCredentials() {
        return this.credentials;
    }
    public void setCredentials(RetsCredentials credentials) {
        this.credentials = credentials;
    }
	public String getUserAgent() {
		return this.userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getUserAgentPassword() {
		return this.userAgentPassword;
	}
	public void setUserAgentPassword(String userAgentPassword) {
		this.userAgentPassword = userAgentPassword;
	}
	public long getMetadataTimeout(){
		return this.metadataTimeout;
	}
	public void setMetadataTimeout(long metadataTimeout) {
		this.metadataTimeout = metadataTimeout;
	}
	public long getSessionTimeout() {
		return this.sessionTimeout;
	}
	public void setSessionTimeout(long sessionTimeout){
		this.sessionTimeout = sessionTimeout;
	}
	public MetadataProvider getMetadataProvider() {
		return this.metadataProvider;
	}
	public void setMetadataProvider(MetadataProvider metadataProvider){
		this.metadataProvider = metadataProvider;
	}
	public int getSessionConcurrentLimit() {
		return this.sessionConcurrentLimit;
	}
	public void setSessionConcurrentLimit(int sessionConcurrentLimit){
		this.sessionConcurrentLimit = sessionConcurrentLimit;
	}
	public RetsMetadataFormat getMetadataFormat() {
        return this.metadataFormat;
    }
    public void setMetadataFormat(RetsMetadataFormat metadataFormat) {
        this.metadataFormat = metadataFormat;
    }
    public boolean isUseZeroMetadataPathSuffix() {
        return this.useZeroMetadataPathSuffix;
    }
    public void setUseZeroMetadataPathSuffix(boolean useZeroMetadataPathSuffix) {
        this.useZeroMetadataPathSuffix = useZeroMetadataPathSuffix;
    }
    public int getWaitTimeout() {
		return this.waitTimeout;
	}
	public void setWaitTimeout(int timeout) {
		this.waitTimeout = timeout;
	}
	public void setRetryDelay(long retryDelay) {
		this.retryDelay = retryDelay;
	}
	public long getRetryDelay() {
		return this.retryDelay;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public int getRetryCount() {
		return this.retryCount;
	}
	public String getHttpMethodDefault(){
		return this.httpMethodDefault;
	}
	public void setHttpMethodDefault(String httpMethodDefault) {
		this.httpMethodDefault = httpMethodDefault;
	}
	public void setRetsVersion(RetsVersion retsVersion) {
		this.retsVersion = retsVersion;
	}
    public RetsVersion getRetsVersion() {
        return this.retsVersion;
    }
	public void setMetadataCachePath(String metadataCachePath) {
		this.metadataCachePath = metadataCachePath;
	}
	public String getMetadataCachePath(){
		return this.metadataCachePath;
	}
	public boolean isRetsStrict() {
		return this.retsStrict;
	}
	public void setRetsStrict(boolean strictRets) {
		this.retsStrict = strictRets;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHostHeaderOverride() {
		return this.hostHeaderOverride;
	}
	public void setHostHeaderOverride(String hostHeaderOverride) {
		this.hostHeaderOverride = hostHeaderOverride;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.name)
				.append(this.retsStrict)
				.append(this.waitTimeout)
				.append(this.httpMethodDefault)
				.append(this.retsVersion)
				.append(this.retryCount)
				.append(this.retryDelay)
				.append(this.metadataTimeout)
				.append(this.metadataCachePath)
				.append(this.loginUrl)
				.append(this.credentials)
				.append(this.userAgent)
				.append(this.metadataProvider)
				.append(this.metadataFormat)
				.append(this.sessionConcurrentLimit)
				.append(this.useZeroMetadataPathSuffix)
				.append(this.hostHeaderOverride)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(!(obj instanceof RetsProfile)) return false;
		
		RetsProfile other = (RetsProfile) obj;
		return new EqualsBuilder()
				.append(this.name,other.name)
				.append(this.retsStrict,other.retsStrict)
				.append(this.waitTimeout, other.waitTimeout)
				.append(this.httpMethodDefault, other.httpMethodDefault)
				.append(this.retsVersion, other.retsVersion)
				.append(this.retryCount, other.retryCount)
				.append(this.retryDelay, other.retryDelay)
				.append(this.metadataTimeout, other.metadataTimeout)
				.append(this.metadataCachePath, other.metadataCachePath)
				.append(this.loginUrl, other.loginUrl)
				.append(this.credentials, other.credentials)
				.append(this.userAgent, other.userAgent)
				.append(this.userAgentPassword, other.userAgentPassword)
				.append(this.metadataProvider, other.metadataProvider)
				.append(this.metadataFormat, other.metadataFormat)
				.append(this.sessionConcurrentLimit, other.sessionConcurrentLimit)
				.append(this.useZeroMetadataPathSuffix, other.useZeroMetadataPathSuffix)
				.append(this.hostHeaderOverride,other.hostHeaderOverride)
			.isEquals();
	}
}
