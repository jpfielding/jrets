package org.realtors.rets.util.dmql;

import java.util.LinkedList;
import java.util.List;

public class Rets15QueryTest extends BaseQueryTest {

	public void testQuery() throws Exception{
		List maps = new LinkedList();
		StringBuffer sb = new StringBuffer();
		
		sb.append("(");
		this.fieldLookupList(maps,sb);
		sb.append(",");
		this.fieldStringList(maps,sb);
		sb.append(",");
		this.fieldRangeListDate(maps,sb);
		sb.append(",");
		this.fieldRangeListNumber(maps,sb);
		sb.append(")");

		Rets15Query query = new Rets15Query(new QueryClause(BinaryOperator.And, maps));
		assertEquals(sb.toString(),query.toString());		
	}
	
	public void testBlank() throws Exception{
		FieldMap emptyLookups = new FieldMap("ListCat",new LookupList<String>(LookupType.OR,new String[0]));
		FieldMap emptyStrings = new FieldMap("ListDog",new StringList(new String[0]));
		FieldMap emptyDateRanges = new FieldMap("ListFrog",new RangeList(new DateRange(null,null, null)));
		FieldMap emptyNumberRanges = new FieldMap("ListChicken",new RangeList(new NumberRange(null,null)));

		Rets15Query query = new Rets15Query(new QueryClause(BinaryOperator.And, (FieldMap)null,emptyLookups,emptyStrings,emptyDateRanges,emptyNumberRanges));
		assertEquals("",query.toString());		
	}
	
}
