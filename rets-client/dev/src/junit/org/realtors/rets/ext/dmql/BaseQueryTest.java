package org.realtors.rets.ext.dmql;

import java.util.List;

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

public abstract class BaseQueryTest extends TestCase {

	public void fieldLookupList(List maps, StringBuffer sb) throws Exception {
		String name = "Ownership";
		String[] values = new String[]{"Condo","Coop"};
		LookupList list = new LookupList<String>(LookupType.OR,values);
		FieldMap map = new FieldMap(name,list);
		maps.add(map);
		sb.append("(Ownership=|Condo,Coop)");
	}
	public void fieldStringList(List maps, StringBuffer sb) throws Exception {
		String name = "ListAgentLastName";
		String[] values = new String[]{"Sm?th","Thom*"};
		StringList list = new StringList(values);
		FieldMap map = new FieldMap(name,list);
		maps.add(map);
		sb.append("(ListAgentLastName=Sm?th,Thom*)");
	}
	public void fieldRangeListDate(List maps, StringBuffer sb) throws Exception {
		String name = "ListDate";
		DateTime to = new DateTime();
		DateTime from = to.minusYears(5);
		DateRange range = new DateRange(from,to);
		RangeList list = new RangeList(range);
		FieldMap map = new FieldMap(name,list);
		maps.add(map);
		sb.append("(ListDate="+ from.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +"-"+ to.toMutableDateTime(DateTimeZone.forID("GMT")).toString(range.getFormat()) +")");
	}
	public void fieldRangeListNumber(List maps, StringBuffer sb) throws Exception {
		String name = "ListPrice";
		Number from = new Integer(250000);
		Number to = new Integer(500000);
		NumberRange range = new NumberRange(from,to);
		RangeList list = new RangeList(range);
		FieldMap map = new FieldMap(name,list);
		maps.add(map);
		sb.append("(ListPrice="+ range.getFormater().format(from) +"-"+ range.getFormater().format(to) +")");
	}
}
