package org.realtors.rets.retsexplorer.filter;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public final class FilterFactory {

	private FilterFactory(){
		throw new UnsupportedOperationException();
	}
	
	public static Filter hideEmptyColumnsFilter(JXTable target) {
		if (target == null) {
			return null;
		}
		int count = target.getColumnCount();
		int[] columns;
		Predicate<Object> predicate;
		if (count <= 0) {
			predicate = Predicates.alwaysTrue();
			columns = new int[0];
		} else {
			predicate = new Predicate<Object>() {
				public boolean apply(Object input) {
					return input != null && !StringUtils.isBlank(input.toString());
				}
			};
			columns = new int[count];
			for (int i=0; i<count; i++) {
				columns[i] = i;
			}
		}
		return new ColumnsPredicateFilter(target, predicate, columns);
	}
}
