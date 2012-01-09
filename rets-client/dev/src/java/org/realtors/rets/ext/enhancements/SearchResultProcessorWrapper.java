package org.realtors.rets.ext.enhancements;

import java.io.InputStream;
import java.io.Reader;

import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultProcessor;
import org.realtors.rets.client.SearchResultSet;

/** Base class for creating wrappers for SearchResultProcessors. */
public class SearchResultProcessorWrapper implements SearchResultProcessor {

	private SearchResultProcessor delegate;

	/** Construct this class with the delegate SearchResultProcessor */
	protected SearchResultProcessorWrapper(SearchResultProcessor delegate) {
		super();
		this.delegate = delegate;
	}
	
	public SearchResultSet parse(InputStream in) throws RetsException {
		return this.delegate.parse(in);
	}
	public SearchResultSet parse(Reader in) throws RetsException {
		return this.delegate.parse(in);
	}
}
