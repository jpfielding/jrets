package org.realtors.rets.ext.transaction;

import java.util.Set;

import org.apache.log4j.Logger;
import org.realtors.rets.client.CompactRowPolicy;
import org.realtors.rets.client.InvalidReplyCodeException;
import org.realtors.rets.client.InvalidReplyCodeHandler;
import org.realtors.rets.client.RetsSession;
import org.realtors.rets.client.SearchResultProcessor;
import org.realtors.rets.client.SearchResultSet;
import org.realtors.rets.client.StreamingSearchResultProcessor;
import org.realtors.rets.ext.RetsTransaction;
import org.realtors.rets.ext.enhancements.BadCompactDecodedSearchResultProcessor;
import org.realtors.rets.ext.enhancements.SearchRequestEx;
import org.realtors.rets.ext.util.RetsSearchInfo;

import com.google.common.collect.Iterables;

public class StreamingSearch implements RetsTransaction<SearchResultSet> {
	private SearchRequestEx searchRequest;
	private int bufferSize;
	private boolean cdataEscape;
	private Set<Integer> validDelayedReplyCodes;
	private CompactRowPolicy policy;

	public StreamingSearch(SearchRequestEx searchRequest, int bufferSize, boolean cdataEscape, Set<Integer> validDelayedReplyCodes, CompactRowPolicy policy) {
		this.searchRequest = searchRequest;
		this.bufferSize = bufferSize;
		this.cdataEscape = cdataEscape;
		this.validDelayedReplyCodes = validDelayedReplyCodes;
		this.policy = policy;
	}
	
	public StreamingSearch(SearchRequestEx searchRequest, RetsSearchInfo searchInfo) {
		this(searchRequest,searchInfo.getSearchBufferSize(),searchInfo.isCdataEscape(), searchInfo.getValidDelayedReplyCodes(), searchInfo.getCompactRowPolicy());
	}

	public SearchResultSet execute(RetsSession session) throws Exception {
		// create streaming processor
		SearchResultProcessor processor = this.createStreamingSearchProcessor();
			
		// wrap with compact decoded helper
		processor = new BadCompactDecodedSearchResultProcessor(processor, this.bufferSize, this.cdataEscape);
		
		this.searchRequest.setVersion(session.getRetsVersion());
		
		return session.search(this.searchRequest, processor);
	}

	private SearchResultProcessor createStreamingSearchProcessor() {
		// no timeout on how long until we must read from our own full buffer
		StreamingSearchResultProcessor processor = new StreamingSearchResultProcessor(this.bufferSize, 0);

		InvalidReplyCodeHandler invalidReplyCodeProcessor = new InvalidReplyCodeHandler() {
			public void invalidRetsReplyCode(int replyCode) throws InvalidReplyCodeException {
				throw new InvalidReplyCodeException(replyCode);
			}
			public void invalidRetsStatusReplyCode(int replyCode) throws InvalidReplyCodeException {
				if(!StreamingSearch.this.validDelayedReplyCodes.contains(replyCode))
					throw new InvalidReplyCodeException(replyCode);
				Logger.getLogger(StreamingSearch.class).info("Trapped valid delayed reply code: " + replyCode);
			}
		};
		if(!Iterables.isEmpty(this.validDelayedReplyCodes)) 
			processor.setInvalidRelyCodeHandler(invalidReplyCodeProcessor);
		processor.setCompactRowPolicy(this.policy);
		return processor;
	}
}
