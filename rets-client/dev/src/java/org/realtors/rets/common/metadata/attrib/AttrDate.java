/*
 * cart:  CRT's Awesome RETS Tool
 *
 * Author: David Terrell
 * Copyright (c) 2003, The National Association of REALTORS
 * Distributed under a BSD-style license.  See LICENSE.TXT for details.
 */
package org.realtors.rets.common.metadata.attrib;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.realtors.rets.common.metadata.AttrType;
import org.realtors.rets.common.metadata.MetaParseException;

public class AttrDate implements AttrType<Date> {
	@Override
	public Date parse(String value, boolean strict) throws MetaParseException {
		Date d;
		try {
			d = this.df.parse(value);
		} catch (ParseException e) {
			if( strict ) 
				throw new MetaParseException(e);
			return null;
		}
		return d;
	}

	@Override
	public String render(Date value) {
		Date date = value;
		return this.df.format(date);
	}

	@Override
	public Class<Date> getType() {
		return Date.class;
	}

	private DateFormat df = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss z");
}
