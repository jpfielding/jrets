package org.realtors.rets.retsexplorer;

import java.util.Properties;

import org.realtors.rets.util.RetsClientConfig;

public class SampleConfigs {

	public static Properties mris(){
		Properties conn = new Properties();
		// @see RetsClientConfig for definitions and defaults
		conn.setProperty(RetsClientConfig.USER_AGENT.getProperty(),"MRIS Conduit/1.0");
		conn.setProperty(RetsClientConfig.LOGIN_URL.getProperty(),"http://cornerstone.mris.com:6103/platinum/login");
	
		// TEST SERVER
		//conn.setProperty(RetsClientConfig.LOGIN_URL.getProperty(),"http://ptest.mris.com:6103/ptest/login");
		
		conn.setProperty(RetsClientConfig.RETS_VERSION.getProperty(),"RETS/1.5");
		conn.setProperty(RetsClientConfig.METADATA_PROVIDER.getProperty(),"INCREMENTAL");
		//conn.setProperty(RetsClientConfig.METADATA_FORMAT.getProperty(),"StandardXML");
		conn.setProperty(RetsClientConfig.METADATA_ZERO_PATH_SUFFIX.getProperty(),"false");
		// if you change this, make sure you update at least the diff time for jobs in scheduled entry
		conn.setProperty(RetsClientConfig.SEARCH_TIMEZONE.getProperty(),"GMT");
		conn.setProperty(RetsClientConfig.SEARCH_RESULT_TIMEZONE.getProperty(),"America/New_York");
		return conn;
	}
	
	public static Properties taar(){
		Properties conn = new Properties();
		// @see RetsClientConfig for definitions and defaults
		conn.setProperty(RetsClientConfig.LOGIN_URL.getProperty(),"http://taar.rets.fnismls.com/rets/fnisrets.aspx/taar/login");
		conn.setProperty(RetsClientConfig.RETS_VERSION.getProperty(),"RETS/1.5");
		// if you change this, make sure you update at least the diff time for jobs in scheduled entry
		conn.setProperty(RetsClientConfig.SEARCH_TIMEZONE.getProperty(),"America/New_York");
		conn.setProperty(RetsClientConfig.XML_CDATA_ESCAPE.getProperty(),"true");
		// fnis paragon needs this as it sends errors at the end of the stream
		conn.setProperty(RetsClientConfig.RETS_SEARCH_OK_LATE_REPLY.getProperty(),"20203");
		conn.setProperty(RetsClientConfig.SEARCH_LIMIT.getProperty(),"2500");
		return conn;
	}
	
}