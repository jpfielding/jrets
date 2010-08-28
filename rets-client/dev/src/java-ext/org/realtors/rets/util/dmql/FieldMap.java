package org.realtors.rets.util.dmql;

import org.apache.commons.lang.StringUtils;

public class FieldMap<T extends FieldValue> implements SearchCondition {

	private String name;
	private T value;
    private boolean sense;

	public FieldMap(String name, T value) {
		this(name, value, true);
	}
	
	public FieldMap(String name, T value, boolean sense) {
		this.name = name;
		this.value = value;
		this.sense = sense;
	}

	public String getName() {
		return this.name;
	}

	public boolean isSense() {
		return this.sense;
	}

	public T getValue() {
		return this.value;
	}

	@Override
	public String toString(){
		if( StringUtils.isEmpty(this.value.toString()) ) return "";
		return ( this.sense ? "(" : "~(" ) + this.name +"="+ this.value.toString() +")";
	}
}
