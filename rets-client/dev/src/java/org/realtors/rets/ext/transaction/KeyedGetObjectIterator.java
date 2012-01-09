package org.realtors.rets.ext.transaction;

import org.realtors.rets.client.RetsException;

public interface KeyedGetObjectIterator {
	
	/** Iterator style hasNext() */
	public boolean hasNext() throws RetsException;
	
	/** Iterator style next() */
	public KeyedSingleObjectResponse next() throws RetsException;
	
	/** Must be called to close underlying resources. */
	public void close() throws RetsException;

}
