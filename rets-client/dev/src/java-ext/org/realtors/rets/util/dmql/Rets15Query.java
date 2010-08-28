package org.realtors.rets.util.dmql;

/** RETS/1.5 or later Query -- supports nested queries and other cool stuff */
public class Rets15Query {
	
	private SearchCondition searchCondition;

	/** Construct a Query from a SearchCondition */
	public Rets15Query( SearchCondition searchCondition ) {
		if (searchCondition == null) 
			throw new IllegalArgumentException("searchCondition cannot be null");
		this.searchCondition = searchCondition;
	}
	
	@Override
	public String toString(){
		return this.searchCondition.toString();
	}

}

