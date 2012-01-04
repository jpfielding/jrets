package org.realtors.rets.util.dmql;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

public class DateRangeTest  {

	public DateTime dateTime = new DateTime(2112, 12, 31, 23, 59, 59, 0, DateTimeZone.forID("America/New_York"));

	@Test /** netledger #538,#549 */
	public void gmtConversion() throws Exception {
		Assert.assertEquals("2113-01-01T04:59:59+", new DateRange(this.dateTime,null).toString());
	}
	
	@Test
	public void noConversion() throws Exception {
		Assert.assertEquals("2112-12-31T23:59:59+", new DateRange(this.dateTime,null, DateRange.DATETIME, this.dateTime.getZone().getID()).toString());
	}
	
	@Test
	public void timeFormat() throws Exception {
		Assert.assertEquals("04:59:59+", new DateRange(this.dateTime,null, DateRange.TIME).toString());
	}
	
	@Test
	public void dateFormat() throws Exception {
		Assert.assertEquals("2113-01-01+", new DateRange(this.dateTime,null, DateRange.DATE).toString());
	}
	
	@Test
	public void until() throws Exception {
		Assert.assertEquals("2113-01-01-", new DateRange(null, this.dateTime,DateRange.DATE).toString());
	}

	@Test 
	public void range() throws Exception {
		DateTime from = new DateTime(this.dateTime);
		DateTime until = new DateTime(from.plusDays(3));
		Assert.assertEquals("2113-01-01T04:59:59-2113-01-04T04:59:59", new DateRange(from,until).toString());
	}
}
