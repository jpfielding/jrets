package org.realtors.rets.ext;

import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultSet;

public class SearchResultSetWrapper implements SearchResultSet {
	private final SearchResultSet delegate;

	public SearchResultSetWrapper(SearchResultSet delegate) {
		this.delegate = delegate;
	}

	public boolean hasNext() throws RetsException {
		return this.delegate.hasNext();
	}
	public String[] next() throws RetsException {
		return this.delegate.next();
	}
	public String[] getColumns() throws RetsException {
		return this.delegate.getColumns();
	}
	public int getCount() throws RetsException {
		return this.delegate.getCount();
	}
	public boolean isComplete() throws RetsException {
		return this.delegate.isComplete();
	}
	public boolean isMaxrows() throws RetsException, IllegalStateException {
		return this.delegate.isMaxrows();
	}
}
