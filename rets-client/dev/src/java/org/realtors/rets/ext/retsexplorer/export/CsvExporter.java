package org.realtors.rets.ext.retsexplorer.export;

public abstract class CsvExporter<T> extends SingleUseExporter<T> implements Exporter {

	public CsvExporter(T target) {
		super("CSV", "Comma-separated Values Document", target);
	}

}
