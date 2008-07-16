package org.realtors.rets.client;

public class RetsVersionTest extends RetsTestCase {
	public void testEquals() {
		assertEquals("Checking 1.0", RetsVersion.RETS_10, new RetsVersion(1, 0));

		assertEquals("Checking 1.5", RetsVersion.RETS_15, new RetsVersion(1, 5));

		assertFalse("Checking draft support", RetsVersion.RETS_15.equals(new RetsVersion(1, 5, 1)));
	}

	public void testToString() {
		assertEquals("Checking toString() 1.0", "RETS/1.0", RetsVersion.RETS_10.toString());
		assertEquals("Checking toString() 1.5", "RETS/1.5", RetsVersion.RETS_15.toString());
		assertEquals("Checking toString() draft", "RETS/1.5d1", new RetsVersion(1, 5, 1).toString());
	}
}
