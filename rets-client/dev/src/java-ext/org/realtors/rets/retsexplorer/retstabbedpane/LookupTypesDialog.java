package org.realtors.rets.retsexplorer.retstabbedpane;

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MLookupType;

public class LookupTypesDialog extends JDialog {
	
	private MLookup lookup = null;
	private JScrollPane scrollPane = null;
	private LookupTypesTable lookupTypesTable = null;
	private JButton okButton = null;
	
	public LookupTypesDialog(MLookup lookup) {
		super();
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Dialog owner, boolean modal, MLookup lookup) {
		super(owner, modal);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc, MLookup lookup) {
		super(owner, title, modal, gc);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Dialog owner, String title, boolean modal, MLookup lookup) {
		super(owner, title, modal);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Dialog owner, String title, MLookup lookup) {
		super(owner, title);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Dialog owner, MLookup lookup) {
		super(owner);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Frame owner, boolean modal, MLookup lookup) {
		super(owner, modal);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc, MLookup lookup) {
		super(owner, title, modal, gc);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Frame owner, String title, boolean modal, MLookup lookup) {
		super(owner, title, modal);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Frame owner, String title, MLookup lookup) {
		super(owner, title);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Frame owner, MLookup lookup) {
		super(owner);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Window owner, ModalityType modalityType, MLookup lookup) {
		super(owner, modalityType);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Window owner, String title, ModalityType modalityType,
			GraphicsConfiguration gc, MLookup lookup) {
		super(owner, title, modalityType, gc);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Window owner, String title, ModalityType modalityType, MLookup lookup) {
		super(owner, title, modalityType);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Window owner, String title, MLookup lookup) {
		super(owner, title);
		setLookup(lookup);
		initialize();
	}

	public LookupTypesDialog(Window owner, MLookup lookup) {
		super(owner);
		setLookup(lookup);
		initialize();
	}
	
	public MLookup getLookup() {
		return this.lookup;
	}

	public void setLookup(MLookup lookup) {
		this.lookup = lookup;
	}

	private void initialize() {
		setUndecorated(false);
		setModal(false);
		setResizable(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		setTitle((getLookup() == null) ? "" : getLookup().getLookupName());
		getRootPane().setDefaultButton(getOkButton());
		setAlwaysOnTop(false);
		setFocusCycleRoot(true);
		setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
			@Override
			public Component getDefaultComponent(Container container) {
				return getOkButton();
			}
			@Override
			public Component getFirstComponent(Container container) {
				return getOkButton();
			}
			@Override
			public Component getInitialComponent(Window window) {
				return getOkButton();
			}
		});
		add(getScrollPane());
		add(Box.createVerticalStrut(5));
		add(getOkButton());
	}
	
	private JScrollPane getScrollPane() {
		if (this.scrollPane == null) {
			this.scrollPane = new JScrollPane(getLookupTypesTable());
			this.scrollPane.setDoubleBuffered(true);
			this.scrollPane.setAlignmentX(CENTER_ALIGNMENT);
			this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
			this.scrollPane.getViewport().setOpaque(false);
			this.scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		}
		return this.scrollPane;
	}

	private LookupTypesTable getLookupTypesTable() {
		if (this.lookupTypesTable == null) {
			MLookup look = getLookup();
			MLookupType[] types = 
				(look == null) ? new MLookupType[0] : look.getMLookupTypes();
			this.lookupTypesTable = new LookupTypesTable(types);
			this.lookupTypesTable.setDoubleBuffered(true);
			this.lookupTypesTable.setAlignmentX(CENTER_ALIGNMENT);
			this.lookupTypesTable.setBorder(BorderFactory.createEmptyBorder());
			this.lookupTypesTable.setOpaque(true);
		}
		return this.lookupTypesTable;
	}
	
	private JButton getOkButton() {
		if (this.okButton == null) {
			this.okButton = new JButton(" Ok ");
			this.okButton.setDoubleBuffered(true);
			this.okButton.setAlignmentX(CENTER_ALIGNMENT);
			this.okButton.setFocusable(true);
			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			};
			
			this.okButton.addActionListener(listener);
		}
		return this.okButton;
	}

}
