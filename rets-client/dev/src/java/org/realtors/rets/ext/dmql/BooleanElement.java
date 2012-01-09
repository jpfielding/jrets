package org.realtors.rets.ext.dmql;

import org.apache.commons.lang.StringUtils;

public class BooleanElement implements SearchCondition {

	private final SearchCondition searchCondition;

	public BooleanElement (SearchCondition searchCondition) {
		if (searchCondition == null) 
			throw new IllegalArgumentException("searchCondition cannot be null");
		this.searchCondition = searchCondition;
	}
	
	@Override
	public String toString() {
		if (this.searchCondition == null || StringUtils.isBlank(this.searchCondition.toString()))
			return "";
		return UnaryOperator.Not.toRetsDmql() + this.searchCondition.toString();
	}
	
}
