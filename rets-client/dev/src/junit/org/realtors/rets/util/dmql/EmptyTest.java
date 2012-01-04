package org.realtors.rets.util.dmql;

import junit.framework.TestCase;

public class EmptyTest extends TestCase {

	public void testNull() {
		assertEquals("(field=.EMPTY.)",new Empty("field", true).toString());
	}
	
	public void testNotNull() {
		assertEquals("~(field=.EMPTY.)",new Empty("field", false).toString());
	}
	
}
