package org.realtors.rets.ext.retsexplorer.retstabbedpane;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;
import org.realtors.rets.common.metadata.types.MLookupType;
import org.realtors.rets.ext.retsexplorer.util.TableStringCopyHandler;


public class LookupTypesTable extends JXTable {
	
	public LookupTypesTable(MLookupType... lookups) {
		super(new LookupTypesTableModel(lookups));
		initialize();
	}
	
	private void initialize() {
		setAutoCreateRowSorter(true);
		setColumnControlVisible(true);
		
		// Copy support
		setTransferHandler(new TableStringCopyHandler());
		getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
		
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		
		addHighlighter(new ShadingColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {
				if (adapter.isSelected()) return false;
				int[] rows = LookupTypesTable.this.getSelectedRows();
				if (rows==null || rows.length <= 0) return false;
				for (int row : rows) {
					if (adapter.row == row) return true;
				}
				return false;
			}
		}));
	}

	public MLookupType[] getLookupTypes() {
		LookupTypesTableModel model = getLookupTypesTableModel();
		return (model == null) ? null : model.getLookupTypes();
	}

	public void setLookupTypes(MLookupType[] lookupTypes) {
		LookupTypesTableModel model = getLookupTypesTableModel();
		if(model != null) {
			model.setLookupTypes(lookupTypes);
		}
	}

	public LookupTypesTableModel getLookupTypesTableModel() {
		TableModel model = getModel();
		if (!(model instanceof LookupTypesTableModel)) {
			Class<?> actual = (model == null) ? null : model.getClass();
			throw new IllegalStateException(
					String.format("Model must be instance of %s, actual type was %s",
							LookupTypesTableModel.class, actual ));
		}
		return (LookupTypesTableModel)model;
	}
	
	@Override
	public void setModel(TableModel dataModel) {
		if (!(dataModel instanceof LookupTypesTableModel)) {
			throw new IllegalArgumentException(
					String.format("Model must be instance of %s, actual type was %s",LookupTypesTableModel.class));
		}
		super.setModel(dataModel);
	}

	
	static class LookupTypesTableModel extends AbstractTableModel {

		private static final String[] COLUMN_NAMES = { MLookupType.VALUE,
				MLookupType.LONGVALUE, MLookupType.SHORTVALUE };
		
		private MLookupType[] lookupTypes = null;
		
		public LookupTypesTableModel(MLookupType... lookupTypes) {
			super();
			setLookupTypes(lookupTypes);
		}
		
		public MLookupType[] getLookupTypes() {
			return this.lookupTypes;
		}

		public void setLookupTypes(MLookupType... lookupTypes) {
			this.lookupTypes = lookupTypes;
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			columnindexCheck(columnIndex);
			return (COLUMN_NAMES == null) ? null : COLUMN_NAMES[columnIndex];
		}
		
		public int getColumnCount() {
			return (COLUMN_NAMES == null) ? 0 : COLUMN_NAMES.length;
		}
		
		public int getRowCount() {
			return (getLookupTypes() == null) ? 0 : getLookupTypes().length;
		}
		
		public Object getValueAt(int rowIndex, int columnIndex) {
			indexCheck(rowIndex, columnIndex);
			return getLookupTypes()[rowIndex].getAttributeAsString(
					COLUMN_NAMES[columnIndex]);
		}
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		void indexCheck(int rowIndex, int columnIndex) {
			columnindexCheck(columnIndex);
			rowIndexCheck(rowIndex);
		}
		
		void rowIndexCheck(int rowIndex) {
			if(getLookupTypes() == null 
					|| rowIndex >= getLookupTypes().length) {				
				throw new IndexOutOfBoundsException(
						String.format("Invalid row index: %d",rowIndex));
			}			
		}
		
		void columnindexCheck(int columnIndex) {
			if(COLUMN_NAMES == null 
					|| columnIndex >= COLUMN_NAMES.length) {
				throw new IndexOutOfBoundsException(
						String.format("Invalid column index: %d",columnIndex));				
			}
		}
		
		
	}
	

}