package org.realtors.rets.client;

import org.xml.sax.InputSource;

public interface SearchResultProcessor {
	public SearchResultSet parse(InputSource src) throws RetsException;

}
