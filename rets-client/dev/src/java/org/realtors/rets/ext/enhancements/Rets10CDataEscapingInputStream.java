package org.realtors.rets.ext.enhancements;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This FilterInputStream is for the purpose of hiding non XML encoded characters inside
 * a RETS Compact response.  RETS compact as of versions before 1.5 does not guarantee
 * the compact response to be valid XML.  As a result, servers may not XML encode data
 * in the response, and actually they SHOULD not XML encode the data in the response, if
 * the RETS version is pre 1.5.
 *
 * This should only be needed to wrap RETS 1.0 responses.
 *
 * I only work on RETS COMPACT DECODED xml streams!
 */
public class Rets10CDataEscapingInputStream extends FilterInputStream {
	private static int[] createIntArray(byte[] bytes) {
		int[] intArray = new int[bytes.length];
		for (int i = 0; i < intArray.length; i++)
			intArray[i] = bytes[i];
		return intArray;
	}

	private static final int[] DATA_START 	= Rets10CDataEscapingInputStream.createIntArray("<DATA>".getBytes());
	private static final int[] DATA_END 		= Rets10CDataEscapingInputStream.createIntArray("</DATA>".getBytes());
	private static final int[] CDATA_START	= Rets10CDataEscapingInputStream.createIntArray("<![CDATA[".getBytes());
	private static final int[] CDATA_END		= Rets10CDataEscapingInputStream.createIntArray("]]>".getBytes());

	interface Command {
		public void execute();
	}
	private final Command START_TOKEN_FOUND = new Command() {
		public void execute() {
			System.arraycopy(DATA_START, 0, Rets10CDataEscapingInputStream.this.buffer, 0, DATA_START.length);
			System.arraycopy(CDATA_START, 0, Rets10CDataEscapingInputStream.this.buffer, DATA_START.length, CDATA_START.length);//append CDATA_START
			Rets10CDataEscapingInputStream.this.bufferPosition = DATA_START.length + CDATA_START.length;

			Rets10CDataEscapingInputStream.this.found = Rets10CDataEscapingInputStream.this.END_TOKEN_FOUND;
			Rets10CDataEscapingInputStream.this.token = DATA_END;//Need a ringbuffer to fix this ugly
		}
	};

	private final Command END_TOKEN_FOUND = new Command() {
		public void execute() {
			System.arraycopy(CDATA_END, 0, Rets10CDataEscapingInputStream.this.buffer, 0, CDATA_END.length);//prepend CDATA_END
			System.arraycopy(DATA_END, 0, Rets10CDataEscapingInputStream.this.buffer, CDATA_END.length, DATA_END.length);
			Rets10CDataEscapingInputStream.this.bufferPosition = CDATA_END.length + DATA_END.length;

			Rets10CDataEscapingInputStream.this.found = Rets10CDataEscapingInputStream.this.START_TOKEN_FOUND;
			Rets10CDataEscapingInputStream.this.token = DATA_START;//Need a ringbuffer to fix this ugly
		}
	};

	private int[] buffer;
	private int bufferPosition;
	private int readMark;
	private boolean eos;

	private Command found;
	private int[] token;

	protected Rets10CDataEscapingInputStream(InputStream stream) throws IOException {
		super(BadCompactDecodedCharsetHelper.replaceXmlDeclaration(stream));

		this.buffer = new int[CDATA_START.length + DATA_END.length];//longest combo

		this.found = this.START_TOKEN_FOUND;
		this.token = DATA_START;
	}

	@Override
	public int read() throws IOException {
		if(this.bufferPosition > 0) {
			int readChar = this.buffer[this.readMark - this.bufferPosition];
			this.bufferPosition--;
			return readChar;
		}

		this.readMark = 0;
		this.readForToken(this.token, this.found);
		return this.read();
	}

	private void readForToken(int[] TOKEN, Command foundCommand) throws IOException {
		int currentChar = -1;
		for(this.bufferPosition = 0;
			this.bufferPosition < TOKEN.length && (currentChar = super.read()) == TOKEN[this.bufferPosition];
			this.bufferPosition++) {

			this.buffer[this.bufferPosition] = currentChar;
		}

		if(this.bufferPosition == TOKEN.length) foundCommand.execute();
		else this.buffer[this.bufferPosition++] = currentChar;

		this.readMark = this.bufferPosition;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(this.eos)
			return -1;

		int read = off;
		for( ; read < off + len; read++) {
			int nextByte = this.read();
			if(nextByte == (-1)) {
				this.eos = true;
				break;
			}

			b[read] = (byte) nextByte;
		}

		return ( read - off );
	}
}