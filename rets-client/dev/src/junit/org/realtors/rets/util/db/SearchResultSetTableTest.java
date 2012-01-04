package org.realtors.rets.util.db;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.realtors.rets.client.CompactRowPolicy;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultSet;

public class SearchResultSetTableTest {
	private String[] expectedColumns;
	private Object[][] rows;

	
	@Before
	public void initExpected() {
		this.expectedColumns = new String[] { "jack", "jane", "jim" };
		this.rows = new Object[][] {
				new String[] { "1", "2", "3" },
				new String[] { "4", "5", "6" },
				new String[] { "7", "8", "9" },
		};
	}
	

	protected SimpleSearchResult createSearchResult() {
		List<Object[]> list = new LinkedList<Object[]>();
		for(Object[] row : this.rows){
			list.add(row);
		}
		return new SimpleSearchResult(this.expectedColumns, list);
	}

	protected Table getResults() throws Exception {
		SearchResultSet resultSetToWrap = new SimpleSearchResultSet(this.createSearchResult());
		return new SearchResultSetTable(resultSetToWrap, CompactRowPolicy.DEFAULT) {
			@Override
			protected void retsException(RetsException e) {
				e.printStackTrace();
			}
		};
	}

	// TODO write the actual test
	
}
