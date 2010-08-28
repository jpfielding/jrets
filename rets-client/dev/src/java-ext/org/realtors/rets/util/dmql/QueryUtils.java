package org.realtors.rets.util.dmql;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

class QueryUtils {

	public static SearchCondition[] clean(SearchCondition... incoming) {
		if (incoming == null)
			return new SearchCondition[0];
		Iterable<SearchCondition> filter = Iterables.filter(Lists.newArrayList(incoming), new Predicate<SearchCondition>(){
			public boolean apply(SearchCondition searchCondition) {
				return searchCondition != null && !StringUtils.isEmpty(searchCondition.toString());
			}});
		return Iterables.toArray(filter, SearchCondition.class);
	}


}
