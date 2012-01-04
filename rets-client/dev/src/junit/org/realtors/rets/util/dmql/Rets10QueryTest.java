package org.realtors.rets.util.dmql;

import java.util.LinkedList;
import java.util.List;

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

