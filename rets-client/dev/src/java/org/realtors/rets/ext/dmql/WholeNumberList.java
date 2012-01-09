package org.realtors.rets.ext.dmql;

import java.math.BigInteger;


public class WholeNumberList extends LookupList<BigInteger> {
	public WholeNumberList() {
		super();
	}

	public WholeNumberList(BigInteger ... values) {
		super(values);
	}

	public WholeNumberList(LookupType type, BigInteger ... values) {
		super(type, values);
	}
}
