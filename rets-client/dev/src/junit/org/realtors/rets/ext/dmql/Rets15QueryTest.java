package org.realtors.rets.ext.dmql;

import java.util.LinkedList;
import java.util.List;

import org.realtors.rets.ext.dmql.BinaryOperator;
import org.realtors.rets.ext.dmql.DateRange;
import org.realtors.rets.ext.dmql.FieldMap;
import org.realtors.rets.ext.dmql.LookupList;
import org.realtors.rets.ext.dmql.LookupType;
import org.realtors.rets.ext.dmql.NumberRange;
import org.realtors.rets.ext.dmql.QueryClause;
import org.realtors.rets.ext.dmql.RangeList;
import org.realtors.rets.ext.dmql.Rets15Query;
import org.realtors.rets.ext.dmql.StringList;

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
