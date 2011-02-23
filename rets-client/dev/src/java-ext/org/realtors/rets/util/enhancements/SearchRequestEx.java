package org.realtors.rets.util.enhancements;

import org.realtors.rets.client.SearchRequest;
import org.realtors.rets.util.ResourceClass;


public class SearchRequestEx extends SearchRequest {
	
	private String dmqlQuery;

	public SearchRequestEx(ResourceClass resourceClass, String query) {
		super(resourceClass.getResource().getName(), resourceClass.getClassName(), query);
		this.dmqlQuery = query;
	}
	
	public String getDmqlQuery() {
		return this.dmqlQuery;
	}
	
    public void forceLimitNone() {
        this.setQueryParameter(KEY_LIMIT, "NONE");
    }
    
}