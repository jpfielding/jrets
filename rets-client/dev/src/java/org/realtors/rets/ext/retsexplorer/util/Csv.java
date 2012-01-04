package org.realtors.rets.ext.retsexplorer.util;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Csv {
	/** default unix line terminator */
	public static final String LF = "\n";
	public static final String CR = "\r";
	/** default windows line terminator */
	public static final String CRLF = CR+LF;
	// escaping
    private static final String DOUBLE_DOUBLE_QUOTE = "\"\"";
	private static final String QUOTE = "\"";

	public static String escape(char delimiter, boolean preserveNull, String input){
		if (input == null) return "";
		// "" != null
		if (preserveNull && input.length() == 0) return DOUBLE_DOUBLE_QUOTE;
		// can we avoid escaping?
		if (CharMatcher.anyOf(delimiter+Csv.QUOTE+CRLF).matchesNoneOf(input)) return input;
		// looks like we need to ecsape
		input = StringUtils.replace(input, Csv.QUOTE, Csv.DOUBLE_DOUBLE_QUOTE);
		return new StringBuilder(Csv.QUOTE).append(input).append(Csv.QUOTE).toString();
	}

	public static Function<String,String> escape(final char delimiter, final boolean preserveNull){
		return new Function<String,String>(){
			public String apply(String input) {
				return escape(delimiter, preserveNull, input);
			}};
	}
	
	public static String escape(final char delimiter, final boolean preserveNull, String... cols) {
		return escape(delimiter, preserveNull, Lists.newArrayList(cols));
	}
	public static String escape(final char delimiter, final boolean preserveNull, Iterable<String> cols) {
		return Joiner.on(delimiter).join(Iterables.transform(cols, escape(delimiter,preserveNull)));
	}
}
