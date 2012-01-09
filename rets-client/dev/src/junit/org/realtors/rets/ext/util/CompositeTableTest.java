package org.realtors.rets.ext.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.realtors.rets.ext.db.Table;

import com.google.common.collect.Lists;

public class CompositeTableTest {

	@Test
	public void iterates() {
		ArrayList<String> cols = Lists.newArrayList("a","b","c");
		ArrayList<String> row1 = Lists.newArrayList("1","2","3");
		ArrayList<String> row2 = Lists.newArrayList("11","22","33");
		ArrayList<String> row3 = Lists.newArrayList("111","222","333");
		ArrayList<String> row4 = Lists.newArrayList("1111","2222","3333");
		
		Table table1 = wrap(cols, row1, row2);
		Table table2 = wrap(cols, row3, row4);

		CompositeTable comp = new CompositeTable(Lists.newArrayList(table1,table2).iterator());
		
		Assert.assertEquals(cols, comp.getColumnNames());
		Assert.assertEquals(row1, comp.next());
		Assert.assertEquals(row2, comp.next());
		Assert.assertEquals(row3, comp.next());
		Assert.assertEquals(row4, comp.next());
		Assert.assertFalse(comp.hasNext());
	}
	
	private Table wrap(final List<String> cols, final List<String>... rows) {
		final Iterator<List<String>> temp = Lists.newArrayList(rows).iterator();
		return new Table(){
			public boolean hasNext() {
				return temp.hasNext();
			}
			public List<String> next() {
				return temp.next();
			}
			public void remove() {
				temp.remove();
			}
			public void close() throws IOException {
				// noop
			}
			public List<String> getColumnNames() {
				return cols;
			}};
	}
}

