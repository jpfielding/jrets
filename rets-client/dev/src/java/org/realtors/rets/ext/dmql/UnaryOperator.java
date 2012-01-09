/**
 * 
 */
package org.realtors.rets.ext.dmql;

public enum UnaryOperator {
	Not("~");

	private String retsDmql;
	UnaryOperator(String retsDmql) {
		this.retsDmql = retsDmql;
	}
	public String toRetsDmql() {
		return this.retsDmql;
	}
}