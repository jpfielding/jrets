package org.realtors.rets.ext.util.dmql;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

class DmqlUtils {

	protected static String[] convert(Object[] objects) {
		return Iterables.toArray(Iterables.transform(Lists.newArrayList(objects),DmqlUtils.TO_STRING),String.class);
	}

	protected static Object[] prune(Object[] objects){
		return Iterables.toArray(Iterables.filter(Lists.newArrayList(objects),DmqlUtils.NOT_BLANK), Object.class);
	}

	protected static <T> List<T> prune(List<T> objects){
		return Lists.newArrayList(Iterables.filter(objects, NOT_BLANK));
	}

	protected static final Predicate NOT_BLANK = new Predicate(){
		public boolean apply(Object object) {
			return object != null && !StringUtils.isEmpty(object.toString());
		}
	};
	protected static final Function TO_STRING = new Function(){
		public Object apply(Object input) {
			return input == null ? "" :  input.toString();
		}
	};

}
