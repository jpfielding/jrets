package org.realtors.rets.client;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WireLogInputStream extends FilterInputStream {

	public static final String WIRE_LOG_NAME = "rets-client.wire";
	private static final Log WIRE_LOG = LogFactory.getLog(WIRE_LOG_NAME);

	public WireLogInputStream(InputStream stream) {
		super(stream);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int numRead = super.read(b, off, len);
		if (numRead != -1) {
			WIRE_LOG.debug(new String(b, off, numRead));
		}
		return numRead;
	}

	@Override
	public int read() throws IOException {
		int byteRead = super.read();
		if (byteRead != -1) {
			WIRE_LOG.debug(new String(new byte[] { (byte) byteRead }));
		}
		return byteRead;
	}

	public static final Log getWireLog() {
		return WIRE_LOG;
	}

}
