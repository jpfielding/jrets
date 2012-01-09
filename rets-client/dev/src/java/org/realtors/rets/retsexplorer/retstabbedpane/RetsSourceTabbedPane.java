package org.realtors.rets.retsexplorer.retstabbedpane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.util.WindowUtils;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SearchResultSet;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.ResourceClass;
import org.realtors.rets.ext.RetsClient;
import org.realtors.rets.ext.RetsClientConfig;
import org.realtors.rets.ext.RetsFieldNameType;
import org.realtors.rets.ext.RetsSearchType;
import org.realtors.rets.ext.RetsClient.ResponseTouch;
import org.realtors.rets.ext.db.Table;
import org.realtors.rets.ext.enhancements.SearchRequestEx;
import org.realtors.rets.ext.transaction.StreamingSearch;
import org.realtors.rets.ext.util.RetsSearchInfo;
import org.realtors.rets.ext.util.StandardRetsSearchPaging;
import org.realtors.rets.retsexplorer.export.Exportable;
import org.realtors.rets.retsexplorer.export.Exporter;
import org.realtors.rets.retsexplorer.export.MapExporter;
import org.realtors.rets.retsexplorer.export.MetadataXlsExporter;
import org.realtors.rets.retsexplorer.export.SearchResultSetCsvExporter;
import org.realtors.rets.retsexplorer.filter.FilterFactory;
import org.realtors.rets.retsexplorer.retstabbedpane.RetsDataTable.RetsDataTableModel;
import org.realtors.rets.retsexplorer.util.ButtonTabComponent;
import org.realtors.rets.retsexplorer.util.Csv;
import org.realtors.rets.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.retsexplorer.util.GuiComponentUtils;
import org.realtors.rets.retsexplorer.util.GuiUtils;
import org.realtors.rets.retsexplorer.util.QueryManager;
import org.realtors.rets.retsexplorer.util.RetsWorker;
import org.realtors.rets.retsexplorer.wirelog.WireLogConsole;

import com.google.common.base.Joiner;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

public class RetsSourceTabbedPane extends JTabbedPane implements Exportable {

	private RetsClientConfig config;
	private RetsClient client;
	private Metadata metadata;
	private ResourceClass resourceClass;
	private MTable[] mTables;
	
	private Set<QueryTabComponent> tabs = Sets.newHashSet();
	private WireLogConsole wireLogConsole;


	public RetsSourceTabbedPane(QueryManager qm, RetsClientConfig config, ResourceClass resourceClass) {
		this(qm, config, resourceClass, null, null, null, null, null, null);
	}
	
	public RetsSourceTabbedPane(QueryManager qm, RetsClientConfig config, ResourceClass resourceClass, RetsClient client, Metadata metadata, WireLogConsole wireLogConsole, MTable... mTables) {
		super();
		this.wireLogConsole = wireLogConsole;
		this.config = config;
		this.resourceClass = resourceClass;
		this.client = client;
		this.metadata = metadata;
		if (mTables != null && mTables.length > 0) {
			this.mTables = new MTable[mTables.length];
			System.arraycopy(mTables, 0, this.mTables, 0, mTables.length);
		}
		
		setBackground(UIManager.getColor("Panel.background"));
		setOpaque(true);

		//initialize the search panel and triggers
		final RetsSearchPane searchPanel = new RetsSearchPane();
		searchPanel.getKeyFieldField().setText(getKeyField());
		searchPanel.getQueryField().setText(qm.createStatusQuery(getConfig().getRetsServiceName(), getResourceClass().getResource().getName(), getResourceClass().getClassName(), getMetadata(), "ListingStatus","Status","ModificationTimestamp","Name"));

		final MetadataTable metaTable = new MetadataTable(getMetadata(), getMTables()); 
		metaTable.setBackground(UIManager.getColor("Panel.background"));
		metaTable.setOpaque(true);
		
		JScrollPane metaScroll = new JScrollPane(metaTable);
		metaScroll.setOpaque(false);
		metaScroll.getViewport().setOpaque(false);
		
		JScrollPane searchScroll = new JScrollPane(searchPanel);
		searchScroll.setOpaque(false);
		searchScroll.getViewport().setOpaque(false);
		
		addTab("Metadata", metaScroll);
		addTab("Search", searchScroll);
		
		// Support for double-click -> add field to select
		metaTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e)) return;
				if (e.getClickCount()!=2 || e.isConsumed()) return;
				e.consume();
				int row = metaTable.rowAtPoint(e.getPoint());
				if ( row<0 || row>=metaTable.getRowCount()) return;
				int col = metaTable.getMetadataTableModel().findColumn(MTable.SYSTEMNAME);
				if ( col<0 || col>=metaTable.getColumnCount(true)) return;
				Object value = metaTable.getValueAt(row, metaTable.convertColumnIndexToView(col));
				if (value == null) return;
				String field = StringUtils.trimToNull(String.valueOf(value));
				if (field == null) return;
				String newSelect = field;
				String select = StringUtils.trimToEmpty(searchPanel.getSelectField().getText());
				if (StringUtils.isNotBlank(select)) {
					if (select.matches(String.format(".*\\b%s\\b.*", Pattern.quote(field))) ) return;
					while(select.endsWith(",")) select = StringUtils.trimToEmpty(StringUtils.substringBeforeLast(select, ","));
					Set<String> fields = Sets.newLinkedHashSet(Arrays.asList(select.split(",+")));
					if ( !fields.isEmpty()) {
						fields.add(field);
						newSelect = Joiner.on(",").join(fields);
					}
				}
				searchPanel.getSelectField().setText(newSelect);
			}
		});
		
		searchPanel.getObjectsButton().addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				new RetsObjectsFrameOperator(getClient(), null, null, RetsSourceTabbedPane.this.wireLogConsole);
			}
		});
		
		class TimedSearchResultSet { //TODO: time is no longer used here, clean this up.
			SearchResultSet result;
			Exception exception;
		}
		
		searchPanel.getSearchButton().addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				
				final RetsFieldNameType fieldName = (RetsFieldNameType) searchPanel.getFieldNameCombo().getSelectedItem();
				final String keyField = searchPanel.getKeyFieldField().getText();
				final boolean hideEmpty = searchPanel.getHideEmptyCheckBox().isSelected();
				final String query = searchPanel.getQueryField().getText();
				final int limit = NumberUtils.toInt(searchPanel.getLimitField().getText(), 0);
				final String columns = searchPanel.getSelectField().getText();
				final boolean paged = searchPanel.getEnablePagingCheckBox().isSelected();
				final boolean countOnly = searchPanel.getCountOnlyCheckBox().isSelected();
				final boolean writeToFile = searchPanel.getWriteToFileCheckBox().isSelected();
				
				class SearchWorker extends RetsWorker<Object, Void> {
					private void setInputEnabled(boolean enabled) {
						searchPanel.enableAll(enabled);
					}

					@Override
					protected Object doInBackgroundWithPopup() throws Exception {
						if (writeToFile) {
							searchToFile(query, columns, limit);
							return true;
						}
						TimedSearchResultSet search = new TimedSearchResultSet();
						try {
							search.result = search(query, columns, limit, paged, countOnly);
						} catch (Exception e) {
							search.exception = e;
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									setInputEnabled(true);
								}
							});
						}
						return search;
					}
					@Override
					protected void doneWithPopup() {
						try {
							if (writeToFile) {
								if ((Boolean)get()) {
									JLabel doneLabel = new JLabel("Search results have been exported to: ");
									doneLabel.setFont(doneLabel.getFont().deriveFont(Font.PLAIN));
									doneLabel.setHorizontalAlignment(SwingConstants.CENTER);
									doneLabel.setAlignmentY(CENTER_ALIGNMENT);
									doneLabel.setAlignmentX(CENTER_ALIGNMENT);
									
									final File file = RetsSourceTabbedPane.this.fileChooser.getSelectedFile();
									
									AbstractHyperlinkAction linkAction = new AbstractHyperlinkAction(file.getAbsolutePath()) {
										public void actionPerformed(ActionEvent e) {
											try {
												Desktop.getDesktop().open(file);
											} catch (IOException e1) {
												GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e1);
											} finally {
												setVisited(true);
											}
										}
									};
									JXHyperlink link = new JXHyperlink(linkAction);
									link.setHorizontalAlignment(SwingConstants.CENTER);
									link.setAlignmentY(CENTER_ALIGNMENT);
									link.setAlignmentX(CENTER_ALIGNMENT);
									
									JPanel donePanel = new JPanel(true);
									donePanel.setLayout(new BoxLayout(donePanel, BoxLayout.PAGE_AXIS));
									donePanel.setAlignmentX(CENTER_ALIGNMENT);
									donePanel.setAlignmentY(CENTER_ALIGNMENT);
									donePanel.add(doneLabel);
									donePanel.add(Box.createVerticalStrut(8));
									donePanel.add(link);
									
									JOptionPane.showMessageDialog(RetsSourceTabbedPane.this, donePanel);
								}
								return;
							}
							TimedSearchResultSet timed = (TimedSearchResultSet)get();
							if (timed.exception != null) {
								throw timed.exception;
							}
							if (countOnly) {
								JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(RetsSourceTabbedPane.this), String.format("Count request shows %s results", timed.result.getCount()));
								return;
							}
							JScrollPane scroll = createResultsPanel(query, hideEmpty, fieldName, timed.result, keyField, limit);
							String tabTitle = (getTabCount()+1 > 3) ? String.format("Results(%d)", getTabCount()+1-2) : "Results";
							addTab(tabTitle, scroll);
							setTabComponentAt(getTabCount()-1, new QueryTabComponent(query,columns));
							setSelectedIndex(getTabCount()-1);
						} catch(Exception e) {
							GuiUtils.exceptionPopup(e.getClass().getSimpleName(), e);
						} finally {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									setInputEnabled(true);
								}
							});
						}
					}
				}
				
				SearchWorker searcher = new SearchWorker();
				searcher.setInputEnabled(false);
				searcher.execute();
			}
		});
	}
	
	private RetsClientConfig getConfig() {
		return this.config;
	}
	
	private RetsClient getClient() {
		if (this.client != null) {
			return this.client;
		}
		if (this.config == null) {
			return null;
		}
		this.client = this.config.createClient();
		return this.client;
	}

	private Metadata getMetadata() {
		if (this.metadata != null) {
			return this.metadata;
		}
		RetsClient retsClient = getClient();
		if(retsClient == null) {
			return null;
		}
		this.metadata = retsClient.getMetadata();
		return this.metadata;
	}
	
	private ResourceClass getResourceClass() {
		return this.resourceClass;
	}
	
	private MTable[] getMTables() {
		if (this.mTables != null) {
			return this.mTables;
		}

		Metadata meta = getMetadata();
		ResourceClass rClass = getResourceClass();
		
		if (meta == null || rClass == null) {
			return null;
		}

		RetsSearchType resource = rClass.getResource();
		
		if (resource == null) {
			return null;
		}
		
		String className = rClass.getClassName();
		String resourceId = resource.getName();
		
		if (resourceId == null || className == null) {
			return null;
		}
		
		MClass mClass = meta.getMClass(resourceId, className);
		
		if (mClass == null) {
			return null;
		}
		
		this.mTables = mClass.getMTables();
		return this.mTables;
	}
	
	private JScrollPane createResultsPanel(String query, boolean hideEmpty, RetsFieldNameType fieldName, SearchResultSet results, String keyField, int limit) throws RetsException {
		Map<String, MTable> mTablesMap = HashBiMap.create();
		String sysName = null;
		for (MTable mTable : getMTables() ) {
			if (mTable != null) {
				sysName = mTable.getSystemName();
				if (sysName != null) {
					mTablesMap.put(sysName, mTable);
				}
			}
		}
		
		JLabel infoLabel = new JLabel();
		infoLabel.setText("Query '" + query + "' returned ");
		final JProgressBar progressBar = new JProgressBar(0,limit);
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
		progressBar.setAlignmentX(LEFT_ALIGNMENT);
		progressBar.setMaximumSize(new Dimension(500, 20));
		progressBar.setMinimumSize(new Dimension(500, 20));
		
		final RetsDataTable dataTable = new RetsDataTable(fieldName, keyField, results, mTablesMap, infoLabel, progressBar);
		dataTable.setHorizontalScrollEnabled(true);
		dataTable.setBorder(BorderFactory.createEtchedBorder());
		dataTable.setBackground(UIManager.getColor("Panel.background"));
		dataTable.setOpaque(true);
		if (hideEmpty) {
			dataTable.setFilters(new FilterPipeline(FilterFactory.hideEmptyColumnsFilter(dataTable)));
		}
		addDataRowViewer(dataTable);
		infoLabel.setAlignmentX(LEFT_ALIGNMENT);
		infoLabel.setHorizontalTextPosition(LEFT);
		infoLabel.setHorizontalAlignment(LEFT);
		
		final JComboBox fieldNamecombo = GuiComponentUtils.createCombo(false, fieldName, RetsFieldNameType.values());
		fieldNamecombo.setMaximumSize(fieldNamecombo.getPreferredSize());
		fieldNamecombo.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						RetsDataTableModel tableModel = dataTable.getRetsDataTableModel();
						tableModel.setRetsFieldNameType((RetsFieldNameType) fieldNamecombo.getSelectedItem());
						dataTable.tableChanged(new TableModelEvent(tableModel,TableModelEvent.HEADER_ROW));
					}
				});
			}
		});
		
		final JCheckBox hideEmptyCheckBox = GuiComponentUtils.createCheckBox("Hide empty columns", hideEmpty);
		hideEmptyCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						dataTable.setFilters(null);
						for (int col=0; col<dataTable.getModel().getColumnCount(); col++) {
							dataTable.getColumnExt(dataTable.getModel().getColumnName(col)).setVisible(true);
						}
						if (e.getStateChange() == ItemEvent.SELECTED) {
							dataTable.setFilters(new FilterPipeline(FilterFactory.hideEmptyColumnsFilter(dataTable)));
						}
						dataTable.repaint();
					}
				});
			}
		});
		JLabel fieldNameLabel = GuiComponentUtils.createLabel("Field Name", fieldNamecombo);
		Component spacer = Box.createRigidArea(new Dimension(1,1));
		
		JButton objectsButton = GuiComponentUtils.createButton("Objects");
		objectsButton.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				RetsDataTableModel tableModel1 = dataTable.getRetsDataTableModel();

				int selectedRow = dataTable.getSelectedRow();
				int keyColIndex = tableModel1.getKeyfieldColumnIndex();
				String contentId = (selectedRow < 0 || keyColIndex < 0) ? "" : String.valueOf(tableModel1.getValueAt(selectedRow, keyColIndex));

				MResource resource = getMetadata().getSystem().getMResource(getResourceClass().getResource().getName());
				new RetsObjectsFrameOperator(getClient(),resource,contentId, RetsSourceTabbedPane.this.wireLogConsole);
			}
		});
		
		JPanel optionsPanel = GuiComponentUtils.createPanel();
		GroupLayout layout = new GroupLayout(optionsPanel);
		optionsPanel.setLayout(layout);
		optionsPanel.setAlignmentX(LEFT_ALIGNMENT);
		optionsPanel.setOpaque(true);
		optionsPanel.setBackground(UIManager.getColor("Panel.background"));
		
		JPanel optionsWrapper = GuiComponentUtils.createPanel();
		optionsWrapper.setLayout(new BoxLayout(optionsWrapper, BoxLayout.LINE_AXIS));
		optionsWrapper.add(optionsPanel);
		optionsWrapper.add(Box.createGlue());
		optionsWrapper.setAlignmentX(LEFT_ALIGNMENT);
		optionsWrapper.setOpaque(true);
		optionsWrapper.setBackground(UIManager.getColor("Panel.background"));

		JPanel tableWrapper = GuiComponentUtils.createPanel();
		tableWrapper.setLayout(new BorderLayout());
		tableWrapper.add(dataTable, BorderLayout.CENTER);
		tableWrapper.setOpaque(true);
		tableWrapper.setBackground(UIManager.getColor("Panel.background"));
		
		JPanel controlWrapper = GuiComponentUtils.createPanel();
		controlWrapper.setLayout(new BorderLayout());
		controlWrapper.add(Box.createVerticalGlue(), BorderLayout.NORTH);
		controlWrapper.add(dataTable.getColumnControl(), BorderLayout.SOUTH);
		controlWrapper.setOpaque(true);
		controlWrapper.setBackground(UIManager.getColor("Panel.background"));
		
		JScrollPane scroll = new JScrollPane(tableWrapper);
		scroll.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, controlWrapper);
		scroll.setOpaque(false);

		JTableHeader header = dataTable.getTableHeader();
		
		JPanel headerPanel = GuiComponentUtils.createPanel();
		headerPanel.setLayout(new BorderLayout());
		headerPanel.add(optionsWrapper, BorderLayout.NORTH);
		headerPanel.add(header, BorderLayout.SOUTH);
		headerPanel.add(Box.createGlue(), BorderLayout.EAST);
		
		JViewport headerView = new JViewport();
		headerView.setView(headerPanel);
		headerView.setOpaque(false);
		scroll.setColumnHeader(headerView);
		
		Group hGroup = layout.createParallelGroup(Alignment.CENTER)
			.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
							.addComponent(fieldNameLabel)
							.addComponent(spacer))
					.addGroup(layout.createParallelGroup()
							.addComponent(fieldNamecombo)
							.addComponent(hideEmptyCheckBox)))
			.addGroup(layout.createSequentialGroup()
					.addComponent(objectsButton))
			.addComponent(progressBar)
			.addComponent(infoLabel);

		Group vGroup = layout.createSequentialGroup()
			.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(fieldNameLabel)
							.addComponent(fieldNamecombo))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(spacer)
							.addComponent(hideEmptyCheckBox)))
			.addGroup(layout.createParallelGroup(Alignment.LEADING)
					.addComponent(objectsButton))
			.addComponent(progressBar)
			.addComponent(infoLabel);

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHonorsVisibility(false);
		return scroll;
	}

	// Support for double-click -> view double clicked row in vertical layout
	private void addDataRowViewer(final JTable table) {
		if (table==null) return;
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Validate and consume the mouse event
				if (!SwingUtilities.isLeftMouseButton(e)) return;
				if (e.getClickCount()!=2 || e.isConsumed()) return;
				e.consume();
				
				// Which row was clicked?
				int row = table.rowAtPoint(e.getPoint());
				if ( row<0 || row>=table.getRowCount()) return;
				
				// Grab a copy of the row and columns
				int colCount = table.getColumnCount();
				final Object[][] data = new Object[colCount][2];
				for (int col=0; col<colCount; col++) {
					data[col][0] = table.getColumnName(col);
					data[col][1] = table.getValueAt(row, col);
				}
				
				// Build the dialog later
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						JXTable rowTable = new JXTable(data, new String[]{"Field","Value"});
						rowTable.setEditable(false);
						rowTable.setSortable(true);
						JScrollPane scroll = new JScrollPane(rowTable);
						Window owner = SwingUtilities.windowForComponent(table);
						JDialog dialog = new JDialog(owner);
						dialog.setLayout(new BorderLayout());
						dialog.add(scroll);
						dialog.setPreferredSize(new Dimension((int)(owner.getWidth()*0.75d),owner.getHeight()));
						dialog.pack();
						dialog.setLocationRelativeTo(null);
						dialog.setVisible(true);
					}
				});
			}
		});
	}
	
	
	private JFileChooser fileChooser;
	
	private void searchToFile(String query, String columns, final int limit) throws Exception {
		
		if (this.fileChooser==null) {
			this.fileChooser=  new JFileChooser(SystemUtils.getUserHome());
			this.fileChooser.setDoubleBuffered(true);
			this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			this.fileChooser.setToolTipText("Select a file to save to");
		}
		
		if (this.fileChooser.showSaveDialog(WindowUtils.findWindow(this)) != JFileChooser.APPROVE_OPTION || this.fileChooser.getSelectedFile() == null) return; 
		
		PrintWriter out = null; 
		
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.fileChooser.getSelectedFile()), Charset.defaultCharset())));
		
			RetsSearchInfo searchInfo = getConfig().createSearchInfo();
			SearchRequestEx request = new SearchRequestEx(getResourceClass(),StringUtils.trimToEmpty(query));
			if (limit > 0) {
				request.setLimit(limit);
			} else {
				request.setLimitNone();
			}
			request.setCountFirst();
			searchInfo.getSearchResultFormat().setSearchResultFormat(request);
			request.setSelect(StringUtils.trimToNull(columns));

			final Table result = StandardRetsSearchPaging.compose(new StandardRetsSearchPaging(getClient(), request, searchInfo.getCompactRowPolicy(), searchInfo, new ResponseTouch<SearchResultSet>(){
				public void apply(SearchResultSet object) throws RetsException {
					object.hasNext();
				}}));

			List<String> columnNames =  result.getColumnNames();
			out.println(Csv.escape(',', false,columnNames));

			while (result.hasNext()) {
				out.println(Csv.escape(',',false, result.next()));
			}
		
		} finally {
			if (out !=null) {
				out.flush();
				Closeables.closeQuietly(out);
			}
		}
	}
	
	private SearchResultSet search(String query, String columns, final int limit, boolean paged, final boolean countOnly) throws RetsException, Exception {
		RetsSearchInfo searchInfo = getConfig().createSearchInfo();
		int bufferSize = searchInfo.getSearchBufferSize();
		boolean cdataEscpae = searchInfo.isCdataEscape();
		Set<Integer> validDelayedRetsCodes = searchInfo.getValidDelayedReplyCodes();
		SearchRequestEx request = new SearchRequestEx(getResourceClass(),StringUtils.trimToEmpty(query));
		if (limit > 0) {
			request.setLimit(limit);
		} else {
			request.setLimitNone();
		}
		if (!countOnly) {
			request.setCountFirst();
		} else {
			request.setCountOnly();
		}
		
		searchInfo.getSearchResultFormat().setSearchResultFormat(request);
		request.setSelect(StringUtils.trimToNull(columns));

		StreamingSearch search = new StreamingSearch(request, bufferSize, cdataEscpae, validDelayedRetsCodes, searchInfo.getCompactRowPolicy());
		
		if (!paged) return getClient().executeRetsTransaction(search);

		final StandardRetsSearchPaging result = new StandardRetsSearchPaging(getClient(), request, searchInfo.getCompactRowPolicy(), searchInfo, new ResponseTouch<SearchResultSet>(){
			public void apply(SearchResultSet object) throws RetsException {
				object.hasNext();
			}});
		
		final int columnCount = result.peek().getColumnNames().size();
		final String[] columnNames = result.peek().getColumnNames().toArray(new String[columnCount]);
		
		final List<List<String>> page = Lists.newArrayList();
		Iterator<List<String>> rows = Iterators.concat(result);

		int count = 0;
		while(rows.hasNext()) {
			if (limit>0 && ++count > limit) continue;
			page.add(rows.next());
		}
		final int finalCount = count;		
		return new SearchResultSet() {
			int position = -1;
			public boolean isMaxrows() throws RetsException, IllegalStateException {
				return false;
			}
			public boolean isComplete() throws RetsException {
				return true;
			}
			public int getCount() throws RetsException {
				return finalCount;
			}
			public String[] getColumns() throws RetsException {
				return columnNames;
			}
			public String[] next() throws RetsException {
				if (!this.hasNext()) throw new RetsException(new NoSuchElementException());
				return page.get(++this.position).toArray(new String[columnCount]);
			}
			public boolean hasNext() throws RetsException {
				return this.position<page.size()-1;
			}
		};
	}
	
	
	
	
	
	private String getKeyField() {
		String text = "";
		try{
			text = getMetadata().getResource(getResourceClass().getResource().getName()).getKeyField();
		}catch(Exception e){
			Logger.getLogger(getClass()).warn(String.format("cant find keyfield for %s %s", getConfig().getRetsServiceName(),getResourceClass()));
		}
		return text;
	}

	public String getExportableName() {
		return WordUtils.capitalize(getConfig().getRetsServiceName());
	}

	public Exporter[] getExporters() {
		Map<String, String> queries = Maps.newTreeMap();
		List<Exporter> exporters = Lists.newArrayList();
		exporters.add(new MetadataXlsExporter(getMetadata()));
		int i=1;
		for (final QueryTabComponent tab : this.tabs) {
			
			SearchResultSet result = new /*Lazy*/SearchResultSet() {
				private SearchResultSet results = null;
				
				private SearchResultSet getResults() throws RetsException {
					if (this.results == null) {
						try {
							this.results = search(tab.getQuery(), tab.getSelect(), 0, true, false);
						} catch (Exception e) {
							throw new RetsException(e);
						}
					}
					return this.results;
				}
				public boolean hasNext() throws RetsException {
					return getResults().hasNext();
				}
				
				public String[] next() throws RetsException {
					return getResults().next();
				}
				
				public String[] getColumns() throws RetsException {
					return getResults().getColumns();
				}
				
				public int getCount() throws RetsException {
					return getResults().getCount();
				}
				
				public boolean isComplete() throws RetsException {
					return getResults().isComplete();
				}
				
				public boolean isMaxrows() throws RetsException,IllegalStateException {
					return getResults().isMaxrows();
				}
			};
			
			String filename = String.format("data%s.csv", (i>1) ? String.format("-%d", i) : "");
			queries.put(filename, tab.getQuery());
			exporters.add(new SearchResultSetCsvExporter(result, filename));
			i++;
		}
		exporters.add(new MapExporter("Queries", "Query to filename mappings", queries, ":  ", "queries.txt"));
		return exporters.toArray(new Exporter[exporters.size()]);
	}
	
	
	private class QueryTabComponent extends ButtonTabComponent {
		
		private final String query;
		private final String select;
		
		public QueryTabComponent(String query,String select) {
			super(RetsSourceTabbedPane.this);
			this.query = query;
			this.select = select;
			RetsSourceTabbedPane.this.tabs.add(this);
		}

		@Override
		public Runnable getCloseAction() {
			final Runnable doClose = super.getCloseAction();
			return new Runnable() {
				public void run() {
					RetsSourceTabbedPane.this.tabs.remove(QueryTabComponent.this);
					if (doClose != null) {
						doClose.run();
					}
				}
			};
		}

		public final String getQuery() {
			return this.query;
		}

		public String getSelect() {
			return this.select;
		}
		
	}
	
}
