package org.realtors.rets.util.dmql;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class LookupList<T> extends QueryList<T> {
	private LookupType lookupType;
	
	public LookupList() {
		this(LookupType.OR, (T[]) null);
	}
	
	public LookupList(T ... values) {
		this(LookupType.OR, values);
	}
	
	public LookupList(LookupType type, T ... values) {
		super(values);
		this.lookupType = type;
	}
	
	public LookupList(LookupType type, List<T> values) {
		super(values);
		this.lookupType = type;
	}

	public LookupType getEquality() {
		return this.lookupType;
	}
	
	public void setEquality(LookupType type) {
		assert type != null : "LookupType cannot be null.";
		this.lookupType = type;
	}
	
	@Override
	public String toString(){
		String lookup = super.toString();
		if(StringUtils.isBlank(lookup))
			return "";
		return this.lookupType.getOperator() + lookup;
	}
}
