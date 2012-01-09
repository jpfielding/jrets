package org.realtors.rets.ext.dmql;

import java.util.Calendar;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.realtors.rets.ext.dmql.DateRange;
import org.realtors.rets.ext.dmql.FieldMap;
import org.realtors.rets.ext.dmql.LookupList;
import org.realtors.rets.ext.dmql.LookupType;
import org.realtors.rets.ext.dmql.NumberRange;
import org.realtors.rets.ext.dmql.RangeList;
import org.realtors.rets.ext.dmql.StringList;

public class FieldMapTest extends TestCase {

	public FieldMapTest() {
		super();
	}
	public FieldMapTest(String arg0) {
		super(arg0);
	}

	public void testFieldLookupList()throws Exception{
		String name = "Ownership";
		String[] values = new String[]{"Condo","Coop"};
		LookupList list = new LookupList<String>(LookupType.OR,values);
		FieldMap map = new FieldMap(name,list);
		{
			list.setEquality(LookupType.AND);
			assertTrue(map.toString(),map.toString().equals("(Ownership=+Condo,Coop)"));
		}
		{
			list.setEquality(LookupType.OR);
			assertTrue(map.toString(),map.toString().equals("(Ownership=|Condo,Coop)"));
		}
		{
			list.setEquality(LookupType.NOT);
			assertTrue(map.toString(),map.toString().equals("(Ownership=~Condo,Coop)"));
		}
	}
	public void testFieldStringList()throws Exception{
		String name = "Ownership";
		String[] values = new String[]{"Condo*","*Coop","?ental"};
		StringList list = new StringList(values);
		FieldMap map = new FieldMap(name,list);
		assertTrue(map.toString(),map.toString().equals("(Ownership=Condo*,*Coop,?ental)"));
	}
	public void testFieldRangeListDate()throws Exception{
		String name = "ListDate";
		Calendar calendar = Calendar.getInstance();
		DateTime to = new DateTime(calendar.getTime());
		calendar.add(Calendar.YEAR,-5);
		DateTime from = new DateTime(calendar.getTime());
		{
			DateRange range = new DateRange(from,to);
			RangeList list = new RangeList(range);
			FieldMap map = new FieldMap(name,list);
			assertTrue(map.toString(),map.toString().equals("(ListDate="+ from.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +"-"+ to.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +")"));
		}
		{
			DateRange range = new DateRange(from,null);
			RangeList list = new RangeList(range);
			FieldMap map = new FieldMap(name,list);
			assertTrue(map.toString(),map.toString().equals("(ListDate="+ from.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +"+"+")"));
		}
		{
			DateRange range = new DateRange(null,to);
			RangeList list = new RangeList(range);
			FieldMap map = new FieldMap(name,list);
			assertTrue(map.toString(),map.toString().equals("(ListDate="+ to.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +"-"+")"));
		}
	}
	public void testFieldRangeListNumber()throws Exception{
		String name = "ListPrice";
		Number from = new Integer(250000);
		Number to = new Integer(500000);
		{
			NumberRange range = new NumberRange(from,to);
			RangeList list = new RangeList(range);
			FieldMap map = new FieldMap(name,list);
			assertTrue(map.toString(),map.toString().equals("(ListPrice="+ range.getFormater().format(from) +"-"+ range.getFormater().format(to) +")"));
		}
		{
			NumberRange range = new NumberRange(from,null);
			RangeList list = new RangeList(range);
			FieldMap map = new FieldMap(name,list);
			assertTrue(map.toString(),map.toString().equals("(ListPrice="+ range.getFormater().format(from) +"+"+ ")"));
		}
		{
			NumberRange range = new NumberRange(null,to);
			RangeList list = new RangeList(range);
			FieldMap map = new FieldMap(name,list);
			assertTrue(map.toString(),map.toString().equals("(ListPrice="+ range.getFormater().format(to) +"-"+ ")"));
		}
	}
}
