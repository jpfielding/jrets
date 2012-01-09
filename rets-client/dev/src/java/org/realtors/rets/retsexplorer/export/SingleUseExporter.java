package org.realtors.rets.retsexplorer.export;

import java.io.File;

/** Can only be used once, then the internal reference to target is cleared */
public abstract class SingleUseExporter<T> extends AbstractExporter implements Exporter {

	private T target;
	
	public SingleUseExporter(String name, String description, T target) {
		super(name, description);
		if (target == null) {
			throw new NullPointerException("Null export target");
		}
		this.target = target;
	}
	
	public final void export(File path) throws Exception {
		if (this.target == null) {
			throw new IllegalStateException("Exporter can only be used once");
		}
		try {
			doExport(path);
		} finally {
			this.target = null;
		}
	}
	
	protected final T getTarget() {
		return this.target;
	}
	
	protected abstract void doExport(File path) throws Exception;
	
}
