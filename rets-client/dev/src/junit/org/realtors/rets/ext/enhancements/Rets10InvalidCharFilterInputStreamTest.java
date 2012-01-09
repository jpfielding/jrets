package org.realtors.rets.ext.enhancements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.realtors.rets.ext.enhancements.RetsInvalidCharFilterInputStream;

import junit.framework.TestCase;

public class Rets10InvalidCharFilterInputStreamTest extends TestCase {
	public static final byte R = ' ';

	public static final byte[] ORIGINAL = 
		new byte[]{
			'a',' ','1','A',// VALID CHARS
			'\t','\r','\n',// VALID CONTROL CHARS
			0,1,2,3,4,5,6,7,8,11,12,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31, // INVALID, RESTRICTED, CONTROL CHARS
			127, // DELETE CHAR
		};
	
	public static final byte[] FILTERED = new byte[]{
			'a',' ','1','A', // VALID CHARS
			'\t','\r','\n', // VALID CONTROL CHARS
			R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R,R, // FILTERED CONTROL CHARS
			R, // DELETE CHAR
		};
	
	
	private RetsInvalidCharFilterInputStream stream;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.stream = new RetsInvalidCharFilterInputStream(new ByteArrayInputStream(ORIGINAL), R);
	}
	
	@Override
	protected void tearDown() throws Exception {
		this.stream.close();
		super.tearDown();
	}
	
	public void testReadSingleBytes() throws Exception {
		ByteArrayOutputStream bll = new ByteArrayOutputStream();
		
		byte b = -1;
		while(-1 != (b = (byte) this.stream.read())){
			bll.write(b);
		}
		
		verify(bll.toByteArray());
	}

	public void testReadByteArray() throws Exception {
		ByteArrayOutputStream bll = new ByteArrayOutputStream();
		
		byte[] b = new byte[1024];
		int length = -1;
		while(-1 != (length = this.stream.read(b))){
			for (int i = 0; i < length; i++) {
				bll.write(b[i]);
			}
		}
		
		verify(bll.toByteArray());
	}

	public void testReadByteArrayWithOffsetLength() throws Exception {
		ByteArrayOutputStream bll = new ByteArrayOutputStream();
		
		byte[] b = new byte[2];
		int length = -1;
		int off = 1;
		while(-1 != (length = this.stream.read(b,off,1))){
			for (int i = off; i < off+length; i++) {
				bll.write(b[i]);
			}
		}
		
		verify(bll.toByteArray());
	}
	
	private void verify(byte[] bs) {
		assertTrue("byte arrays should be equal", Arrays.equals(FILTERED, bs));
	}
}
