package org.realtors.rets.ext.util.util;

import java.util.Set;

import org.realtors.rets.client.CompactRowPolicy;
import org.realtors.rets.ext.util.RetsSearchResultFormat;



public class RetsSearchInfo {
	
	private boolean cdataEscape;
    private int searchBufferSize;
    private int limit;
    private Set<Integer> validDelayedReplyCodes;
	private int offset;
	private boolean forceSearchLimitNone;
	private RetsSearchResultFormat retsSearchResultFormat;
	private CompactRowPolicy compactRowPolicy;
	
	public boolean isCdataEscape() {
		return this.cdataEscape;
	}
	public void setCdataEscape(boolean cdataEscape) {
		this.cdataEscape = cdataEscape;
	}
	public int getLimit() {
		return this.limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getSearchBufferSize() {
		return this.searchBufferSize;
	}
	public void setSearchBufferSize(int searchBufferSize) {
		this.searchBufferSize = searchBufferSize;
	}
	public Set<Integer> getValidDelayedReplyCodes() {
		return this.validDelayedReplyCodes;
	}
	public void setValidDelayedReplyCodes(Set<Integer> validDelayedReplyCodes) {
		this.validDelayedReplyCodes = validDelayedReplyCodes;
	}
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public boolean isForceSearchLimitNone() {
		return this.forceSearchLimitNone;
	}
	public void setForceLimitNone(boolean forceSearchLimitNone) {
		this.forceSearchLimitNone = forceSearchLimitNone;
	}
	public void setSearchResultFormat(RetsSearchResultFormat retsSearchResultFormat) {
		this.retsSearchResultFormat = retsSearchResultFormat;
	}
	public RetsSearchResultFormat getSearchResultFormat(){
		return this.retsSearchResultFormat;
	}
	public CompactRowPolicy getCompactRowPolicy() {
		return this.compactRowPolicy;
	}
	public void setCompactRowPolicy(CompactRowPolicy compactRowPolicy) {
		this.compactRowPolicy = compactRowPolicy;
	}
}
