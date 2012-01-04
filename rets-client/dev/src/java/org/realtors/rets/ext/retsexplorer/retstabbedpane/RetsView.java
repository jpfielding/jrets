package org.realtors.rets.ext.retsexplorer.retstabbedpane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.swingx.JXPanel;
import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.retsexplorer.export.Exportable;
import org.realtors.rets.ext.retsexplorer.export.Exporter;
import org.realtors.rets.ext.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.ext.retsexplorer.util.GuiUtils;
import org.realtors.rets.ext.retsexplorer.util.QueryManager;
import org.realtors.rets.ext.retsexplorer.wirelog.WireLogConsole;
import org.realtors.rets.ext.retsexplorer.wirelog.WireLoggedRetsClient;
import org.realtors.rets.ext.util.ResourceClass;
import org.realtors.rets.ext.util.RetsClient;
import org.realtors.rets.ext.util.RetsClientConfig;
import org.realtors.rets.ext.util.io.UncloseableOutputStream;
import org.realtors.rets.ext.util.transaction.metadata.GetFields;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

public class RetsView extends JPanel implements Exportable {
	
	private RetsClientConfig config;
	private RetsClient client;
	private Metadata metadata;
	
	private Exportable exportable;
	private SelectorPanel selectorObject;

	private Map<String, RetsSourceTabbedPane> mclassTableCache;
	private final QueryManager qm;
	
	WireLogConsole wireLogConsole;
	
	public RetsView(QueryManager qm, final RetsClientConfig config) {
		this(qm, config, null, null);
	}
	
	public RetsView(QueryManager qm, final RetsClientConfig config, RetsClient client, WireLogConsole console){
		this.qm = qm;
		this.wireLogConsole = console;
		this.mclassTableCache = Maps.newHashMap();
		setConfig(config);
		setClient(client);
		setLayout(new BorderLayout());
		
		final JPanel rightPanel = new JPanel(new GridLayout());
		this.selectorObject = new SelectorPanel();
		final Component selectPanel = this.selectorObject.createSelector(rightPanel);
		
		JSplitPane leftRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		leftRightSplit.setLeftComponent(selectPanel);
		leftRightSplit.setRightComponent(rightPanel);
		leftRightSplit.setContinuousLayout(true);
		leftRightSplit.setDoubleBuffered(true);
		leftRightSplit.setOneTouchExpandable(true);
		
		add(leftRightSplit, BorderLayout.CENTER);
		
	}

	
	public Set<MetadataTable> getMetadataTables() {
		Set<MetadataTable> metas = Sets.newLinkedHashSet();
		for (Component comp : this.mclassTableCache.values()) {
			if (comp instanceof MetadataTable) {
				metas.add((MetadataTable) comp);
			} else if (comp instanceof Container) {
				metas.addAll(GuiUtils.getAllChildren(MetadataTable.class,(Container)comp));
			}
		}
		return metas;
	}
	//tells you what the currently selected resource and mclass are
	public String getSelectedInfo() {
		return String.format("%s-%s", getSelectorObject().selectedResource, getSelectorObject().selectedMClass);
	}
	
	public String getSelectedResource() {
		return getSelectorObject().selectedResource;
	}
	
	public String getSelectedMClass() {
		return this.getSelectorObject().selectedMClass;
	}
	
	public JButton getDummyActionButton() {
		return this.getSelectorObject().dummyActionButton;
	}

	private class SelectorPanel {
		JButton dummyActionButton = new JButton(); //meant only to pick up on actions within the selector panel
		String selectedResource = null;
		String selectedMClass = null;
		
		private Component createSelector(final JPanel rightPanel) {
			final ButtonGroup buttonGroup = new ButtonGroup();
			final JXPanel selectPanel = new JXPanel();
	
			GroupLayout layout = new GroupLayout(selectPanel);
			selectPanel.setLayout(layout);
			
			layout.setAutoCreateContainerGaps(true);
			layout.setAutoCreateGaps(false);
			
			Group hGroup = layout.createParallelGroup(Alignment.LEADING, false);
			Group vGroup = layout.createSequentialGroup();
			
			Comparator<MetaObject> metaObjectComparator = new Comparator<MetaObject>() {
				public int compare(MetaObject o1, MetaObject o2) {
					String o1Str = (o1==null) ? "" : o1.getId();
					String o2Str = (o2==null) ? "" : o2.getId();
					Pattern hasNum = Pattern.compile("^.*?(\\d+).*?$");
					Matcher m1 = hasNum.matcher(o1Str);
					Matcher m2 = hasNum.matcher(o2Str);
					try {
						if (m1.matches() && m2.matches()) {
							return NumberUtils.createBigDecimal(m1.group(1)).compareTo(NumberUtils.createBigDecimal(m2.group(1)));
						}
						return NumberUtils.createBigDecimal(o1Str).compareTo(NumberUtils.createBigDecimal(o2Str));
					} catch (NumberFormatException e) {
						return o1Str.compareToIgnoreCase(o2Str);
					}
				}
			};
			
			// Sort
			TreeMultimap<MResource, MClass> resoureMap = TreeMultimap.create(metaObjectComparator,metaObjectComparator);
			for( MResource mresource : getMetadata().getSystem().getMResources() ){
				for( final MClass mclass : mresource.getMClasses() ) {
					resoureMap.put(mresource, mclass);
				}
			}
			
			for( MResource mresource : resoureMap.keySet() ){
				JLabel resourceLabel = new JLabel(mresource.getId());
				resourceLabel.setFont(resourceLabel.getFont().deriveFont(Font.BOLD, resourceLabel.getFont().getSize2D()*1.1f));
				resourceLabel.setToolTipText(mresource.getDescription());
				resourceLabel.setHorizontalAlignment(SwingConstants.LEFT);
				
				hGroup.addComponent(resourceLabel);
				vGroup.addComponent(resourceLabel);
				
				for( final MClass mclass : resoureMap.get(mresource) ) {
					
					//preinitialize cache so that we can do finds across all without having to click each button first
					String[] parts = mclass.getPath().split(":");
					final ResourceClass resourceClass = new ResourceClass(parts[0],parts[1]);
					try {
						RetsView.this.mclassTableCache.put(resourceClass.toString(), createRetsTabbePane(resourceClass));
					} catch (Exception e1) {
						GuiUtils.exceptionPopup(e1);
					}
					
					//this class is needed to be able to determine what resource/class is currently selected within the selector panel
					class CustomToggleButton extends JToggleButton {
						String resource = "";
						String propType = "";
						public CustomToggleButton(String mclassId, String resource) {
							super(mclassId);
							this.resource = resource;
							this.propType = mclassId;
						}
					}
					
					
					final CustomToggleButton classButton = new CustomToggleButton(mclass.getId(), mresource.getId());
					buttonGroup.add(classButton);
					classButton.setToolTipText(mclass.getDescription());
					classButton.setHorizontalAlignment(SwingConstants.CENTER);
					classButton.setMargin(new Insets(0,0,0,0));
					hGroup.addComponent(classButton);
					vGroup.addComponent(classButton);
					
					classButton.addActionListener(new ErrorPopupActionListener(){
						@Override
						public void action() throws Exception {
							rightPanel.setVisible(false); 
							rightPanel.removeAll();
							rightPanel.setVisible(true);
							
							SelectorPanel.this.selectedMClass = classButton.propType;
							SelectorPanel.this.selectedResource = classButton.resource;
							SelectorPanel.this.dummyActionButton.doClick(); //Hey! Look at me! Something happened! ...i really hate these kinds of hacks
							
							if (RetsView.this.mclassTableCache.containsKey(resourceClass.toString())) {
								Component comp = RetsView.this.mclassTableCache.get(resourceClass.toString());
								if (comp instanceof Exportable) {
									RetsView.this.exportable = (Exportable)comp;
								}
								rightPanel.add(comp);
								return;
							}
							RetsSourceTabbedPane pane = RetsView.this.mclassTableCache.get(resourceClass.toString()); //since it is already precached...
							RetsView.this.exportable = pane;
							rightPanel.add(pane);
						}});
				}
				
				layout.setHorizontalGroup(hGroup);
				layout.setVerticalGroup(vGroup);
				layout.linkSize(SwingConstants.HORIZONTAL, selectPanel.getComponents());
				
			}
			selectPanel.setScrollableTracksViewportWidth(true);
			selectPanel.setScrollableTracksViewportHeight(false);
			selectPanel.setOpaque(false);
	
			JScrollPane scroll = new JScrollPane(selectPanel);
			scroll.setBackground(selectPanel.getBackground());
			scroll.setOpaque(false);
			scroll.getViewport().setBackground(selectPanel.getBackground());
			scroll.getViewport().setOpaque(false);
			
			Dimension selectMinSize = selectPanel.getPreferredSize();
			selectMinSize.height = scroll.getMinimumSize().height;
			selectMinSize.width += scroll.getVerticalScrollBar().getPreferredSize().width;
			
			scroll.setMinimumSize(new Dimension(selectMinSize));
			return scroll;
		}
	}
	
	private RetsSourceTabbedPane createRetsTabbePane(ResourceClass resourceClass) throws Exception {
		MTable[] mtables = new GetFields(resourceClass).execute(getMetadata());
		return new RetsSourceTabbedPane(this.qm, getConfig(), resourceClass, getClient(), getMetadata(), this.wireLogConsole, mtables);
	}
	
	
	public RetsClientConfig getConfig() {
		return this.config;
	}
	
	
	public void setConfig(RetsClientConfig config) {
		if (config != this.config) {
			this.config = config;
		}
	}
	
	public RetsClient getClient() {
		if (this.client != null) {
			return this.client;
		}
		if (this.config == null) {
			return null;
		}
		OutputStream request = new UncloseableOutputStream(System.err);
		OutputStream response = new UncloseableOutputStream(System.out);
		this.client = new WireLoggedRetsClient(this.config.createRetsProfile(), request, response);
		return this.client;
	}
	
	
	public void setClient(RetsClient client) {
		if (client!= this.client) {
			this.client = client;
			setMetadata(null);
		}
	}
	
	public Metadata getMetadata() {
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
	
	public void setMetadata(Metadata metadata) {
		if (metadata != this.metadata) {
			this.metadata = metadata;
		}
	}
	
	//NOTE: this doesn't actually return the panel itself, just the object that it gets created within
	private SelectorPanel getSelectorObject() {
		return this.selectorObject;
	}

	public String getExportableName() {
		return (this.exportable == null) ? "" : this.exportable.getExportableName();
	}
	
	public Exporter[] getExporters() {
		return (this.exportable == null) ? new Exporter[0] : this.exportable.getExporters();
	}
	
	public Map<String, RetsSourceTabbedPane> getMclassTableCache() {
		return this.mclassTableCache;
	}
	
	
}
