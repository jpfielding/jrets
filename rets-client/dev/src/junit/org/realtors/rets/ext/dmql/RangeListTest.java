package org.realtors.rets.ext.dmql;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.realtors.rets.ext.dmql.DateRange;
import org.realtors.rets.ext.dmql.NumberRange;
import org.realtors.rets.ext.dmql.RangeList;

public class RangeListTest extends TestCase {

	public RangeListTest() {
		super();
	}
	public RangeListTest(String arg0) {
		super(arg0);
	}

	public void testDate()throws Exception{
		DateTime to = new DateTime();
		DateTime from = to.minusYears(5);

		{
			DateRange range = new DateRange(from,to);
			RangeList list = new RangeList(range);
			assertTrue(list.toString(),list.toString().equals( from.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +"-"+ to.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) ));
		}
		{
			DateRange range = new DateRange(from,null);
			RangeList list = new RangeList(range);
			assertTrue(list.toString(),list.toString().equals( from.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +"+" ));
		}
		{
			DateRange range = new DateRange(null,to);
			RangeList list = new RangeList(range);
			assertTrue(list.toString(),list.toString().equals( to.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +"-" ));
		}
	}
	public void testNumber()throws Exception{
		Number from = new Integer(250000);
		Number to = new Integer(500000);
		{
			NumberRange range = new NumberRange(from,to);
			RangeList list = new RangeList(range);
			assertTrue(list.toString(),list.toString().equals( range.getFormater().format(from) +"-"+ range.getFormater().format(to) ));
		}
		{
			NumberRange range = new NumberRange(from,null);
			RangeList list = new RangeList(range);
			assertTrue(list.toString(),list.toString().equals( range.getFormater().format(from) +"+" ));
		}
		{
			NumberRange range = new NumberRange(null,to);
			RangeList list = new RangeList(range);
			assertTrue(list.toString(),list.toString().equals(  range.getFormater().format(to) +"-" ));
		}
	}
}

