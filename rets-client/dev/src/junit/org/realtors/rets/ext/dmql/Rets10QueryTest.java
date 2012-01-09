package org.realtors.rets.ext.dmql;

import java.util.LinkedList;
import java.util.List;

import org.realtors.rets.ext.dmql.DateRange;
import org.realtors.rets.ext.dmql.FieldMap;
import org.realtors.rets.ext.dmql.LookupList;
import org.realtors.rets.ext.dmql.LookupType;
import org.realtors.rets.ext.dmql.NumberRange;
import org.realtors.rets.ext.dmql.RangeList;
import org.realtors.rets.ext.dmql.Rets10Query;
import org.realtors.rets.ext.dmql.StringList;

public class Rets10QueryTest extends BaseQueryTest {

	public void testQuery() throws Exception{
		List<FieldMap> maps = new LinkedList();
		StringBuffer sb = new StringBuffer();

		this.fieldLookupList(maps,sb);
		sb.append(",");
		this.fieldStringList(maps,sb);
		sb.append(",");
		this.fieldRangeListDate(maps,sb);
		sb.append(",");
		this.fieldRangeListNumber(maps,sb);

		Rets10Query query = new Rets10Query(maps.toArray(new FieldMap[]{}));
		assertEquals(sb.toString(),query.toString());
	}
	public void testBlank() throws Exception{
		FieldMap lookups = new FieldMap("ListCat",new LookupList<String>(LookupType.OR,new String[0]));
		FieldMap strings = new FieldMap("ListDog",new StringList(new String[0]));
		FieldMap dateranges = new FieldMap("ListFrog",new RangeList(new DateRange(null,null, null)));
		FieldMap numberranges = new FieldMap("ListChicken",new RangeList(new NumberRange(null,null)));

		Rets10Query query = new Rets10Query((FieldMap)null,lookups,strings,dateranges,numberranges);
		assertEquals("",query.toString());
	}

}

