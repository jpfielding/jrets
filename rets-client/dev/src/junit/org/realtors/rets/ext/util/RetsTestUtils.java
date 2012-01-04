package org.realtors.rets.ext.util;
import java.io.Serializable;
import java.util.Properties;

import org.realtors.rets.ext.util.RetsClient;
import org.realtors.rets.ext.util.RetsClientConfig;

public class RetsTestUtils {
	private RetsTestUtils() { super(); }
	
	public static final String TEST_RETS_SERVICE_NAME = "TestRetsService";
	public static final Serializable TEST_RETS_SERVICE_ID = TEST_RETS_SERVICE_NAME;
	
	public static RetsClient createRetsClient() {
		RetsClientConfig retsClientConfig = RetsTestUtils.getRetsClientProperties();
        return retsClientConfig.createClient();
	}
	
	private static RetsClientConfig getRetsClientProperties() {
		Properties rets = new Properties();
		rets.setProperty("rets.user.agent","Threewide/1.0");
		rets.setProperty("rets.login.url","http://rmls.rexplorer.net/cgi-bin/REDISREA/Login");
		rets.setProperty("rets.username","THREEWID");
		rets.setProperty("rets.password","THREEW");
		rets.setProperty("rets.timeout","60000");
		// Number of compact decoded rows to fetch at a time
		rets.setProperty("search.fetchsize","100");
		// Sax stream buffer size 
		rets.setProperty("search.buffersize","10000");
		// Number of keys that can have objects requested for at once 
		// (San Antonio limits this to somewhere between 25 and 100) 
		rets.setProperty("getobject.fetchsize","25");
		// Object stream buffer size
		rets.setProperty("getobject.buffersize","10000");
		rets.setProperty("rets.cdataescape","true");
		rets.setProperty("rets.logprotocol","true");
		rets.setProperty("rets.useposts","false");
		rets.setProperty("rets.useincrementalmetadata","true");
		rets.setProperty("rets.metadata.format","Compact");
		// FNIS error
		rets.setProperty("rets.version","RETS/1.0");
		rets.setProperty("rets.search.result.format","CompactDecoded");
		return new RetsClientConfig(rets, "junit");
	}
}
