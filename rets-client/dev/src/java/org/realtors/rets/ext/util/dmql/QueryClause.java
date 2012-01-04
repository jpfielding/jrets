package org.realtors.rets.ext.util.dmql;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * Represents the ability to join multiple SearchConditions by And or Or 
 * from the RETS DMQL specf
 */
public class QueryClause implements SearchCondition {
	
	private final BinaryOperator conjunction;
	private final SearchCondition[] searchConditions;

	public QueryClause(BinaryOperator conjunction, SearchCondition... searchConditions) {
		this.conjunction = conjunction;
		this.searchConditions = QueryUtils.clean(searchConditions);
	}
	public QueryClause(BinaryOperator conjunction, Collection<? extends SearchCondition> searchConditions) {
		this.conjunction = conjunction;
		this.searchConditions = searchConditions == null ? QueryUtils.clean(new SearchCondition[0]) : searchConditions.toArray(new SearchCondition[]{});
	}

	@Override
	public String toString() {
		if (this.searchConditions == null || this.searchConditions.length == 0) 
			return "";
		if (this.searchConditions.length == 1) 
			return this.searchConditions[0].toString();
		
		StringBuffer result = new StringBuffer();
		Iterable<SearchCondition> iterable = Lists.newArrayList(this.searchConditions);
		iterable = Iterables.filter(iterable,new Predicate<SearchCondition>(){
			public boolean apply(SearchCondition object) {
				return StringUtils.isNotBlank(object.toString());
			}});
		result.append("(");
		result.append(Joiner.on(this.conjunction.toRetsDmql()).join(iterable));
		result.append(")");
		return result.toString();
	}
	
}
