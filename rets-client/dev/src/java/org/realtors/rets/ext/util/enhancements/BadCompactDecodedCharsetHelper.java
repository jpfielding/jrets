package org.realtors.rets.ext.util.enhancements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;


public class BadCompactDecodedCharsetHelper {
	private static final byte[] WINDOWS_CHARSET_SAFE_XML_DECLARATION =
		"<?xml version=\"1.0\" encoding=\"windows-1252\"?>".getBytes();

	private static final byte[] XML_START_BLOCK = "?xml".getBytes();
	private static final byte[] RETS_START_BLOCK = "RETS".getBytes();

	public static InputStream replaceXmlDeclaration(InputStream stream) throws IOException {
		InputStream replaced = BadCompactDecodedCharsetHelper.readOutXmlDeclaration(stream);
		return new SequenceInputStream(replaced, stream);
	}

	private static InputStream readOutXmlDeclaration(InputStream stream) throws IOException {
		boolean started = false;
		boolean lookingForEnd = false;
		int startBlockPos = 0;
		StringBuffer read = new StringBuffer();
		for(int data = -1; (data = stream.read()) != (-1); ) {
			read.append((char) data);
			if(!started && data == '<') started = true;
			else if(started && !lookingForEnd) {
				if(startBlockPos == 0 && data == RETS_START_BLOCK[0])
					return createMissingXmlDeclarationInputStream(read);
				else if(data != XML_START_BLOCK[startBlockPos++])
					throw new IllegalArgumentException("Bad xml declaration - read so far: " + read);
				else if(startBlockPos == XML_START_BLOCK.length) lookingForEnd = true;
			} else if(lookingForEnd && data == '>') break;
		}
		return new ByteArrayInputStream(WINDOWS_CHARSET_SAFE_XML_DECLARATION);
	}

	private static ByteArrayInputStream createMissingXmlDeclarationInputStream(StringBuffer read) {
		byte[] readBytes = read.toString().getBytes();
		byte[] xmlDeclAddedToBytesRead = new byte[WINDOWS_CHARSET_SAFE_XML_DECLARATION.length + readBytes.length];
		System.arraycopy(WINDOWS_CHARSET_SAFE_XML_DECLARATION, 0, xmlDeclAddedToBytesRead, 0, WINDOWS_CHARSET_SAFE_XML_DECLARATION.length);
		System.arraycopy(readBytes, 0, xmlDeclAddedToBytesRead, WINDOWS_CHARSET_SAFE_XML_DECLARATION.length, readBytes.length);
		return new ByteArrayInputStream(xmlDeclAddedToBytesRead);
	}

	private BadCompactDecodedCharsetHelper() { super(); }
}
