package org.realtors.rets.client;

import java.io.InputStream;
import java.io.Reader;

public interface SearchResultProcessor {
	public SearchResultSet parse(InputStream in) throws RetsException;

	public SearchResultSet parse(Reader in) throws RetsException;
}
