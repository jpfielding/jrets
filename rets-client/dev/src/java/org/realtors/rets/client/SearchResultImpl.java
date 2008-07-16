package org.realtors.rets.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SearchResultImpl implements SearchResult, SearchResultCollector {

	private String[] columnNames;
	private int count;
	private List<String[]> rows;
	private boolean maxRows;
	private boolean complete;

	public SearchResultImpl() {
		this.count = 0;
		this.rows = new ArrayList<String[]>();
		this.maxRows = false;
		this.complete = false;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		if (this.count > 0) {
			return this.count;
		}
		return this.rows.size();
	}

	public void setColumns(String[] columns) {
		this.columnNames = columns;
	}

	public String[] getColumns() {
		return this.columnNames;
	}

	public void addRow(String[] row) {
		this.rows.add(row);
	}

	public String[] getRow(int idx) {
		if (idx >= this.rows.size()) {
			throw new NoSuchElementException();
		}
		return this.rows.get(idx);
	}

	public Iterator iterator() {
		return this.rows.iterator();
	}

	public void setMaxrows() {
		this.maxRows = true;
	}

	public boolean isMaxrows() {
		return this.maxRows;
	}

	public void setComplete() {
		this.complete = true;
	}

	public boolean isComplete() {
		return this.complete;
	}
}
