package org.realtors.rets.client;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface SearchResult extends SearchResultInfo {
	public String[] getRow(int idx) throws NoSuchElementException;

	public Iterator iterator();

	@Override
	public String[] getColumns();

	@Override
	public boolean isMaxrows();

	@Override
	public int getCount();

	@Override
	public boolean isComplete();
}
