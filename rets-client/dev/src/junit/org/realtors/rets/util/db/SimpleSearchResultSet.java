package org.realtors.rets.util.db;

import java.util.Iterator;

import org.realtors.rets.client.SearchResult;
import org.realtors.rets.client.SearchResultSet;

public class SimpleSearchResultSet implements SearchResultSet {
	private SearchResult delegate;
	private Iterator resultsIterator;

	public SimpleSearchResultSet(SearchResult delegate) {
		this.delegate = delegate;
	}

	public String[] next() {
		Object next = this.getResultIterator().next();
		return (String[]) next;
	}

	private Iterator getResultIterator() {
		if(this.resultsIterator == null) this.resultsIterator = this.delegate.iterator();
		return this.resultsIterator;
	}

	public boolean hasNext() {
		return this.getResultIterator().hasNext();
	}

	public int getCount() {
		return this.delegate.getCount();
	}

	public String[] getColumns() {
		return this.delegate.getColumns();
	}

	public boolean isMaxrows() throws IllegalStateException {
		return this.delegate.isMaxrows();
	}

	public boolean isComplete() {
		return this.delegate.isComplete();
	}
}
