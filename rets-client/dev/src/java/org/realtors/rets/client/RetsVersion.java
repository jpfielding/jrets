package org.realtors.rets.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RetsVersion implements Serializable {
	private static Map mMap = new HashMap();
	public static final RetsVersion RETS_10 = new RetsVersion(1, 0);
	public static final RetsVersion RETS_15 = new RetsVersion(1, 5);
	public static final RetsVersion RETS_16 = new RetsVersion(1, 6);
	public static final RetsVersion RETS_17 = new RetsVersion(1, 7);
	public static final RetsVersion DEFAULT = RETS_15;
	public static final String RETS_VERSION_HEADER = "RETS-Version";

	private int mMajor;
	private int mMinor;
	private int mDraft;

	public RetsVersion(int major, int minor) {
		this(major, minor, 0);
	}

	public RetsVersion(int major, int minor, int draft) {
		this.mMajor = major;
		this.mMinor = minor;
		this.mDraft = draft;
		mMap.put(this.toString(), this);
	}

	public int getMajor() {
		return this.mMajor;
	}

	public int getMinor() {
		return this.mMinor;
	}

	public int getDraft() {
		return this.mDraft;
	}

	@Override
	public String toString() {
		if (this.mDraft == 0) {
			return "RETS/" + this.mMajor + "." + this.mMinor;
		}
		return "RETS/" + this.mMajor + "." + this.mMinor + "d" + this.mDraft;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RetsVersion) {
			RetsVersion v = (RetsVersion) o;
			if ((v.getMajor() == this.mMajor) && (v.getMinor() == this.mMinor) && (v.getDraft() == this.mDraft)) {
				return true;
			}
		}
		return false;
	}

	public static RetsVersion getVersion(String ver) {
		return (RetsVersion) mMap.get(ver);
	}

}
