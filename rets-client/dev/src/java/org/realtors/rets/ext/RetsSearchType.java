package org.realtors.rets.ext;

/**
 * The strange enum case insensitiviy towards names is forced by Marketlinx RETS servers
 */
public class RetsSearchType extends CaseInsensitiveWellKnownName {
	public static final RetsSearchType ACTIVE_AGENT = new RetsSearchType("ActiveAgent");
	public static final RetsSearchType AGENT = new RetsSearchType("Agent");
	public static final RetsSearchType HISTORY = new RetsSearchType("History");
	public static final RetsSearchType OFFICE = new RetsSearchType("Office");
	public static final RetsSearchType OPEN_HOUSE = new RetsSearchType("OpenHouse");
	public static final RetsSearchType PROPERTY = new RetsSearchType("Property");
	public static final RetsSearchType PROSPECT = new RetsSearchType("Prospect");
	public static final RetsSearchType TAX = new RetsSearchType("Tax");
	public static final RetsSearchType TOUR = new RetsSearchType("Tour");

	public RetsSearchType(String name) {
		super(name);
	}

	public static RetsSearchType forName(String resourceID) {
		return CaseInsensitiveWellKnownName.forName(resourceID, RetsSearchType.class);
	}

	public static RetsSearchType[] getAll() {
		return CaseInsensitiveWellKnownName.getAll(RetsSearchType.class);
	}
}
