package org.realtors.rets.util.db;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

public interface Table extends Iterator<List<String>>, Closeable{
	
	public List<String> getColumnNames();

}
