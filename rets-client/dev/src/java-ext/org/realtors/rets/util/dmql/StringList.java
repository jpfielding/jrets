package org.realtors.rets.util.dmql;

import java.util.List;

public class StringList extends QueryList<String> {
	
	public static final String WILDCARD = "*";
	public static final String SINGLE_CHAR = "?";

	public StringList() {
		super();
	}

	public StringList(List<String> values) {
		super(values);
	}
	public StringList(String ... values) {
		super(values);
	}
}
