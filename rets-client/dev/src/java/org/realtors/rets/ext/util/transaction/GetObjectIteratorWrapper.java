package org.realtors.rets.ext.util.transaction;

import java.io.IOException;

import org.realtors.rets.client.GetObjectIterator;
import org.realtors.rets.client.SingleObjectResponse;

public class GetObjectIteratorWrapper<T extends SingleObjectResponse> implements GetObjectIterator<T> {
	
	protected GetObjectIterator<T> delegate;
	
	public GetObjectIteratorWrapper(GetObjectIterator delegate){
		this.delegate = delegate;
	}
	
	public void close() throws IOException {
		this.delegate.close();
	}
	public T next() {
		return this.delegate.next();
	}
	public void remove() {
		this.delegate.remove();
	}
	@Override
	public boolean equals(Object obj) {
		return this.delegate.equals(obj);
	}
	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}
	public boolean hasNext() {
		return this.delegate.hasNext();
	}
	@Override
	public String toString() {
		return this.delegate.toString();
	}
}
