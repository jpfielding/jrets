package org.realtors.rets.ext.util.dmql;


public class Empty implements SearchCondition {

	private final String name;
	private final boolean sense;

	public Empty(String name, boolean sense) {
		this.name = name;
		this.sense = sense;
	}

	@Override
	public String toString() {
		return (this.sense?"(":"~(") + this.name + "=.EMPTY.)";
	}

}
