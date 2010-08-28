package org.realtors.rets.util.dmql;

import java.util.List;

import com.google.common.base.Joiner;

/** RETS/1.0 Version of a Query -- only supports FieldMaps that are "And"ed */
public class Rets10Query {
	
	private SearchCondition[] fieldMaps;

	/** Construct a Query from a bunch of FieldMaps */
	public Rets10Query( List<FieldMap> fieldMaps ) {
		this.fieldMaps = QueryUtils.clean(fieldMaps.toArray(new FieldMap[fieldMaps.size()]));
	}
	
	/** Construct a Query from a bunch of FieldMaps */
	public Rets10Query( FieldMap... fieldMaps ) {
		this.fieldMaps = QueryUtils.clean(fieldMaps);
	}
	
	@Override
	public String toString(){
		return Joiner.on(",").join(this.fieldMaps);
	}

}

