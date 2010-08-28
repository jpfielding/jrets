package org.realtors.rets.util.enhancements;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FilterInputStream implementation that strips out characters
 * that should not be found in a RETS Compact response.
 * 
 * The contract of this class is that the underlying stream
 * is an 7-bit ASCII character set compatible stream.  All 
 * control characters except for CR, LF, HT, and space are
 * replaced with an empty space character.
 */
public class RetsInvalidCharFilterInputStream extends FilterInputStream {
	
	public static final byte LF = '\n';
	public static final byte CR = '\r';
	public static final byte SP = ' ';
	public static final byte HT = '\t';
	
	public static final byte CTLS_START = 0;
	public static final byte CTLS_END = 31;
	public static final byte DEL = 127;
	
	private byte replacement;

	/**
	 */
	public RetsInvalidCharFilterInputStream(InputStream in, byte replacement) {
		super(in);
		this.replacement = replacement;
	}
	
	/**
	 */
	public RetsInvalidCharFilterInputStream(InputStream in) {
		this(in, SP);
	}
	
	/** 
	 * @return replace <code>b</code> with valid byte if necessary,
	 * otherwise simply return <code>b</code>
	 */
	private int clean(int b) {
		if (b <= CTLS_END || b == DEL){
			if (b == LF || b == CR || b == HT )
				return b;
			return this.replacement;
		}
		return b;
	}

	@Override
	public int read() throws IOException {
		int b = super.read();
		if (b == -1)
			return b;
		return clean(b);
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b,0,b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int length = super.read(b, off, len);
		if (length == -1)
				return length;
		for (int i = off; i < off+length; i++) {
			b[i] = (byte) clean(b[i]);
		}
		return length;
	}
	
	

}
