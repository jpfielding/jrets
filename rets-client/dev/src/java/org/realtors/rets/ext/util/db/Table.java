package org.realtors.rets.ext.util.db;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

public interface Table extends Iterator<List<String>>, Closeable{
	
	public List<String> getColumnNames();

}
