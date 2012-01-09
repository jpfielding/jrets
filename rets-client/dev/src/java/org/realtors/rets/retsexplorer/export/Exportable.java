package org.realtors.rets.retsexplorer.export;

public interface Exportable {
	
	String getExportableName();
	
	Exporter[] getExporters();
}