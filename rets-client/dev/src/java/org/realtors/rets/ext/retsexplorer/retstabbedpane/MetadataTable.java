package org.realtors.rets.ext.retsexplorer.retstabbedpane;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;
import org.realtors.rets.common.metadata.AttrType;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.ext.retsexplorer.util.GuiUtils;
import org.realtors.rets.ext.retsexplorer.util.TableStringCopyHandler;

import com.google.common.collect.Maps;

public class MetadataTable extends JXTable {

	static final List<String> METADATA_COLUMNS = new ArrayList<String>() {
		{
			final Map<String, AttrType> tableAttrs = Maps.newLinkedHashMap();
			new MTable() {
				{
					addAttributesToMap(tableAttrs);
				}
			};
			add(MTable.SYSTEMNAME);
			add(MTable.STANDARDNAME);
			add(MTable.LONGNAME);
			add(MTable.LOOKUPNAME);
			add(MTable.DATATYPE);
			add(MTable.DEFAULT);
			add(MTable.SEARCHABLE);
			tableAttrs.keySet().removeAll(this);
			addAll(tableAttrs.keySet());
		}
	};

	private MTable[] mTables;
	
	public MetadataTable(Metadata metadata, MTable... mTables) {
		super(new MetadataTableModel(metadata, mTables));
		this.mTables = mTables;
		initialize();
	}

	private void initialize() {
		setAutoCreateRowSorter(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setColumnControlVisible(true);

		// Copy support
		setTransferHandler(new TableStringCopyHandler());
		getActionMap().put(
				TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());

		// Lookup button support
		MetadataTableModel model = getMetadataTableModel();
		int lookupColumnIndex = model.getLookupColumnIndex();
		LookupNameCell cell = new LookupNameCell(this);
		TableColumn tablecolumn = getColumnModel().getColumn(lookupColumnIndex);
		tablecolumn.setCellEditor(cell);
		tablecolumn.setCellRenderer(cell);
		
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);

		addHighlighter(new ShadingColorHighlighter(new HighlightPredicate() {
			public boolean isHighlighted(Component renderer,ComponentAdapter adapter) {
				if (adapter.isSelected()) return false;
				int[] rows = MetadataTable.this.getSelectedRows();
				if (rows==null || rows.length <= 0) return false;
				for (int row : rows) {
					if (adapter.row == row) return true;
				}
				return false;
			}
		}));
		
		updateUI();
	}

	public MTable[] getMTables() {
		return this.mTables;
	}

	public void setMTables(MTable... mTables) {
		this.mTables = mTables;
		getMetadataTableModel().setMTables(mTables);
	}

	public MetadataTableModel getMetadataTableModel() {
		TableModel model = getModel();
		if (!(model instanceof MetadataTableModel)) {
			Class<?> actual = (model == null) ? null : model.getClass();
			throw new IllegalStateException(String.format("Model must be instance of %s, actual type was %s",MetadataTableModel.class, actual));
		}
		return (MetadataTableModel) model;
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (dataModel == null) {
			throw new IllegalArgumentException("Cannot set a null table model");
		}
		if (!(dataModel instanceof MetadataTableModel)) {
			Class<?> actual = (dataModel == null) ? null : dataModel.getClass();
			throw new IllegalArgumentException(String.format(
					"Model must be instance of %s, actual type was %s",
					MetadataTableModel.class, actual));
		}
		super.setModel(dataModel);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (isVisible() && isShowing()) {
					packAll();
				}
			}
		});
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		Dimension size = getPreferredSize();
		if (size == null || (size.height == 0 && size.width == 0)) {
			return super.getPreferredScrollableViewportSize();
		}
		return size;
	}
	
	public static class MetadataTableModel extends AbstractTableModel {

		private int lookupColumnIndex = -1;

		private ArrayList<MTable> mTables = new ArrayList<MTable>();
		private Metadata metadata;

		public MetadataTableModel(Metadata metadata, MTable[] mTables) {
			super();
			setMTables(mTables);
			setMetadata(metadata);
		}

		public MTable[] getMTables() {
			return this.mTables.toArray(new MTable[this.mTables.size()]);
		}

		public void setMTables(MTable... mTables) {
			if (this.mTables == null || mTables == null) this.mTables = new ArrayList<MTable>();
			else {
				this.mTables.clear();
				this.mTables.addAll(Arrays.asList(mTables));
			}
		}
		
		public void clearMTables() {
			this.mTables.clear();
			this.fireTableDataChanged();
		}

		public Metadata getMetadata() {
			return this.metadata;
		}

		public void setMetadata(Metadata metadata) {
			this.metadata = metadata;
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			columnindexCheck(columnIndex);
			return MetadataTable.METADATA_COLUMNS.get(columnIndex);
		}

		public int getColumnCount() {
			return (MetadataTable.METADATA_COLUMNS == null) ? 0 : MetadataTable.METADATA_COLUMNS.size();
		}

		public int getRowCount() {
			return (getMTables() == null) ? 0 : getMTables().length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			indexCheck(rowIndex, columnIndex);
			return getMTables()[rowIndex].getAttributeAsString(MetadataTable.METADATA_COLUMNS.get(columnIndex));
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == getLookupColumnIndex();
		}

		@Override
		public void fireTableStructureChanged() {
			this.lookupColumnIndex = -1;
			super.fireTableStructureChanged();
		}
		
		public void appendRows(MTable... mtables) {
			int initSize = this.mTables.size();
			this.mTables.addAll(Arrays.asList(mtables));
			fireTableRowsInserted(initSize, this.mTables.size()-1);
		}

		int getLookupColumnIndex() {
			if (this.lookupColumnIndex < 0 || this.lookupColumnIndex >= MetadataTable.METADATA_COLUMNS.size()) {
				this.lookupColumnIndex = findColumn(MTable.LOOKUPNAME);
			}
			return this.lookupColumnIndex;
		}

		void indexCheck(int rowIndex, int columnIndex) {
			columnindexCheck(columnIndex);
			rowIndexCheck(rowIndex);
		}

		void rowIndexCheck(int rowIndex) {
			if (getMTables() == null || rowIndex >= getMTables().length) {
				throw new IndexOutOfBoundsException(String.format("Invalid row index: %d", rowIndex));
			}
		}

		void columnindexCheck(int columnIndex) {
			if (MetadataTable.METADATA_COLUMNS == null || columnIndex >= MetadataTable.METADATA_COLUMNS.size()) {
				throw new IndexOutOfBoundsException(String.format("Invalid column index: %d", columnIndex));
			}
		}
	}

	// TODO: Refactor this out into a standalone class that can wrap
	// any table cell rendered with a "..." button, with customizable action(s)
	private static class LookupNameCell extends AbstractCellEditor implements
			TableCellRenderer, TableCellEditor {
		MetadataTable table;

		JPanel editorPanel;
		JButton editorButton;
		JLabel editorLabel;
		final JLabel emptyEditorLabel = createEmptyLabel();

		JPanel rendererPanel;
		JButton rendererButton;
		JLabel rendererLabel;
		final JLabel emptyRendererLabel = createEmptyLabel();

		final String DEFAULT_BUTTON_TEXT = "...";

		public LookupNameCell(MetadataTable table) {
			super();
			this.table = table;
		}

		public Component getTableCellRendererComponent(JTable tab, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			adjustRendererColors(isSelected);

			String text = (value == null) ? "" : StringUtils.defaultString(value.toString());

			if (StringUtils.isEmpty(text)) return this.emptyRendererLabel;
			
			getRendererLabel().setText(text);

			return getRendererPanel();
		}

		public Component getTableCellEditorComponent(JTable tab, Object value, boolean isSelected, int row, int column) {

			GuiUtils.setForeground(false, tab.getSelectionForeground(),
					getEditorButton(), getEditorLabel(), getEditorPanel(),
					this.emptyEditorLabel);
			GuiUtils.setBackground(false, tab.getSelectionBackground(),
					getEditorButton(), getEditorLabel(), getEditorPanel(),
					this.emptyEditorLabel);

			String text = (value == null) ? "" : StringUtils.defaultString(value.toString());

			if (StringUtils.isEmpty(text)) return this.emptyEditorLabel;

			getEditorLabel().setText(text);

			return getEditorPanel();
		}

		public Object getCellEditorValue() {
			Object value = null;
			if (this.table != null) {
				try {
					int row = this.table.getSelectedRow();
					int col = this.table.getSelectedColumn();
					value = this.table.getValueAt(row, col);
				} catch (Exception shhh) {
					// Shhh
				}
			}
			return StringUtils.defaultString(String.valueOf(value));
		}

		private void adjustRendererColors(boolean isSelected) {
			if (isSelected) {
				GuiUtils.setForeground(false, this.table
						.getSelectionForeground(), getRendererButton(),
						getRendererLabel(), getRendererPanel(),
						this.emptyRendererLabel);
				GuiUtils.setBackground(false, this.table
						.getSelectionBackground(), getRendererButton(),
						getRendererLabel(), getRendererPanel(),
						this.emptyRendererLabel);
			} else {
				GuiUtils.setForeground(false, this.table.getForeground(),
						getRendererButton(), getRendererLabel(),
						getRendererPanel(), this.emptyRendererLabel);
				GuiUtils.setBackground(false, this.table.getBackground(),
						getRendererButton(), getRendererLabel(),
						getRendererPanel(), this.emptyRendererLabel);
			}
		}

		JLabel createEmptyLabel() {
			JLabel label = new JLabel("");
			label.setFocusable(true);
			label.setOpaque(true);
			return label;
		}

		JButton getRendererButton() {
			if (this.rendererButton == null) {
				this.rendererButton = new JButton(this.DEFAULT_BUTTON_TEXT);
				this.rendererButton.setDoubleBuffered(true);
				this.rendererButton.setMargin(new Insets(0, 0, 0, 0));
				if (SystemUtils.IS_OS_MAC_OSX) {
					this.rendererButton.putClientProperty("JButton.buttonType","bevel");
					this.rendererButton.putClientProperty("JComponent.sizeVariant", "mini");
				}
			}
			return this.rendererButton;
		}

		JLabel getRendererLabel() {
			if (this.rendererLabel == null) {
				this.rendererLabel = new JLabel();
				this.rendererLabel.setDoubleBuffered(true);
				this.rendererLabel.setOpaque(false);
				this.rendererLabel.setAlignmentX(LEFT_ALIGNMENT);
				this.rendererLabel.setFont(this.rendererLabel.getFont().deriveFont(Font.PLAIN));
			}
			return this.rendererLabel;
		}

		JPanel getRendererPanel() {
			if (this.rendererPanel == null) {
				JPanel panel = new JPanel(true);
				panel.setOpaque(true);
				panel.setAlignmentX(LEFT_ALIGNMENT);
				panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

				GroupLayout layout = new GroupLayout(panel);
				panel.setLayout(layout);

				Component glue = Box.createHorizontalGlue();

				Group hGroup = layout.createSequentialGroup()
				.addComponent(getRendererButton())
				.addComponent(getRendererLabel())
				.addComponent(glue);

				Group vGroup = layout.createParallelGroup(Alignment.BASELINE,false)
					.addComponent(getRendererButton())
					.addComponent(getRendererLabel())
					.addComponent(glue);

				layout.setHorizontalGroup(hGroup);
				layout.setVerticalGroup(vGroup);

				layout.setAutoCreateContainerGaps(false);
				layout.setAutoCreateGaps(true);
				this.rendererPanel = panel;
			}
			return this.rendererPanel;
		}

		JLabel getEditorLabel() {
			if (this.editorLabel == null) {
				this.editorLabel = new JLabel();
				this.editorLabel.setDoubleBuffered(true);
				this.editorLabel.setOpaque(false);
				this.editorLabel.setAlignmentX(LEFT_ALIGNMENT);
				this.editorLabel.setFont(this.editorLabel.getFont().deriveFont(Font.PLAIN));
			}
			return this.editorLabel;
		}

		JButton getEditorButton() {
			if (this.editorButton == null) {
				this.editorButton = new JButton(this.DEFAULT_BUTTON_TEXT);
				this.editorButton.setDoubleBuffered(true);
				this.editorButton.setMargin(new Insets(0, 0, 0, 0));
				this.editorButton.setFocusPainted(false);
				if (SystemUtils.IS_OS_MAC_OSX) {
					this.editorButton.putClientProperty("JButton.buttonType","bevel");
					this.editorButton.putClientProperty("JComponent.sizeVariant", "mini");
				}
				this.editorButton
						.addActionListener(new ErrorPopupActionListener() {
							@Override
							public void action() throws Exception {
								String lookupName = getEditorButton().getText();
								if (StringUtils.isEmpty(lookupName)) {
									return;
								}

								if (LookupNameCell.this.table == null) {
									showError("Metadata table was null");
									return;
								}

								MetadataTable tab = LookupNameCell.this.table;

								MetadataTableModel model = tab.getMetadataTableModel();
								if (model == null) {
									showError("Metadata table model was null");
									return;
								}

								Metadata metadata = model.getMetadata();
								if (metadata == null) {
									showError("Metadata was null");
									return;
								}

								int row = tab.convertRowIndexToModel(tab.getSelectedRow());
								if (row == -1) {
									showError("Unable to determine selected row");
									return;
								}

								MLookup lookup = metadata.getLookup(model.getMTables()[row]);
								if (lookup == null) {
									showError("Lookup was null");
									return;
								}

								JDialog dialog = new LookupTypesDialog(JOptionPane.getFrameForComponent(tab),lookup);
								dialog.pack();
								dialog.setLocationRelativeTo(null);
								dialog.setVisible(true);
							}

							void showError(String reason) {
								JOptionPane.showMessageDialog(null,String.format("Unable to display lookup types: %s.",reason),"Error",	JOptionPane.ERROR_MESSAGE);
							}
						});
			}
			return this.editorButton;
		}

		JPanel getEditorPanel() {
			if (this.editorPanel == null) {
				JPanel panel = new JPanel(true);
				panel.setOpaque(true);
				panel.setAlignmentX(LEFT_ALIGNMENT);
				panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

				GroupLayout layout = new GroupLayout(panel);
				panel.setLayout(layout);

				Component glue = Box.createHorizontalGlue();

				Group hGroup = layout.createSequentialGroup()
						.addComponent(getEditorButton())
						.addComponent(getEditorLabel())
						.addComponent(glue);

				Group vGroup = layout.createParallelGroup(Alignment.BASELINE,false)
					.addComponent(getEditorButton())
					.addComponent(getEditorLabel())
					.addComponent(glue);

				layout.setHorizontalGroup(hGroup);
				layout.setVerticalGroup(vGroup);

				layout.setAutoCreateContainerGaps(false);
				layout.setAutoCreateGaps(true);

				this.editorPanel = panel;
			}
			return this.editorPanel;
		}

	}

}