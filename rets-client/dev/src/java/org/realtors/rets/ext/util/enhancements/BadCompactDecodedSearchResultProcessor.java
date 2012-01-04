package org.realtors.rets.ext.util.enhancements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultProcessor;
import org.realtors.rets.client.SearchResultSet;
import org.xml.sax.InputSource;

public class BadCompactDecodedSearchResultProcessor extends SearchResultProcessorWrapper {

	private int streamBufferSize;
	private boolean cdataEscape;
	
	public BadCompactDecodedSearchResultProcessor(SearchResultProcessor delegate, int streamBufferSize, boolean cdataEscape) {
		super(delegate);
		
		this.streamBufferSize = streamBufferSize;
		this.cdataEscape = cdataEscape;
	}
	
	@Override
	public SearchResultSet parse(InputStream reader) throws RetsException {
		return this.parse(new InputSource(reader));
	}
	
	@Override
	public SearchResultSet parse(Reader reader) throws RetsException {
		return this.parse(new InputSource(reader));
	}
	
	public SearchResultSet parse(InputSource source) throws RetsException {
		try {
			InputStream input = new BufferedInputStream(source.getByteStream(), this.streamBufferSize);
			
			if (this.cdataEscape)
				input = new Rets10CDataEscapingInputStream(input);
			
			input = new RetsInvalidCharFilterInputStream(input);
			
			return super.parse(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
