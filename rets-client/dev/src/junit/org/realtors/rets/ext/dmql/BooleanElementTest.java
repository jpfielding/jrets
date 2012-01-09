package org.realtors.rets.ext.dmql;

import org.realtors.rets.ext.dmql.BooleanElement;
import org.realtors.rets.ext.dmql.UnaryOperator;

import junit.framework.TestCase;

public class BooleanElementTest extends TestCase {

	public void testNull() {
		try {
			new BooleanElement(null);
			fail("Should not allow null argument");
		}catch(IllegalArgumentException e) {
			// expected
		}
	}
	
	public void test() {
		BooleanElement element = new BooleanElement(QueryClauseTest.listDate);
		String expected = UnaryOperator.Not.toRetsDmql()+QueryClauseTest.listDate;
		assertEquals(expected, element.toString());
	}

	public void testEmpty() {
		BooleanElement element = new BooleanElement(QueryClauseTest.emptyList);
		String expected = "";
		assertEquals(expected, element.toString());
	}
}
