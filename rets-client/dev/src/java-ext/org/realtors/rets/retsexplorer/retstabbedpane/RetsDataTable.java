package org.realtors.rets.retsexplorer.retstabbedpane;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultSet;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.retsexplorer.util.RetsWorker;
import org.realtors.rets.retsexplorer.util.TableStringCopyHandler;
import org.realtors.rets.util.RetsFieldNameType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RetsDataTable extends JXTable {
	
	public RetsDataTable() throws RetsException {
		this(RetsFieldNameType.SystemName, null, null, null, null, null);
	}

	public RetsDataTable(RetsFieldNameType retsFieldNameType, String keyfield, SearchResultSet data, Map<String,MTable> mTables, JLabel infoLabel, JProgressBar progressBar) throws RetsException {
		this(new RetsDataTableModel(retsFieldNameType, keyfield, data, mTables, infoLabel, progressBar));
	}
	
	public RetsDataTable(RetsDataTableModel model) {
		super(model);
		initialize();
	}
	
	private void initialize() {
		setAutoCreateRowSorter(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		setColumnControlVisible(true);
		setAutoscrolls(true);
		
		// Copy support
		setTransferHandler(new TableStringCopyHandler());
		getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				packAll();
			}});
		
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		
		addHighlighter(new ShadingColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component renderer,ComponentAdapter adapter) {
				if (adapter.isSelected()) return false;
				int[] rows = RetsDataTable.this.getSelectedRows();
				if (rows==null || rows.length <= 0) return false;
				for (int row : rows) {
					if (adapter.row == row) return true;
				}
				return false;
			}
		}));
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (isDataChanged(e) || isStructureChanged(e) || e.getColumn()==TableModelEvent.ALL_COLUMNS && getColumnCount() > 0) {
			try {
				packAll();
			} catch (Exception shhh) {
				// packAll() may results in various calls to a buggy ColumnFactory class from SwingX that seems to cause random NPEs
			}
		} else {
			try {
				packColumn(e.getColumn(), -1);
			} catch (Exception shhh) {
				// Eat it for the same reason as above
			}
		}
	}

	public RetsDataTableModel getRetsDataTableModel() {
		TableModel model = getModel();
		if (!(model instanceof RetsDataTableModel)) {
			Class<?> actual = (model == null) ? null : model.getClass();
			throw new IllegalStateException(
					String.format("Model must be instance of %s, actual type was %s", RetsDataTableModel.class, actual));
		}
		return (RetsDataTableModel)model;
	}
	
	@Override
	public void setModel(TableModel dataModel) {
		if(dataModel == null) {
			throw new IllegalArgumentException("Cannot set a null table model");
		}
		if (!(dataModel instanceof RetsDataTableModel)) {
			Class<?> actual = (dataModel == null) ? null : dataModel.getClass();
			throw new IllegalArgumentException(
					String.format("Model must be instance of %s, actual type was %s", RetsDataTableModel.class, actual ));
		}
		super.setModel(dataModel);
	}
	
	
	
	
	public void setData(SearchResultSet data) throws RetsException {
		setData(data, null);
	}

	
	public void setData(SearchResultSet data, Map<String,MTable> mTables) throws RetsException {
		final RetsDataTableModel model = getRetsDataTableModel();
		if (model!= null) {
			model.setData(data, (mTables != null) ? mTables : model.getMTables(), null, null);
		}
	}

	
	public static class RetsDataTableModel extends AbstractTableModel {

		private List<String> columns;
		private List<List<Object>> rows;
		private Map<String,MTable> mTables;
		private List<Class<?>> columnClasses;
		private Map<String,Class<?>> dataTypeClasses;
		private RetsFieldNameType retsFieldNameType;
		private String keyfield;
		private boolean keyfieldPresent;
		private int keyfieldColumnIndex;

		public RetsDataTableModel() throws RetsException {
			this(RetsFieldNameType.SystemName, null,null, null, null, null);
		}

		public RetsDataTableModel(RetsFieldNameType retsFieldNameType, String keyfield, final SearchResultSet data, final Map<String,MTable> mTables, JLabel infoLabel, JProgressBar progressBar) throws RetsException {
			super();
			setRetsFieldNameType(retsFieldNameType);
			setKeyfield(keyfield);
			setData(data,mTables, infoLabel, progressBar);
		}

		public void setKeyfield(String keyfield) {
			this.keyfield = keyfield;
		}

		public String getKeyfield() {
			return this.keyfield;
		}

		private void setKeyfieldPresent(boolean present) {
			this.keyfieldPresent = present;
		}

		/** Returns whether or not the keyfield column is actually present in the */
		public boolean isKeyfieldPresent() {
			return this.keyfieldPresent;
		}

		public int getKeyfieldColumnIndex() {
			return this.keyfieldColumnIndex;
		}

		private void setKeyfieldColumnIndex(int index) {
			this.keyfieldColumnIndex = index;
		}

		public void setRetsFieldNameType(RetsFieldNameType retsFieldNameType) {
			this.retsFieldNameType = retsFieldNameType;
		}

		public int getColumnCount() {
			return (getColumns() == null) ? 0 : getColumns().size();
		}

		public int getRowCount() {
			return (getRows() == null) ? 0 : getRows().size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			indexCheck(rowIndex, columnIndex);
			return getRows().get(rowIndex).get(columnIndex);
		}

		
		@Override
		public int findColumn(String columnName) {
			List<String> cols = getColumns();
			if(cols == null) {
				return -1;
			}
			int colCount = getColumnCount();
			for (int col = 0; col < colCount; col++) {
				if(StringUtils.equals(cols.get(col), columnName)) {
					return col;
				}
			}
			return -1;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			columnCheck(columnIndex);
			return getColumnClasses().get(columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			columnCheck(column);
			MTable mTable = this.mTables.get(getColumns().get(column));
			if (mTable != null) {
				return this.retsFieldNameType.getFieldName(mTable);
			}
			return getColumns().get(column);
		}

		private void setData(final SearchResultSet data, Map<String,MTable> mTables, final JLabel infoLabel, JProgressBar progBar) throws RetsException {
			//null check because some constructors pass in a null value for this
			final JProgressBar progressBar;
			if (progBar == null) progressBar = new JProgressBar();
			else progressBar = progBar;
			
			setMTables(mTables);
			setColumns(null);
			setRows(null);

			if (data == null) {
				progressBar.setString("Done");
				progressBar.setValue(progressBar.getMaximum());
				infoLabel.setText(infoLabel.getText() + " No Results");
				return;
			}

			String[] dataColumns = data.getColumns();
			if (dataColumns == null) {
				progressBar.setString("Done");
				progressBar.setValue(progressBar.getMaximum());
				infoLabel.setText(infoLabel.getText() + " No Results");
				return;
			}

			final List<String> cols = getColumns();
			final String key = getKeyfield();

			setKeyfieldPresent(false);
			setKeyfieldColumnIndex(-1);

			if (key != null) {
				int index = 0;
				for (String columnName : dataColumns) {
					if (StringUtils.equals(key,columnName)) {
						setKeyfieldPresent(true);
						if (getKeyfieldColumnIndex() < 0 ) {
							setKeyfieldColumnIndex(index);
						}
					}
					index++;
					cols.add(columnName);
				}
			} else {
				cols.addAll(Arrays.asList(dataColumns));
			}
			
			final int columnCount = getColumnCount();
			final List<List<Object>> newRowList = getRows();
			
			final int maxCount = data.getCount() < progressBar.getMaximum() ? data.getCount() : progressBar.getMaximum();
			progressBar.setMaximum(maxCount);
			
			class Message {
				public Integer count;
				public List<Object> row;
			}
			class BuildListings extends RetsWorker<Void, Message> { //pull listings over the wire *without* locking the gui
				double startTime = System.currentTimeMillis();
				ArrayList<List<Object>> rowBacklog = new ArrayList<List<Object>>();
				double lastUpdateTime = 0;
				int updateNum = 0;
		        @Override
				protected Void doInBackgroundWithPopup() throws Exception {
	        		Integer count = 0;
					while(data.hasNext()) {
						String[] pulledRow = data.next(); //this is where it pulls the data across the line
						//TODO: if this becomes an issue for more than one source(cmlsct, tour proptypes), run a tally of all bad rows and display a summary exception at the end
						if (pulledRow.length != columnCount) throw new Exception("Error processing queried listings. Expecting " + columnCount + " columns, and got " + pulledRow.length +":");
						Message m = new Message();
						m.row = Lists.newArrayList();
						Object value = null;
						for (int col = 0; col < columnCount; col++) {
							value = ConvertUtils.convert(pulledRow[col], getColumnClasses().get(col));
							m.row.add(value);
						}
						m.count = ++count;
						publish(m);
					}
		            return null;
		        }

		        @Override
				protected void processWithPopup(List<Message> listingNums) {
		            Integer number = listingNums.get(listingNums.size() - 1).count;
		            progressBar.setValue(number);
					progressBar.setString("Acquiring Listing " + number + " of " + maxCount);
					Integer currentNum = newRowList.size();
					this.updateNum++; //this is so that I don't start backlogging when it first creates the table.
					//update the table constantly up until it simply takes too long between each update. 
					//this is because there are more listings pulled than what it can update in any given time.
					if (this.updateNum > 3 && this.lastUpdateTime > 250) {
						for (Message pair : listingNums) {
							this.rowBacklog.add(pair.row); //add the listing to the table model
						}
					}
					else {
						for (Message pair : listingNums) {
							newRowList.add(pair.row); //add the listing to the table model
						}
						double start = System.currentTimeMillis();
						fireTableRowsInserted(currentNum, newRowList.size()-1); //tell the table it has new rows, and see how long it takes to update
						this.lastUpdateTime = System.currentTimeMillis() - start;
					}
		        }
		        
		        @Override
				protected void doneWithPopup() {
		        	progressBar.setValue(progressBar.getMaximum());
		        	progressBar.setString("Building Listing Table");
		        	if (infoLabel != null) {
			        	String text = infoLabel.getText();
			        	double totalTimeSecs = (System.currentTimeMillis() - this.startTime) / 1000;
			        	String secondsStr = String.format("%.2f",totalTimeSecs);
			    		text += String.format("%s of %s result%s in %s second%s", newRowList.size()+this.rowBacklog.size(), maxCount, (maxCount == 1) ? "" : "s", secondsStr, "1.00".equals(secondsStr) ? "" : "s");
			    		infoLabel.setText(text);
		        	}
		        	if (!this.rowBacklog.isEmpty()) {
		        		final int initSize = newRowList.size();
		        		newRowList.addAll(this.rowBacklog); //add all backlogged rows to the table, then tell it that you did
		        		if (!SwingUtilities.isEventDispatchThread()) {
		        			SwingUtilities.invokeLater(new Runnable() {
		        				public void run() {
		        					fireTableRowsInserted(initSize, newRowList.size()-1);
		        				}
		        			});
		        		} else {
		        			fireTableRowsInserted(initSize, newRowList.size()-1);
		        		}
		        	}
					progressBar.setString("Done");
		        }
		    }
			
			BuildListings listingBuilder = new BuildListings();
			listingBuilder.execute(); //run thread
		}

		public Map<String, MTable> getMTables() {
			return this.mTables;
		}

		public void setMTables(Map<String, MTable> mTables) {
			this.mTables = mTables;
			setColumnClasses(null);			
		}

		private List<Class<?>> getColumnClasses() {
			if(this.columnClasses ==null) {
				this.columnClasses = Lists.newArrayList();
				int columnCount = getColumnCount();
				
				for (int columnIndex=0; columnIndex<columnCount; columnIndex++) {
					String columnName = 
						StringUtils.defaultString(getColumns().get(columnIndex));
					String columnDataType = getDataTypeFromMTables(columnName);
					Class<?> columnClass = getDataTypeClasses().get(
							StringUtils.trimToEmpty(columnDataType));

					if (columnClass == null) {
						columnClass = String.class;
					}

					this.columnClasses.add(columnClass);
				}
				
			}
			return this.columnClasses;
		}

		private void setColumnClasses(List<Class<?>> columnClasses) {
			this.columnClasses = columnClasses;
		}

		private List<String> getColumns() {
			if (this.columns == null) {
				this.columns = Lists.newArrayList();
			}
			return this.columns;
		}

		private List<List<Object>> getRows() {
			if (this.rows == null) {
				this.rows = Lists.newLinkedList();
			}
			return this.rows;
		}

		private void setColumns(List<String> columns) {
			if (this.columns != columns) {
				this.columns = columns;
				setColumnClasses(null);
			}
		}

		private void setRows(List<List<Object>> rows) {
			this.rows = rows;
		}

		private String getDataTypeFromMTables(String columnName) {
			if (this.mTables == null) {
				return null;
			}
			MTable mTable = this.mTables.get(columnName);
			if(mTable== null) {
				return null;
			}
			return mTable.getDataType();
		}

		private Map<String,Class<?>> getDataTypeClasses() {
			if (this.dataTypeClasses == null) {
				this.dataTypeClasses = Maps.newHashMap();
				// TODO: Use another class besides String for Date, DateTime, and Time types
				// TODO: Temp - make these all String.class due to rendering inconsistencies across different LAFs
				this.dataTypeClasses.put("Boolean", String.class);
				this.dataTypeClasses.put("Character",String.class);
				this.dataTypeClasses.put("Date",String.class);
				this.dataTypeClasses.put("DateTime",String.class);
				this.dataTypeClasses.put("Time",String.class);
				this.dataTypeClasses.put("Tiny",String.class);
				this.dataTypeClasses.put("Small",String.class);
				this.dataTypeClasses.put("Int",String.class);
				this.dataTypeClasses.put("Long",String.class);
				this.dataTypeClasses.put("Decimal",String.class);
			}
			return this.dataTypeClasses;
		}

		private void indexCheck(int rowIndex,int columnIndex) throws IndexOutOfBoundsException {
			rowCheck(rowIndex);
			columnCheck(columnIndex);
			List<Object> row = getRows().get(rowIndex);
			if (getRows() == null || columnIndex >= row.size()) {
				throw new IndexOutOfBoundsException(
						String.format("Table data is non-rectangular: row %d has %d columns when %d were expected",
								rowIndex,row.size(),
								getColumnCount()));
			}
		}

		private void rowCheck(int rowIndex) throws IndexOutOfBoundsException {
			if (getRows() == null || rowIndex < 0 || rowIndex >= getRowCount()) {
				throw new IndexOutOfBoundsException(
						String.format("Invalid row index: %d",rowIndex));
			}
		}

		private void columnCheck(int columnIndex) throws IndexOutOfBoundsException {
			if (columnIndex < 0 || columnIndex >= getColumnCount()) {
				throw new IndexOutOfBoundsException(String.format("Invalid column index: %d",columnIndex));
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

	}
	
}

