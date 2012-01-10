package org.realtors.rets.ext.db;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.realtors.rets.client.CompactRowPolicy;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultSet;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;


public abstract class SearchResultSetTable extends AbstractIterator<List<String>> implements Table {
	
	private static final List<String> EMPTY_COLUMNS = Collections.unmodifiableList(new LinkedList<String>());
	
    private final CompactRowPolicy policy;
	private final SearchResultSet resultSet;
	
	private List<String> columns;

	private int rowIndex;
	
	public SearchResultSetTable(SearchResultSet resultSet, CompactRowPolicy policy) {
		this.resultSet = resultSet;
		this.policy = policy;
	}
	
	public List<String> getColumnNames() {
        if(this.columns == null) {
            try {
        	    this.columns = Lists.newArrayList();
        	    String[] cols = this.resultSet.getColumns();
				if (cols != null) this.columns.addAll(Arrays.asList(cols));
            } catch (RetsException e) {
    			retsException(e);
    			return EMPTY_COLUMNS;
            }
        }
        return Lists.newArrayList(this.columns);
    }

	protected abstract void retsException(RetsException e);

	@Override
	protected List<String> computeNext() {
		try {
			if(this.resultSet == null || !this.resultSet.hasNext()) return endOfData();
			
			String[] currentRow = this.resultSet.next();
			this.rowIndex++;//1 based
			
			List<String> cols = this.getColumnNames();
			
			if(currentRow.length == cols.size()) return Lists.newArrayList(currentRow);
			
			this.policy.apply(this.rowIndex, this.columns.toArray(new String[this.columns.size()]), currentRow);
			
			return computeNext();//Our policy didn't blow up, so recurse until a good row is found or we run out of rows!
		} catch (RetsException e) {
			retsException(e);
			return null;
		}
	}

	public void close() { /* noop */ }
	
}
