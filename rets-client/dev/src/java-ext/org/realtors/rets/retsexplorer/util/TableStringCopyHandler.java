package org.realtors.rets.retsexplorer.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.apache.commons.lang.SystemUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class TableStringCopyHandler extends TransferHandler {
	
	public TableStringCopyHandler() {
		super();
	}
	
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return false;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return false;
	}

	private Transferable copySelectedColumns(JTable table) {
        int[] colIndices = table.getSelectedColumns();
        if (colIndices == null || colIndices.length <= 0) return null;
        List<String> rowsCopy = Lists.newArrayList(); 
        for (int row = 0; row < table.getRowCount(); row++) {
        	List<String> rowCopy = Lists.newArrayList();
	        for (int col : colIndices) {
	    		Object value = table.getValueAt(row, col);
	    		rowCopy.add((value == null) ? "" : String.valueOf(value));
	        }
	        rowsCopy.add(Joiner.on("\t").join(rowCopy));
        }
        return new StringSelection(Joiner.on(SystemUtils.LINE_SEPARATOR).join(rowsCopy));
	}
	
	private Transferable copySelectedRows(JTable table) {
        int[] rowIndices = table.getSelectedRows();
        if (rowIndices == null || rowIndices.length <= 0) return null;
        List<String> rowsCopy = Lists.newArrayList();
        for (int row : rowIndices) {
        	List<String> rowCopy = Lists.newArrayList();
	        for (int col = 0; col < table.getColumnCount(); col++) {
	    		Object value = table.getValueAt(row, col);
	    		rowCopy.add((value == null) ? "" : String.valueOf(value));
	        }
	        rowsCopy.add(Joiner.on("\t").join(rowCopy));
        }
        return new StringSelection(Joiner.on(SystemUtils.LINE_SEPARATOR).join(rowsCopy));
	}
	
	private Transferable copySelectedCell(JTable table) {
		int row = table.getSelectedRow();
		if (row < 0 || row >= table.getRowCount()) return null;
		int col = table.getSelectedColumn();
		if (col < 0 || col >= table.getColumnCount()) return null;
		Object value = table.getValueAt(row, col);
		return new StringSelection((value == null) ? "" : String.valueOf(value));
	}
	
	private Transferable copySelectedInterval(JTable table) {
		int rowIndexStart = table.getSelectedRow();
		int rowIndexEnd = table.getSelectionModel().getMaxSelectionIndex();
		int colIndexStart = table.getSelectedColumn();
		int colIndexEnd = table.getColumnModel().getSelectionModel().getMaxSelectionIndex();
		if (rowIndexStart < 0 || rowIndexStart >= table.getRowCount()) return null;
		if (rowIndexEnd < 0 || rowIndexEnd >= table.getRowCount()) return null;
		if (colIndexStart < 0 || colIndexStart >= table.getColumnCount()) return null;
		if (colIndexEnd < 0 || colIndexEnd >= table.getColumnCount()) return null;
		List<String> rowsCopy = Lists.newArrayList();
		for (int row = rowIndexStart; row <= rowIndexEnd; row++) {
			List<String> rowCopy = Lists.newArrayList();
			for (int col = colIndexStart; col <= colIndexEnd; col++) {
				if (table.isCellSelected(row, col)) {
					Object value = table.getValueAt(row, col);
					rowCopy.add((value == null) ? "" : String.valueOf(value));
				}
			}
			rowsCopy.add(Joiner.on("\t").join(rowCopy));
		}
		return new StringSelection(Joiner.on(SystemUtils.LINE_SEPARATOR).join(rowsCopy));
	} 
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		if (!(c instanceof JTable)) return null;
		JTable table = (JTable)c;
		// Column selection
		if (table.getColumnSelectionAllowed() && !table.getRowSelectionAllowed()) {
			return copySelectedColumns(table);
		}
		// Row selection
		if (!table.getColumnSelectionAllowed() && table.getRowSelectionAllowed()) {
			return copySelectedRows(table);
		}
		// No selection allowed at all?
		if (!table.getCellSelectionEnabled()) return null;
		// Single cell selection
		if (table.getSelectionModel().getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
			return copySelectedCell(table);
		}
		// Interval (single or multiple) selection
		return copySelectedInterval(table);
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

}