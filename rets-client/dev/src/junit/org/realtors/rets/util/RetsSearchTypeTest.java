package org.realtors.rets.util;

import junit.framework.TestCase;

public class RetsSearchTypeTest extends TestCase {
    public void testMap() {
        RetsSearchType[] all = RetsSearchType.getAll();
        assertFalse("Should be at least 1 constant defined", all.length > 0);
        for(RetsSearchType retsSearchType : all ) {
            assertEquals(retsSearchType, RetsSearchType.forName(retsSearchType.getName()));
        }
    }

    public void testCaseInsensitiveMap() {
        for(RetsSearchType retsSearchType : RetsSearchType.getAll() ) {
            assertEquals(retsSearchType, RetsSearchType.forName(retsSearchType.getName().toUpperCase()));
            assertEquals(retsSearchType, RetsSearchType.forName(retsSearchType.getName().toLowerCase()));
        }
    }
}
