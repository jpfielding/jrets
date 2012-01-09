package org.realtors.rets.ext;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.realtors.rets.client.CompactRowPolicy;
import org.realtors.rets.client.RetsVersion;
import org.realtors.rets.ext.transaction.MetadataProvider;
import org.realtors.rets.ext.util.RetsSearchInfo;


public class RetsClientConfig {
	public interface Property{
		public String getName();
		public String getProperty();
		public String getDefault();
		public String getDescription();
	}
	// RETS CREDENTIALS
	public static final Property LOGIN_URL = new SimpleProperty("Login Url","rets.login.url", null, "The url for RETS Login");
	public static final Property USERNAME = new SimpleProperty("Username","rets.username", null, "The username for RETS Login");
	public static final Property PASSWORD = new SimpleProperty("Password","rets.password", null, "The password for RETS Login");
	public static final Property USER_AGENT = new SimpleProperty("User Agent","rets.user.agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)", "The user agent to access the RETS server (may require user agent password)");
	public static final Property USER_AGENT_PASSWORD = new SimpleProperty("User Agent Password","rets.user.agent.password", null, "The password for the given user agent header");
	public static final Property RETS_VERSION = new SimpleProperty("Protocol Version","rets.version", "1.5", "The RETS version that this conversation will be conducted in");
	public static final Property[] LOGIN = new Property[]{ LOGIN_URL, USERNAME, PASSWORD, USER_AGENT, USER_AGENT_PASSWORD, RETS_VERSION };
	// GENERAL CONFIG OPTIONS
	public static final Property SESSION_TIMEOUT = new SimpleProperty("Session Timeout","rets.session.timeout", TimeUnit.MINUTES.toMillis(1)+"", "How long until a rets session should be rechecked staleness");
	public static final Property SESSION_CONCURRENT_LIMIT = new SimpleProperty("Concurrent Session Limit","rets.session.concurrent.limit", "3", "the number of concurrent accesses to a server");
	public static final Property RETRY_DELAY = new SimpleProperty("Retry Delay","rets.retry.delay", "500", "Number of millis to wait between the retry an individual rets transaction");
	public static final Property RETRY_COUNT = new SimpleProperty("Retry Count","rets.retry.count", "3", "Number of attempts to retry an individual rets transaction (should be concurrent session limit + 1)");
	public static final Property STRICT = new SimpleProperty("Strict RETS Spec","rets.strict", "false", "Whether a strict RETS interpretation should be used");
	public static final Property GZIP = new SimpleProperty("GZip","rets.gzip", "false", "Whether gzip is accepted on RETS responses");
	public static final Property TIMEOUT = new SimpleProperty("Transaction Timeout","rets.timeout", TimeUnit.MINUTES.toMillis(1)+"", "the time before an individual rets transaction times out");
	public static final Property RETS_HTTP_METHOD = new SimpleProperty("HTTP Method","rets.httpmethod.default", "GET", "Which type of http request gets sent to the RETS server");
	public static final Property RETS_HTTP_HOST_HEADER = new SimpleProperty("HTTP Host Header","rets.host.header.override", null, "Force the http host header to this value (useful for servers that may be bounced off of another box, but use the host header to distinguish between app instances)");
	public static final Property[] GENERAL_CONFIG_OPTIONS = new Property[]{ SESSION_TIMEOUT, SESSION_CONCURRENT_LIMIT, RETRY_DELAY, RETRY_COUNT, STRICT, GZIP, TIMEOUT, RETS_HTTP_METHOD, RETS_HTTP_HOST_HEADER };
	// METDATER STUFF
	public static final Property METADATA_CACHE_PATH = new SimpleProperty("Cache Path","rets.metadata.cache.path", System.getProperty("java.io.tmpdir")+"/rets/metadata", "The filesystem path for caching rets metadata");
	public static final Property METADATA_TIMEOUT = new SimpleProperty("Timeout","rets.metadata.timeout", TimeUnit.MINUTES.toMillis(1)+"", "How long until metadata should be rechecked staleness");
	public static final Property METADATA_PROVIDER = new SimpleProperty("Provider","rets.metadata.provider", "CACHED", "Which Rets Metadata Provider to use {CACHED, FULL, INCREMENTAL}");
	public static final Property METADATA_FORMAT = new SimpleProperty("Format","rets.metadata.format", "Compact", "Which metadat format to request {Compact, StandardXML}");
	public static final Property METADATA_ZERO_PATH_SUFFIX = new SimpleProperty("Zero Path Suffix","rets.metadata.usezeropathsuffix", "true", "Whether or not to use zero path suffix {true, false}");
	public static final Property[] METADATA = new Property[]{ METADATA_CACHE_PATH, METADATA_TIMEOUT, METADATA_PROVIDER, METADATA_FORMAT, METADATA_ZERO_PATH_SUFFIX };
	// SEARCH STUFF
	public static final Property BAD_COMPACT_ROW_POLICY = new SimpleProperty("Compact Bad Row Policy","rets.search.result.compact.rowpolicy", "DEFAULT", "The action to take on bad search result rows");
	/** this (windows-1252) is known to conflict with the utf-8 encoding when documents are exactly 8096 in size */
	public static final Property XML_CDATA_ESCAPE = new SimpleProperty("XML Cdata Escape","rets.cdataescape", "false", "Whether the data contains unescaped xml (ie, '&'), and requires escaping to read by an XML parser");
	public static final Property SEARCH_RESULT_FORMAT = new SimpleProperty("Result Format","rets.search.result.format", "CompactDecoded", "The format of the search results we want");
	public static final Property SEARCH_LIMIT = new SimpleProperty("Limit","rets.search.limit", "0", "The number of rows the server limits us to on a single query.");
	public static final Property SEARCH_FORCE_LIMIT = new SimpleProperty("Force Limit","rets.search.forcelimitnone", "true", "If the rets server has a limit, but allows us to defer it for this query");
	public static final Property SEARCH_OFFSET = new SimpleProperty("Paging Offset","rets.search.paging.offset", "1", "Paging may require us to modify the offset");
	public static final Property SEARCH_TIMEZONE = new SimpleProperty("Timezone","rets.search.datetime.zone", "GMT", "This 'should' NOT  be necessary, as RETS specifically states all datetime queries are in GMT");
	public static final Property SEARCH_RESULT_TIMEZONE = new SimpleProperty("Results Timezone","rets.result.datetime.zone", TimeZone.getDefault().getID(), "The timezone that search results are returned in");
	public static final Property SEARCH_BUFFERSIZE = new SimpleProperty("Buffersize","search.buffersize", "8096", "");
	public static final Property RETS_SEARCH_OK_LATE_REPLY = new SimpleProperty("OK Late Reply","rets.search.oklatereplys", "", "provides a set of reply codes that are known to occur but not meant to crash a streaming search (@see fnis paragon)");
	public static final Property[] SEARCH = new Property[]{ BAD_COMPACT_ROW_POLICY, XML_CDATA_ESCAPE, SEARCH_RESULT_FORMAT, SEARCH_LIMIT, SEARCH_FORCE_LIMIT, SEARCH_OFFSET, SEARCH_TIMEZONE, SEARCH_RESULT_TIMEZONE, SEARCH_BUFFERSIZE, RETS_SEARCH_OK_LATE_REPLY };
	
	private String retsServiceName;
	private Properties properties;
    
	public RetsClientConfig(Properties properties, String retsServiceName) {
		this.retsServiceName = retsServiceName;
		this.properties = properties;
	}
	
	public Properties getProperties() {
		return this.properties;
	}
	public String getRetsServiceName() {
		return this.retsServiceName;
	}

	private String getProperty(Property property){
        String propertyValue = this.properties.getProperty(property.getProperty());
        if(StringUtils.isBlank(propertyValue)) return property.getDefault();
        return propertyValue;
	}

    public TimeZone getSearchDateTimeZone() {
        return TimeZone.getTimeZone(getProperty(SEARCH_TIMEZONE));
	}
    public TimeZone getResultDateTimeZone() {
        return TimeZone.getTimeZone(getProperty(SEARCH_RESULT_TIMEZONE));
	}
    
    public RetsClient createClient() {
    	Logger.getLogger(this.getClass()).info("creating rets client");
		return new RetsClient(createRetsProfile());
	}

    public RetsSearchInfo createSearchInfo(){
		RetsSearchInfo searchInfo = new RetsSearchInfo();
		searchInfo.setCdataEscape(new Boolean(getProperty(XML_CDATA_ESCAPE)).booleanValue());
		searchInfo.setForceLimitNone(new Boolean(getProperty(SEARCH_FORCE_LIMIT)).booleanValue());
		searchInfo.setLimit(Integer.parseInt(getProperty(SEARCH_LIMIT)));
		searchInfo.setOffset(NumberUtils.toInt(getProperty(SEARCH_OFFSET)));
		searchInfo.setSearchBufferSize(Integer.parseInt(getProperty(SEARCH_BUFFERSIZE)));
		Set<Integer> okLateReplyCodes = new HashSet<Integer>();
		for(String replyCode : StringUtils.split(getProperty(RETS_SEARCH_OK_LATE_REPLY),",")) okLateReplyCodes.add(Integer.parseInt(replyCode));
		searchInfo.setValidDelayedReplyCodes(okLateReplyCodes);
		searchInfo.setSearchResultFormat(RetsSearchResultFormat.valueOf(getProperty(SEARCH_RESULT_FORMAT)));
		try {
			CompactRowPolicy compactRowPolicy = (CompactRowPolicy) CompactRowPolicy.class.getField(getProperty(BAD_COMPACT_ROW_POLICY)).get(null);
			searchInfo.setCompactRowPolicy(compactRowPolicy);
		} catch (Exception e) {
			throw new RuntimeException(String.format("dont recognize compact row policy '%s'",getProperty(BAD_COMPACT_ROW_POLICY)),e);
		}
		return searchInfo;
	}
	
    public RetsProfile createRetsProfile() {
		RetsProfile retsProfile = new RetsProfile();
		// connection properties
		retsProfile.setCredentials(new RetsCredentials(getProperty(USERNAME), getProperty(PASSWORD)));
		retsProfile.setLoginUrl(getProperty(LOGIN_URL));
		retsProfile.setGzipEnabled(new Boolean(getProperty(GZIP)).booleanValue());
		retsProfile.setHostHeaderOverride(getProperty(RETS_HTTP_HOST_HEADER));
		retsProfile.setHttpMethodDefault(getProperty(RETS_HTTP_METHOD));
		retsProfile.setUserAgent(getProperty(USER_AGENT));
		retsProfile.setUserAgentPassword(getProperty(USER_AGENT_PASSWORD));
		retsProfile.setUseZeroMetadataPathSuffix(new Boolean(getProperty(METADATA_ZERO_PATH_SUFFIX)).booleanValue());
		retsProfile.setWaitTimeout(Integer.parseInt(getProperty(TIMEOUT)));
		retsProfile.setRetsVersion(RetsVersion.getVersion(getProperty(RETS_VERSION)));
		// interaction properties
		retsProfile.setRetsStrict(new Boolean(getProperty(STRICT)).booleanValue());
		retsProfile.setRetryCount(Integer.parseInt(getProperty(RETRY_COUNT)));
		retsProfile.setRetryDelay(Long.parseLong(getProperty(RETRY_DELAY)));
		// session/metadata properties
		retsProfile.setMetadataCachePath(getProperty(METADATA_CACHE_PATH));
		retsProfile.setMetadataFormat(RetsMetadataFormat.valueOf(getProperty(METADATA_FORMAT)));
		retsProfile.setMetadataProvider(MetadataProvider.valueOf(getProperty(METADATA_PROVIDER)));
		retsProfile.setMetadataTimeout(new Long(getProperty(METADATA_TIMEOUT)).longValue());
		retsProfile.setSessionTimeout(new Long(getProperty(SESSION_TIMEOUT)).longValue());
		retsProfile.setSessionConcurrentLimit(Math.max(1,Integer.parseInt(getProperty(SESSION_CONCURRENT_LIMIT))));

		retsProfile.setName(this.retsServiceName);
		return retsProfile;
	}

    @Override
    public String toString() {
    	return this.retsServiceName;
    }
}


class SimpleProperty implements RetsClientConfig.Property{
	private final String name;
	private final String defaultValue;
	private final String description;
	private final String property;

	public SimpleProperty(String name, String property, String defaultValue, String description){
		this.name = name;
		this.property = property;
		this.defaultValue = defaultValue;
		this.description = description;
		
	}

	public String getDefault() {
		return this.defaultValue;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public String getProperty() {
		return this.property;
	}
}