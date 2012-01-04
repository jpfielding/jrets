package org.realtors.rets.ext.util.dmql;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberRange implements Range {

	public static final NumberFormat INTEGER = new DecimalFormat("0");

	private NumberFormat formatter = INTEGER;
	private Number from;
	private Number to;

	public NumberRange(Number from, Number to) {
		this.from = from;
		this.to = to;
	}

	public NumberFormat getFormater(){
		return this.formatter;	
	}
	public void setFormater( NumberFormat format ){
		this.formatter = format;
	}
	
	@Override
	public String toString(){
		if( this.from == null && this.to == null ) return "";
		if( this.to == null ) return this.formatter.format(this.from)+"+";
		if( this.from == null ) return this.formatter.format(this.to)+"-";
		return this.formatter.format(this.from) +"-"+this.formatter.format(this.to);
	}
}
