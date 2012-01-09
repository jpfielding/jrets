package org.realtors.rets.ext.db;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.realtors.rets.client.CompactRowPolicy;
import org.realtors.rets.client.RetsException;

import com.google.common.collect.Lists;

public class SearchResultSetTableTest {

	private String[] expectedColumns;
	private List<String[]> rows;

	private SimpleSearchResult result;
	
	private List<Exception> exceptions;
	private SimpleSearchResultSet resultSetToWrap;
	private SearchResultSetTable resultTable;
	
	@Before
	public void setup() {
		this.expectedColumns = new String[] { "jack", "jane", "jim" };
		this.rows = Lists.newArrayList();
		this.rows.add(new String[] { "1", "2", "3" });
		this.rows.add(new String[] { "4", "5", "6" });
		this.rows.add(new String[] { "7", "8", "9" });
		
		this.result = new SimpleSearchResult(this.expectedColumns, this.rows);
		
		this.exceptions = Lists.newArrayList();
		this.resultSetToWrap = new SimpleSearchResultSet(this.result);
		this.resultTable = new SearchResultSetTable(this.resultSetToWrap, CompactRowPolicy.DEFAULT) {
			@Override
			protected void retsException(RetsException e) {
				SearchResultSetTableTest.this.exceptions.add(e);
			}
		};
	}

	public void test() {
		Assert.assertEquals(this.expectedColumns, this.resultTable.getColumnNames());
		Assert.assertEquals(this.rows.get(0), this.resultTable.next());
		Assert.assertEquals(this.rows.get(1), this.resultTable.next());
		Assert.assertEquals(this.rows.get(2), this.resultTable.next());
		Assert.assertFalse(this.resultTable.hasNext());
	}

}
