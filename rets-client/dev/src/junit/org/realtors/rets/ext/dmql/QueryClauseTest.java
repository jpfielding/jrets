package org.realtors.rets.ext.dmql;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.realtors.rets.ext.dmql.BinaryOperator;
import org.realtors.rets.ext.dmql.DateRange;
import org.realtors.rets.ext.dmql.Empty;
import org.realtors.rets.ext.dmql.FieldMap;
import org.realtors.rets.ext.dmql.NumberRange;
import org.realtors.rets.ext.dmql.QueryClause;
import org.realtors.rets.ext.dmql.RangeList;
import org.realtors.rets.ext.dmql.SearchCondition;
import org.realtors.rets.ext.dmql.StringList;

public class QueryClauseTest extends TestCase {

	public static final FieldMap listNum = new FieldMap("listnum", new StringList("123456"));
	public static final FieldMap listPrice = new FieldMap("listprice", new RangeList(new NumberRange(1000, 2000)));
	public static final FieldMap listDate = new FieldMap("listdate", new RangeList(new DateRange(new DateTime(), new DateTime())));
	public static final FieldMap emptyList = new FieldMap("empty", new StringList(new String[]{}));
	public static final SearchCondition nullCheck = new Empty("status", true);

	public void testSingleElement() throws Exception {
		QueryClause compound = new QueryClause(BinaryOperator.And, listNum);
		assertEquals(listNum.toString(), compound.toString());
	}
	
	public void testEmpty() throws Exception {
		QueryClause compound = new QueryClause(BinaryOperator.Or, emptyList, emptyList, listPrice, emptyList, listDate);
		String expected = "(" + listPrice + BinaryOperator.Or.toRetsDmql() + listDate + ")";
		assertEquals(expected, compound.toString());
	}
	
	public void testOr() throws Exception {
		QueryClause compound = 
			new QueryClause(BinaryOperator.Or, listNum, listDate, listPrice, nullCheck);
		String expected = "(" + listNum + BinaryOperator.Or.toRetsDmql() + listDate + BinaryOperator.Or.toRetsDmql() + listPrice + BinaryOperator.Or.toRetsDmql() + nullCheck + ")";
		assertEquals(expected, compound.toString());
	}
	
	public void testAnd() throws Exception {
		QueryClause compound = 
			new QueryClause(BinaryOperator.And, listNum, listDate, listPrice);
		String expected = "(" + listNum + BinaryOperator.And.toRetsDmql() + listDate + BinaryOperator.And.toRetsDmql() + listPrice + ")";
		assertEquals(expected, compound.toString());
	}
	
	public void testNull() throws Exception {
		assertEquals("", new QueryClause(BinaryOperator.And, (FieldMap) null).toString());
	}
	
	public void testNone() throws Exception {
		assertEquals("", new QueryClause(BinaryOperator.And, new SearchCondition[0]).toString());
	}
	
	public void testNullMember() throws Exception {
		String expected = "(" + listPrice + BinaryOperator.And.toRetsDmql() + listPrice + ")";
		assertEquals(expected, new QueryClause(BinaryOperator.And, new SearchCondition[]{listPrice, null, listPrice}).toString());
	}
	
}
