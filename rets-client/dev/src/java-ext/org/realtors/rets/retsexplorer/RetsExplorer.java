package org.realtors.rets.retsexplorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.search.SearchFactory;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MLookupType;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.retsexplorer.export.ExportWizard;
import org.realtors.rets.retsexplorer.export.Exportable;
import org.realtors.rets.retsexplorer.find.RetsSearchFactory;
import org.realtors.rets.retsexplorer.login.MainTabbedPane;
import org.realtors.rets.retsexplorer.login.SourceSplitView;
import org.realtors.rets.retsexplorer.retstabbedpane.RetsView;
import org.realtors.rets.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.retsexplorer.util.GuiKeyBindings;
import org.realtors.rets.retsexplorer.util.QueryManager;
import org.realtors.rets.util.RetsClientConfig;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RetsExplorer extends JFrame { 
	
	/** the app */
	public static void main(String args[]) {
		load(new Supplier<RetsExplorer>() {
			public RetsExplorer get() {
				return new RetsExplorer("RETS Explorer");
			}
		});
	}
	
	public static void load(Supplier<RetsExplorer> explorer) {
	    try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {
	    	// Not much we can do here
	    }
	    ProgressBarDialog.CreateProgDialog((JFrame)null, "Loading ...");
	    ProgressBarDialog.setMessage("Building Components...");
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
	    JFrame retsExplorer = explorer.get();
		retsExplorer.setSize((int)(screenSize.getWidth()*.75), (int)(screenSize.getHeight()*.75));
		retsExplorer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		retsExplorer.setLocationRelativeTo(null);
		retsExplorer.setResizable(true);
		ProgressBarDialog.dispose();
		retsExplorer.setVisible(true);
	}
	
	public RetsExplorer(String title){
		setTitle(title);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		// Search "accross all" support
		SearchFactory.setInstance(new RetsSearchFactory());
		
		setBackground(UIManager.getColor("Panel.background"));
		
		final MainTabbedPane pane = newMainTabbedPane();
		final JMenuBar menuBar = new JMenuBar();
		
		ProgressBarDialog.update(750,"Building Menu...");
		
		// FILE 
		JMenu file = menuBar.add(new JMenu("File"));
		file.setMnemonic(KeyEvent.VK_F);
		// FILE - login
		JMenuItem loginItem = file.add(new JMenuItem("Login"));
		loginItem.setMnemonic(KeyEvent.VK_L);
		loginItem.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				pane.createTab();
				pane.setSelectedIndex(pane.getTabCount()-1);
			}
		});
		// FILE - export
		JMenuItem export = new JMenuItem("Export...");
		export.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				int count = pane.getTabCount();
	
				if (count <= 0) {
					return;
				}
				
				List<Exportable> exportables = Lists.newLinkedList();
				
				for (int i=0; i<count; i++) {
					Component rv = ((SourceSplitView) pane.getComponentAt(i)).getRetsView();
					if (rv instanceof RetsView) {
						exportables.add((RetsView)rv);
					}
				}
	
				ExportWizard exportWizard = new ExportWizard(RetsExplorer.this, exportables.toArray(new Exportable[exportables.size()]));
				exportWizard.setModal(true);
				exportWizard.pack();
				exportWizard.setLocationRelativeTo(RetsExplorer.this);
				exportWizard.setVisible(true);
			}
		});
		file.add(export);
		// FILE - exit
		JMenuItem exitItem = file.add(new JMenuItem("Exit"));
		exitItem.addActionListener(new ErrorPopupActionListener(){
			@Override
			public void action() throws Exception {
				dispose();
			}});

		exitItem.setMnemonic(KeyEvent.VK_X);
		// EDIT
		JMenu edit = menuBar.add(new JMenu("Edit"));
		edit.setMnemonic(KeyEvent.VK_E);
		// VIEW
		JMenu view = menuBar.add(new JMenu("View"));
		view.setMnemonic(KeyEvent.VK_V);
		// VIEW - close tab
		JMenuItem closeItem = view.add(new JMenuItem("Close Tab"));
		closeItem.setMnemonic(KeyEvent.VK_C);
		closeItem.addActionListener(pane.getCloseTabActionListener());
		// HELP
		JMenu help = menuBar.add(new JMenu("Help"));
		help.setMnemonic(KeyEvent.VK_H);
		JMenuItem keyBindingsItem = help.add(new JMenuItem("Key Bindings"));
		keyBindingsItem.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				RetsExplorer.keyBindingsWindow();
			}
		});
		addHelpItem(help);
		
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		setJMenuBar(menuBar);
		
		GuiKeyBindings.setCloseTabAction(pane, this); //should close the currently open tab upon a key binding; if the only open tab is the login, close the window
	}
	
	protected MainTabbedPane newMainTabbedPane() {
		final List<RetsClientConfig> configs = Lists.newArrayList();
		configs.add(new RetsClientConfig(SampleConfigs.mris(),"MRIS"));
		configs.add(new RetsClientConfig(SampleConfigs.taar(),"TAAR"));
		return new MainTabbedPane(new SimpleQueryManager(), configs);
	}

	@SuppressWarnings("unused") 
	protected void addHelpItem(JMenu help) {
		// noop
	}
	
	public static class SimpleQueryManager implements QueryManager {
		final Map<String, String> cache = new ConcurrentHashMap<String, String>(2000, 0.75f, 2);
		
		public void put(String key, String query) {
			this.cache.put(key,query);
		}
		public void put(String retsServiceName, String resource, String className, String query) {
			put(toKey(retsServiceName, resource, className),query);
		}
		public String get(String retsServiceName, String resource, String className) {
			return this.cache.get(toKey(retsServiceName, resource, className));
		}
		
		protected String toKey(String retsServiceName, String resource, String className) {
			return String.format("%s.%s.%s", StringUtils.lowerCase(retsServiceName), StringUtils.lowerCase(resource), StringUtils.lowerCase(className));
		}
		
		public String createStatusQuery(String retsServiceName, String resource, String className, Metadata metadata, String... fields) {
			String toKey = toKey(retsServiceName, resource, className);
			String query = this.cache.get(toKey);
			
			if (query != null) return query;
			if (metadata==null) return "";
			MClass mClass = metadata.getMClass(resource, className);
			if (mClass==null) return "";
			MTable[] mTables = mClass.getMTables();
			if (mTables==null) return "";
			for (String field : fields) {
				for (MTable table : mTables){
					if (!StringUtils.containsIgnoreCase(table.getStandardName(), field)) continue;
					MLookup lookup = metadata.getLookup(table);
					if (lookup == null) {
						if (StringUtils.containsIgnoreCase(table.getDataType(),"Character")) return String.format("~(%s=UNKNOWN)",table.getSystemName());
						if (StringUtils.containsIgnoreCase(table.getDataType(),"DateTime")) return String.format("(%s=1900-01-01T00:00:00+)",table.getSystemName());
						if (StringUtils.containsIgnoreCase(table.getDataType(),"Int")) return String.format("(%s=0+)",table.getSystemName());
						return String.format("(%s=<%s>)",table.getSystemName(), table.getDataType());
					}
					Iterable<MLookupType> filtered = Iterables.filter(Arrays.asList(lookup.getMLookupTypes()), new Predicate<MLookupType>(){
						public boolean apply(MLookupType from) {
							if (StringUtils.containsIgnoreCase(from.getLongValue(), "active")) return true;
							if (StringUtils.containsIgnoreCase(from.getLongValue(), "current")) return true;
							return false;
						}});
					Iterable<String> values = Iterables.transform(filtered, new Function<MLookupType,String>(){
						public String apply(MLookupType from) {
							return from.getValue();
						}});
					return String.format("(%s=|%s)",table.getSystemName(), Joiner.on(",").join(values));
				}
			}
			return "";
		}
	
	}

	static void keyBindingsWindow(){
		String info = "New Login: ctrl + n or ctrl + t\n" +
				"Close Selected Tab: ctrl + w\n" +
				"Close Window (with no tabs open): ctrl + w\n";
		JOptionPane.showMessageDialog(null, info, "Key Bindings", JOptionPane.INFORMATION_MESSAGE);
	}
	
}