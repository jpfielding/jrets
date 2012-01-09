package org.realtors.rets.retsexplorer.retstabbedpane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.realtors.rets.retsexplorer.util.ErrorPopupActionListener;


public class RetsObjectsFrame extends JFrame {
	
	private JComboBox resourceCombo;
	private JTextField contentIdField;
	private JTextField objectIdField;
	private JComboBox objectTypeCombo;
	private JCheckBox pauseConsoleCheckBox;
	private JCheckBox savePhotosCheckBox;
	private JTextField savePhotoDirField;
	private JButton browseButton;
	
	private JButton getButton;
	private JButton resetButton;
	
	private JPanel configPanel; //this holds all the options for the query
	private JPanel objectsPanel; //this is to hold the photos/objects
	
	public RetsObjectsFrame() {
		initialize();
		
		//settings for the frame
		setLayout(new BorderLayout());
		add(this.configPanel, BorderLayout.CENTER);
		add(this.objectsPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initialize() {
		JPanel panel = new JPanel(true);
		panel.setAlignmentX(LEFT_ALIGNMENT);
		
		this.resourceCombo = new JComboBox();
		this.resourceCombo.setDoubleBuffered(true);
		this.resourceCombo.setAutoscrolls(true);
		this.resourceCombo.setEditable(true);
		
		this.contentIdField = new JTextField();
		this.contentIdField.setDoubleBuffered(true);
		this.contentIdField.setHorizontalAlignment(SwingConstants.LEADING);
		
		this.objectIdField = new JTextField("*");
		this.objectIdField.setDoubleBuffered(true);
		this.objectIdField.setHorizontalAlignment(SwingConstants.LEADING);
		
		this.objectTypeCombo = new JComboBox();
		this.objectTypeCombo.setDoubleBuffered(true);
		this.objectTypeCombo.setAutoscrolls(true);
		this.objectTypeCombo.setEditable(true);
		
		this.pauseConsoleCheckBox = new JCheckBox();
		this.pauseConsoleCheckBox.setSelected(true);
		this.pauseConsoleCheckBox.setToolTipText("Unchecking this while pulling photos is *NOT* recommended, and will likely cause the app to hang");
		
		this.savePhotosCheckBox = new JCheckBox();
		this.savePhotosCheckBox.setSelected(false);
		
		this.savePhotoDirField = new JTextField(StringUtils.trimToEmpty(SystemUtils.USER_HOME), 12);
		this.savePhotoDirField.setToolTipText(this.savePhotoDirField.getText());
		this.savePhotoDirField.setDoubleBuffered(true);
		this.savePhotoDirField.setAlignmentX(LEFT_ALIGNMENT);
		this.savePhotoDirField.setHorizontalAlignment(SwingConstants.LEFT);
		
		createBrowseButton();
		
		this.getButton = new JButton("Get");
		this.getButton.setDoubleBuffered(true);
		this.getButton.setAlignmentX(CENTER_ALIGNMENT);
		
		this.resetButton = new JButton("Reset");
		this.resetButton.setDoubleBuffered(true);
		this.resetButton.setAlignmentX(CENTER_ALIGNMENT);
		
		this.objectsPanel = new JPanel();
		this.objectsPanel.setDoubleBuffered(true);
		this.objectsPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		//creating all labels --NOTE: leave this after everything is initialized, or else it will be linking to null values
		JLabel resourceLabel = createLabel("Resource:", getResourceCombo());
		JLabel contentIdLabel = createLabel("Content Id:", getContentIdField());
		JLabel objectIdLabel = createLabel("Object Id:", getObjectIdField());
		JLabel objectTypeLabel = createLabel("Object Type:", getObjectTypeCombo());
		JLabel pauseConsoleLabel = createLabel("Pause Console", getPauseConsoleCheckBox());
		JLabel savePhotosLabel = createLabel("Save Photos:", getSavePhotoCheckBox());
		JLabel saveDirLabel = createLabel("Save Directory:", getSavePhotoDirField());

		this.configPanel = new JPanel(true);
		
		GroupLayout layout = new GroupLayout(this.configPanel);
		this.configPanel.setLayout(layout);
		// x-axis
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.TRAILING)
								.addComponent(resourceLabel)
								.addComponent(contentIdLabel)
								.addComponent(objectIdLabel)
								.addComponent(objectTypeLabel)
								.addComponent(pauseConsoleLabel)
								.addComponent(savePhotosLabel)
								.addComponent(saveDirLabel)
						)
						.addGroup(layout.createParallelGroup()
								.addComponent(getResourceCombo())
								.addComponent(getContentIdField())
								.addComponent(getObjectIdField())
								.addComponent(getObjectTypeCombo())
								.addComponent(getPauseConsoleCheckBox())
								.addComponent(getSavePhotoCheckBox())
								.addGroup(layout.createSequentialGroup()
										.addComponent(getSavePhotoDirField())
										.addComponent(getBrowseButton()))
						)
				)
				.addGroup(layout.createSequentialGroup()
						.addComponent(getGetButton())							
				)
		);
		
		// y-axis
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createBaselineGroup(false,true)
								.addComponent(resourceLabel)
								.addComponent(getResourceCombo())
						)
						.addGroup(layout.createBaselineGroup(false,true)
								.addComponent(contentIdLabel)
								.addComponent(getContentIdField())
						)
						.addGroup(layout.createBaselineGroup(false,true)
								.addComponent(objectIdLabel)
								.addComponent(getObjectIdField())
						)
						.addGroup(layout.createBaselineGroup(false,true)
								.addComponent(objectTypeLabel)
								.addComponent(getObjectTypeCombo())
						)
						.addGroup(layout.createBaselineGroup(false,true)
								.addComponent(pauseConsoleLabel)
								.addComponent(getPauseConsoleCheckBox())
						)
						.addGroup(layout.createBaselineGroup(false,true)
								.addComponent(savePhotosLabel)
								.addComponent(getSavePhotoCheckBox())
						)
						.addGroup(layout.createBaselineGroup(false,true)
								.addComponent(saveDirLabel)
								.addComponent(getSavePhotoDirField())
								.addComponent(getBrowseButton())
						)
				)
				.addComponent(getGetButton())
		);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.linkSize(getResourceCombo(), getContentIdField(),getObjectIdField(), getObjectTypeCombo());
	}
	
	private JLabel createLabel(String text, Component labelFor) {
		JLabel label = new JLabel(text, SwingConstants.RIGHT);
		label.setDoubleBuffered(true);
		label.setLabelFor(labelFor);
		return label;
	}
	
	private void createBrowseButton() {
		this.browseButton = new JButton("Browse");
		this.browseButton.setDoubleBuffered(true);
		this.browseButton.setAlignmentX(LEFT_ALIGNMENT);
		this.browseButton.addActionListener(new ErrorPopupActionListener() {
			final JFileChooser dirChooser;
			{
				this.dirChooser = new JFileChooser(new File(RetsObjectsFrame.this.savePhotoDirField.getText()));
				this.dirChooser.setDoubleBuffered(true);
				this.dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				this.dirChooser.setToolTipText("Browse for an export directory");
			}
			@Override
			public void action() throws Exception {
				if (this.dirChooser.showSaveDialog(RetsObjectsFrame.this) == JFileChooser.APPROVE_OPTION && this.dirChooser.getSelectedFile() != null) {
					RetsObjectsFrame.this.savePhotoDirField.setText(this.dirChooser.getSelectedFile().getAbsolutePath());
				} else {
					this.dirChooser.setSelectedFile(new File(RetsObjectsFrame.this.savePhotoDirField.getText()));
				}
			}
		});
	}
	
	public JComboBox getResourceCombo() {
		return this.resourceCombo;
	}
	
	public JTextField getContentIdField() {
		return this.contentIdField;
	}
	
	public JTextField getObjectIdField() {
		return this.objectIdField;
	}
	
	public JComboBox getObjectTypeCombo() {
		return this.objectTypeCombo;
	}
	
	public JCheckBox getPauseConsoleCheckBox() {
		return this.pauseConsoleCheckBox;
	}
	
	public JCheckBox getSavePhotoCheckBox() {
		return this.savePhotosCheckBox;
	}
	
	public JTextField getSavePhotoDirField() {
		return this.savePhotoDirField;
	}
	
	public JButton getBrowseButton() {
		return this.browseButton;
	}
	
	public JButton getGetButton() {
		return this.getButton;
	}
	
	public JButton getResetButton() {
		return this.resetButton;
	}
	
	public JPanel getObjectsPanel() {
		return this.objectsPanel;
	}
	
	public JPanel getConfigPanel() {
		return this.configPanel;
	}
	
	public static void main(String[] args) {
		new RetsObjectsFrame();
	}
}
