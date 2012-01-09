package org.realtors.rets.ext.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.realtors.rets.client.InvalidReplyCodeException;
import org.realtors.rets.client.SearchResult;

import com.google.common.collect.Lists;

public class SimpleSearchResult implements SearchResult {
	private String[] columns;
	private ArrayList rows;
	private int last;
	private boolean maxrows;
	private boolean complete;

	public SimpleSearchResult() {
		this(null, Lists.newArrayList());
	}

	public SimpleSearchResult(String[] columns, Collection stringArrayRows) {
		this.columns = columns;
		this.last = stringArrayRows.size();
		this.rows = new ArrayList(stringArrayRows);
		this.complete = false;
		this.maxrows = false;
	}

	public void setCount(int count) {
		while(this.rows.size() < count) {
			this.rows.add(null);
		}
	}

	public int getCount() {
		return this.rows.size();
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String[] getColumns() {
		return this.columns;
	}

	public void addRow(String[] row) {
		this.rows.add(this.last++, row);
	}

	public String[] getRow(int idx) throws NoSuchElementException {
		return (String[]) this.rows.get(idx);
	}

	public Iterator iterator() {
		return this.rows.iterator();
	}

	public void setMaxrows() {
		this.maxrows = true;
	}

	public boolean isMaxrows() {
		return this.maxrows;
	}

	public void setComplete() {
		this.complete = true;
	}

	public boolean isComplete() {
		return this.complete;
	}

	public InvalidReplyCodeException getInvalidReplyCodeException() {
		return null;
	}
}
