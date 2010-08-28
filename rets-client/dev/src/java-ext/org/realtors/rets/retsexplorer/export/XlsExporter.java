package org.realtors.rets.retsexplorer.export;

public abstract class XlsExporter<T> extends SingleUseExporter<T> implements Exporter {

	public XlsExporter(T target) {
		super("XLS", "Microsoft Office Excel Spreadsheet", target);
	}

}
