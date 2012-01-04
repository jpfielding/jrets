package org.realtors.rets.ext.retsexplorer.export;

import java.io.File;

public interface Exporter {
	
	String getName();
	
	String getDescription();
	
	void export(File path) throws Exception;
	
}
