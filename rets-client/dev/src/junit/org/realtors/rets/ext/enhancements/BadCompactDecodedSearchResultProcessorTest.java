package org.realtors.rets.ext.enhancements;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.realtors.rets.client.SearchResultSet;
import org.realtors.rets.client.StreamingSearchResultProcessor;
import org.realtors.rets.ext.enhancements.BadCompactDecodedSearchResultProcessor;
import org.xml.sax.SAXParseException;

public class BadCompactDecodedSearchResultProcessorTest extends TestCase {
	private static final Logger logger = Logger.getLogger(BadCompactDecodedSearchResultProcessorTest.class);

	public void test() throws Exception {
		try {
			BadCompactDecodedSearchResultProcessor p = 
				new BadCompactDecodedSearchResultProcessor(
						new StreamingSearchResultProcessor(1), 
						1024, true);
			
			SearchResultSet srs = p.parse(getClass().getResourceAsStream("bad-xml-char.xml"));
			
			while(srs.hasNext()){
				srs.next();
			}
		}catch(Exception e){
			handle(e);
		}
	}

	public void testNefmls() throws Exception {
		try {
			BadCompactDecodedSearchResultProcessor p = 
				new BadCompactDecodedSearchResultProcessor(
						new StreamingSearchResultProcessor(1), 
						1024, true);
			
			SearchResultSet srs = p.parse(getClass().getResourceAsStream("nefmls.badcharsample.txt"));
			
			while(srs.hasNext()){
				srs.next();
			}
		}catch(Exception e){
			handle(e);
		}
	}
	
	private void handle(Throwable e) throws Exception {
		if (e instanceof SAXParseException){
			e.printStackTrace();
			logger.info("Line number: " + ((SAXParseException)e).getLineNumber());
			logger.info("Clmn number: " + ((SAXParseException)e).getColumnNumber());
			return;
		}
		if(e == null){
			logger.info("Unknown cause");
			return;
		}
		handle(e.getCause());
		throw new Exception("Failed due to: " + e);
	}
}
