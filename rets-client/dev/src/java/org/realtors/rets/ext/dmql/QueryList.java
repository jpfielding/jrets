package org.realtors.rets.ext.dmql;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;

public abstract class QueryList<T> implements FieldValue, Serializable {
	private List<T> values;
	
	public QueryList() {
	    this((T[]) null);
	}
	
	public QueryList(T ... values) {
		this.setValues(values);
	}
	
	public QueryList(List<T> values) {
		this.values = values;
	}

	public void setValues(T ... values) {
		// the hibernate way
		this.getValues().clear();
		this.getValues().addAll(values == null ? new LinkedList<T>() : Arrays.asList(values));
	}
	
	public List<T> getValues() {
		// the hibernate way
	    return this.values == null ? this.values = new LinkedList<T>() : this.values;
	}
	
	@Override
	public String toString(){
		List prunedList = DmqlUtils.prune(this.values);
		return Joiner.on(",").join(prunedList);
	}

    @Override
	public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof QueryList)) return false;
        
		QueryList other = (QueryList) obj;
        return this.values.equals(other.values);
    }
}
