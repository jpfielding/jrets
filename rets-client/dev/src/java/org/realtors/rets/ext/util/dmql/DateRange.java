package org.realtors.rets.ext.util.dmql;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/** per the rets spec, dates here are toString'd to GMT */
public class DateRange implements Range {
	
	public static final String GMT = "GMT";
	
	public static final String DATE = "yyyy-MM-dd";
	public static final String TIME = "HH:mm:ss"; // full precsion (optional) needs the "HH:mm:ss.SSS" format, with the last piece optional
	public static final String DATETIME = DATE +"'T'"+ TIME;
	
	private String tz = "GMT";
	private String format = DATETIME;
	private DateTime from;
	private DateTime to;

	public DateRange(DateTime from, DateTime to) {
		this(from, to, DATETIME, "GMT");
	}
	public DateRange(DateTime from, DateTime to, String format) {
		this(from, to, format, "GMT");
	}
	public DateRange(DateTime from, DateTime to, String format, String tz) {
		this.from = from;
		this.to = to;
		this.format = format;
		this.tz = tz;
	}
	
	public String getFormat(){
		return this.format;
	}
	
	public String getTimeZone(){
		return this.tz;
	}

	@Override
	public String toString(){
		if( this.from == null && this.to == null ) return "";
		if( this.to == null ) return this.from.toMutableDateTime(DateTimeZone.forID(this.tz)).toString(this.format)+"+";
		if( this.from == null ) return this.to.toMutableDateTime(DateTimeZone.forID(this.tz)).toString(this.format)+"-";
		return this.from.toMutableDateTime(DateTimeZone.forID(this.tz)).toString(this.format) +"-"+this.to.toMutableDateTime(DateTimeZone.forID(this.tz)).toString(this.format);
	}
	
}

