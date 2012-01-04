package org.realtors.rets.ext.util.enhancements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.realtors.rets.ext.util.enhancements.Rets10CDataEscapingInputStream;

import com.google.common.io.ByteStreams;

public class BadXmlSafeReaderTest {
	private InputStream badInputReader;
	private Rets10CDataEscapingInputStream badSafeInput;

	@Before
	protected void setUp() throws Exception {
		String badInput =
		"<RETS ReplyCode=\"0\" ReplyText=\"SUCCESS\">\n" +
		"<DELIMITER value=\"09\" />\n" +
		"<COLUMNS>	JACK	JILL	</COLUMNS>\n" +
		"<DATA>	jimmy&jane	<small	</DATA>\n" +
		"<!-- ... -->\n" +
		"<DATA>	jack	fred	</DATA>\n" +
		"</RETS>\n";
		this.badInputReader = new ByteArrayInputStream(badInput.getBytes());
		this.badSafeInput = new Rets10CDataEscapingInputStream(this.badInputReader);
	}

	@Test
	public void test() throws IOException {
		ByteStreams.toByteArray(this.badSafeInput);
	}
}
