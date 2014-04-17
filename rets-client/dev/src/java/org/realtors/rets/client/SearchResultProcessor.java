package org.realtors.rets.client;

import java.io.InputStream;
import java.io.Reader;

import org.xml.sax.InputSource;

public interface SearchResultProcessor {
	public SearchResultSet parse(InputSource src) throws RetsException;
	public SearchResultSet parse(InputStream src) throws RetsException;
    public SearchResultSet parse(Reader src) throws RetsException;


}
