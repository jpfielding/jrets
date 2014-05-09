package org.realtors.rets.client;

import java.util.NoSuchElementException;
import java.util.Iterator;

public interface SearchResult extends SearchResultInfo {
	public String[] getRow(int idx) throws NoSuchElementException;

	public Iterator iterator();

	public String[] getColumns();

	public boolean isMaxrows();

	public int getCount();

	public boolean isComplete();
}
