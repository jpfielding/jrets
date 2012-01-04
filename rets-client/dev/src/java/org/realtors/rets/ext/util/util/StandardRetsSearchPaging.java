package org.realtors.rets.ext.util.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.realtors.rets.client.CompactRowPolicy;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultSet;
import org.realtors.rets.ext.util.RetsClient;
import org.realtors.rets.ext.util.SearchResultSetWrapper;
import org.realtors.rets.ext.util.RetsClient.ResponseTouch;
import org.realtors.rets.ext.util.db.SearchResultSetTable;
import org.realtors.rets.ext.util.db.Table;
import org.realtors.rets.ext.util.enhancements.SearchRequestEx;
import org.realtors.rets.ext.util.transaction.StreamingSearch;

import com.google.common.collect.AbstractIterator;
import com.google.common.io.Closeables;

public class StandardRetsSearchPaging extends AbstractIterator<Table> {
	
	private class RowCountSearchResultSet extends SearchResultSetWrapper {
		public RowCountSearchResultSet(SearchResultSet delegate) {
			super(delegate);
		}
		@Override
		public String[] next() throws RetsException {
			StandardRetsSearchPaging.this.rowsRetrieved++;
			return super.next();
		}
	}

	private final RetsClient retsClient;
	private final SearchRequestEx searchRequest;
	private final CompactRowPolicy badRowPolicy;
	private final RetsSearchInfo searchInfo;
	private final ResponseTouch<SearchResultSet>[] onComplete;
	
	private int searchCount;
	private int rowsRetrieved;
	private int chunksAttempted;
	
	public StandardRetsSearchPaging(RetsClient retsClient, SearchRequestEx searchRequest, CompactRowPolicy badRowPolicy, RetsSearchInfo searchInfo, ResponseTouch<SearchResultSet>... onComplete) {
		this.retsClient = retsClient;
		
		this.searchRequest = searchRequest;
		
		this.badRowPolicy = badRowPolicy;
		this.searchInfo = searchInfo;
		this.onComplete = onComplete;
		
		this.searchCount = -1;
		this.rowsRetrieved = 0;
		this.chunksAttempted = 0;
		
		Logger.getLogger(this.getClass()).debug(String.format("paging setup for query %s", searchRequest));

		if(this.searchInfo.getLimit() > 0) {
			// only necessary if we're paging, would be nice for debugging otherwise, but breaks a few people
			this.searchRequest.setCountFirst();
			this.searchRequest.setLimit(this.searchInfo.getLimit());
			Logger.getLogger(this.getClass()).debug("Requesting search count and a limit of: " + this.searchInfo.getLimit());
		} else {
			if(this.searchInfo.isForceSearchLimitNone())
				this.searchRequest.forceLimitNone();
			else
				this.searchRequest.setLimitNone();
		}
	}
	
	@Override
	protected Table computeNext() {
		// Always try 1 chunk
		if(this.chunksAttempted > 0) {
			// no search limit so 1 try should be enough
			if (this.searchInfo.getLimit() > 0) return endOfData();
			// tried 1 with a limit, did we pull more than expected
			if (this.rowsRetrieved >= this.searchCount) return endOfData();
		}
		// RETS 1.7.2-7.4.4 - if we _ever_ set offset, set it at the start (changed 2009.01.09 from default 0 -> 1 and to always set)
		if (this.searchInfo.getLimit() > 0) { // but only if we're paging, interealty breaks if its set and they dont support offset
			int offset = this.getOffset();
			this.searchRequest.setOffset(offset);
			Logger.getLogger(this.getClass()).debug("Setting offset to: " + offset + " for chunked search of " + this.searchCount + " rows");
		}
		try {
			this.chunksAttempted++;
			StreamingSearch search = new StreamingSearch(this.searchRequest, this.searchInfo);
			SearchResultSet chunkResultSet = this.retsClient.executeRetsTransaction(search,this.onComplete);
			Logger.getLogger(this.getClass()).debug(String.format("%s results found for segment %s of %s",chunkResultSet.getCount(),this.chunksAttempted, this.searchRequest));
			
			if(!chunkResultSet.hasNext()) {
				//Sanity check to prevent infinite retrieval loop in case server side count shrinks mid-chunking (allowed by RETS)
				Logger.getLogger(this.getClass()).debug("Stopping search with less rows than advertised [rows retrieved: " + this.rowsRetrieved + " search count: " + this.searchCount + "]");
				this.rowsRetrieved = this.searchCount;
			}
			
			// Always (re-)set the count - it may change on each chunk
			if(this.searchInfo.getLimit() > 0)
				this.setSearchCount(chunkResultSet.getCount());
			
			return new SearchResultSetTable(new RowCountSearchResultSet(chunkResultSet), this.badRowPolicy) {
				@Override
				protected void retsException(RetsException e) {
					Logger.getLogger(getClass()).debug("Table.close() of streaming rets search.");
				}
			};
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private int getOffset() {
		return this.rowsRetrieved + this.searchInfo.getOffset();
	}

	private void setSearchCount(int count) {
		this.searchCount = count;
		Logger.getLogger(this.getClass()).debug("Search count of " + this.searchCount);
	}
	
	public static Table compose(final StandardRetsSearchPaging tables) {
		return new Table(){
			Table current;
			public boolean hasNext() {
				if (this.current != null && this.current.hasNext()) return true;

				if (!tables.hasNext()) return false;
				this.current = tables.peek();
				if (!this.current.hasNext()) return hasNext();
				return true;
			}
			public List<String> next() {
				return this.current.next();
			}
			public void remove() {
				remove();
			}
			public List<String> getColumnNames() {
				return this.current.getColumnNames();
			}
			public void close() {
				Closeables.closeQuietly(this.current);
			}
		};
	}
	
}