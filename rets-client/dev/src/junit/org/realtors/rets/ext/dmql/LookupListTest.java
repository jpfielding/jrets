package org.realtors.rets.ext.dmql;

import org.realtors.rets.ext.dmql.LookupList;
import org.realtors.rets.ext.dmql.LookupType;

import junit.framework.TestCase;

public class LookupListTest extends TestCase {

	public void testAnd()throws Exception{
		LookupList list = new LookupList<String>(LookupType.AND,new String[]{"Sold,Rented"});
		assertTrue(list.toString(),list.toString().equals("+Sold,Rented"));
	}
	public void testOr()throws Exception{
		LookupList list = new LookupList<String>(LookupType.OR,new String[]{"Active,Sold"});
		assertTrue(list.toString(),list.toString().equals("|Active,Sold"));
	}
	public void testNot()throws Exception{
		LookupList list = new LookupList<String>(LookupType.NOT,new String[]{"Closed,Open"});
		assertTrue(list.toString(),list.toString().equals("~Closed,Open"));
	}
	public void testEmpty()throws Exception{
		LookupList list = new LookupList<String>(LookupType.NOT,new String[]{});
		assertTrue(list.toString(),list.toString().equals(""));
	}
}
