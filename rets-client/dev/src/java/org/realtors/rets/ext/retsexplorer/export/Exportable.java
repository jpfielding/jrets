package org.realtors.rets.ext.retsexplorer.export;

public interface Exportable {
	
	String getExportableName();
	
	Exporter[] getExporters();
}