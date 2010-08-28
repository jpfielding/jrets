package org.realtors.rets.retsexplorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.search.SearchFactory;
import org.realtors.rets.retsexplorer.export.ExportWizard;
import org.realtors.rets.retsexplorer.export.Exportable;
import org.realtors.rets.retsexplorer.find.RetsSearchFactory;
import org.realtors.rets.retsexplorer.login.MainTabbedPane;
import org.realtors.rets.retsexplorer.login.SourceSplitView;
import org.realtors.rets.retsexplorer.retstabbedpane.RetsView;
import org.realtors.rets.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.retsexplorer.util.GuiKeyBindings;
import org.realtors.rets.retsexplorer.util.QueryManager;
import org.realtors.rets.retsexplorer.util.RetsSource;
import org.realtors.rets.util.RetsClientConfig;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class RetsExplorer extends JFrame { 
	
	/** the app */
	public static void main(String args[]) {
	    try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {
	    	// Not much we can do here
	    }
	    ProgressBarDialog.CreateProgDialog((JFrame)null, "Loading RetsExplorer...");
	    ProgressBarDialog.setMessage("Building Components...");
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
	    JFrame RetsExplorer = new RetsExplorer();
		RetsExplorer.setSize((int)(screenSize.getWidth()*.75), (int)(screenSize.getHeight()*.75));
		RetsExplorer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		RetsExplorer.setLocationRelativeTo(null);
		RetsExplorer.setResizable(true);
		RetsExplorer.setTitle("RetsExplorer");
		ProgressBarDialog.dispose();
		RetsExplorer.setVisible(true);
	}
	
	public RetsExplorer(){
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
		
		final List<RetsClientConfig> retsConfigs = getRetsConfigs();
		final MainTabbedPane pane = new MainTabbedPane(retsConfigs);
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
		JMenuItem matcherHelpItem = help.add(new JMenuItem("Matcher"));
		matcherHelpItem.addActionListener(new ErrorPopupActionListener() {
			@Override
			public void action() throws Exception {
				RetsExplorer.matcherHelpWindow();
			}
		});
		
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
		setJMenuBar(menuBar);
		
		GuiKeyBindings.setCloseTabAction(pane, this); //should close the currently open tab upon a key binding; if the only open tab is the login, close the window
	}
	
	private List<RetsClientConfig> getRetsConfigs() { //acquires all rets configs in the background
		ProgressBarDialog.update(250,"Starting Background Credential Loader...");
		
		final Map<String,RetsSource> sources = RetsSource.all();
		
		Thread queryLoader = new Thread("Base-Query-Loader") {
			@Override
			public void run() {
				for (RetsSource source : sources.values()) {
					QueryManager.createQuery(source);
				}
			}
		};
		queryLoader.setDaemon(true);
		queryLoader.start();

		List<RetsClientConfig> retsConfigs = Lists.newLinkedList(Iterables.transform(sources.values(), new Function<RetsSource, RetsClientConfig>(){
			public RetsClientConfig apply(RetsSource from) {
				return from.getConfig();
			}}));
		Collections.sort(retsConfigs,new Comparator<RetsClientConfig>(){
			public int compare(RetsClientConfig o1, RetsClientConfig o2) {
				return o1.getRetsServiceName().compareTo(o2.getRetsServiceName());
			}});
		return retsConfigs;
	}
	
	static void keyBindingsWindow(){
		String info = "New Login: ctrl + n or ctrl + t\n" +
				"Close Selected Tab: ctrl + w\n" +
				"Close Window (with no tabs open): ctrl + w\n";
		JOptionPane.showMessageDialog(null, info, "Key Bindings", JOptionPane.INFORMATION_MESSAGE);
	}
	
	static void matcherHelpWindow(){
		String info = "The Matcher tab is an interface to a script that matches the various metadata fields to \n" +
					  "our datamodel. Clicking the \"Check Access\" button will also make it perform a query \n" +
					  "against the server and determine which fields we actually have access to.";
		JOptionPane.showMessageDialog(null, info, "Matcher", JOptionPane.INFORMATION_MESSAGE);
	}
}