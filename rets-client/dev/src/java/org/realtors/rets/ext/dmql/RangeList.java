package org.realtors.rets.ext.dmql;

import com.google.common.base.Joiner;

public class RangeList implements FieldValue {

	private Range[] list;

	public RangeList(Range... values) {
		this.list = values;
	}
	
	@Override
	public String toString(){
		String[] convertedList = DmqlUtils.convert(this.list);
		Object[] prunedList = DmqlUtils.prune(convertedList);
		if( prunedList.length == 0 ) return "";
		return Joiner.on(",").join(prunedList);
	}
}
