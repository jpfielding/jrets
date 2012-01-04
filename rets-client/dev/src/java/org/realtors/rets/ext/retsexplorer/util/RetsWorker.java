package org.realtors.rets.ext.retsexplorer.util;

import java.util.List;

import javax.swing.SwingWorker;

public abstract class RetsWorker<T, V> extends SwingWorker<T, V> {

	@Override
	protected final T doInBackground() throws Exception {
		T value = null;
		try {
			value = doInBackgroundWithPopup();
		} catch (Throwable t) {
			GuiUtils.exceptionPopup(t);
		}
		return value;
	}

	@Override
	protected final void done() {
		try {
			doneWithPopup();
		} catch (Throwable t) {
			GuiUtils.exceptionPopup(t);
		}
	}

	@Override
	protected final void process(List<V> chunks) {
		try {
			processWithPopup(chunks);
		} catch (Throwable t) {
			GuiUtils.exceptionPopup(t);
		}
	}
	
	protected abstract T doInBackgroundWithPopup() throws Exception;
	
	protected void doneWithPopup() { return; }
	
	protected void processWithPopup(@SuppressWarnings("unused") List<V> chunks) { return; }

}
