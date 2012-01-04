package org.realtors.rets.ext.retsexplorer.login;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.realtors.rets.ext.retsexplorer.util.ErrorPopupActionListener;
import org.realtors.rets.ext.retsexplorer.util.GuiKeyBindings;
import org.realtors.rets.ext.retsexplorer.wirelog.WireLogConsoleOutputStream;
import org.realtors.rets.ext.retsexplorer.wirelog.WireLoggedRetsClientConfig;
import org.realtors.rets.ext.util.RetsClientConfig;
import org.realtors.rets.ext.util.RetsClientConfig.Property;

import com.google.common.collect.Maps;

public class LoginView extends JPanel {
	private Map<String,JTextField> fields;
	private String retsServiceName = "login";
	private JButton loginButton;
	private JButton refreshButton;
	
	public LoginView(final List<RetsClientConfig> configs) {
		this.fields = new HashMap<String,JTextField>();
		final JComboBox configSelect = new JComboBox();

		configSelect.setModel(new DefaultComboBoxModel(){
			@Override
			public Object getElementAt(int index) {
				return configs.get(index);
			}
			@Override
			public int getSize() {
				return configs.size();
			}});
        configSelect.addActionListener(new ErrorPopupActionListener(){
			@Override
			public void action() throws Exception {
				RetsClientConfig selectedItem = (RetsClientConfig) configSelect.getSelectedItem();
				if (!(selectedItem == null)) { 
					setRetsServiceName(selectedItem.getRetsServiceName());
				}
					Properties selectedProperties = selectedItem.getProperties();
					for (String key : LoginView.this.fields.keySet()) LoginView.this.fields.get(key).setText(selectedProperties.getProperty(key));
			}
			});
        
        this.loginButton = new JButton("Login",getIcon("http://imageserver.3yd.com/image_library/incors/images/png/application/24x24/plain/nav_right_green.png"));
		this.refreshButton = new JButton("Refresh",getIcon("http://imageserver.3yd.com/image_library/incors/images/png/application/24x24/plain/refresh.png"));
		this.refreshButton.setToolTipText("Refreshes login properties from the source file (Dev only)");
		
		this.refreshButton.addActionListener(new ActionListener() { //manually checks the source file for property changes, after the file has already been compiled, and changes the text fields accordingly
			public void actionPerformed(ActionEvent e) {
				RetsClientConfig selectedItem = (RetsClientConfig) configSelect.getSelectedItem();
				if (selectedItem != null) { //do nothing if nothing is selected
					HashMap<String,String> mappedProps = Maps.newHashMap(); //ConnectionPropertiesParser.parseProperties(selectedItem.toString());
					if (mappedProps != null) { //only continue if it could parse something.. if it could not, then the sourceFile format has likely been changed, and the parser needs updated
						for (String key : mappedProps.keySet()) {
							if (!LoginView.this.fields.containsKey(key))
								System.err.println("Could not find: " + key);
							else {
								LoginView.this.fields.get(key).setText(mappedProps.get(key));
							}
						}
					}
				}
			}
		});
		
		
        JLabel selectFieldLabel = new JLabel("Select");
        selectFieldLabel.setLabelFor(configSelect);
        JTabbedPane tabbedPane = new JTabbedPane();
        this.fields.putAll(addSection(tabbedPane, RetsClientConfig.LOGIN,"Login"));
        this.fields.putAll(addSection(tabbedPane, RetsClientConfig.GENERAL_CONFIG_OPTIONS,"Config"));
        this.fields.putAll(addSection(tabbedPane, RetsClientConfig.METADATA,"Metadata"));
        this.fields.putAll(addSection(tabbedPane, RetsClientConfig.SEARCH,"Search"));
        
        JTextField customNameTextField = new JTextField(20);
        JLabel customNameLabel = new JLabel("Custom Rets Name");
        customNameLabel.setLabelFor(customNameTextField);
        this.fields.put("customName", customNameTextField);
        JPanel something = (JPanel)tabbedPane.getComponentAt(0);
        addLabelTextRows(customNameLabel, customNameTextField, something);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(getLoginButton());
        buttonPanel.add(getRefreshButton());
        
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        
        Group hGroup = layout.createParallelGroup(Alignment.CENTER)
        	.addGroup(layout.createSequentialGroup()
        			.addComponent(selectFieldLabel)
        			.addComponent(configSelect))
        	.addComponent(tabbedPane)
        	.addComponent(buttonPanel);
        
        Group vGroup = layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        			.addComponent(selectFieldLabel)
        			.addComponent(configSelect))
        	.addComponent(tabbedPane)
        	.addComponent(buttonPanel);
        
        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        
        //have to set keybindings to both the config panel, and the general panel
        GuiKeyBindings.setEnterKeyAction(getLoginButton(), configSelect);
        GuiKeyBindings.setEnterKeyAction(getLoginButton(), this);
	}
	
	private ImageIcon getIcon(String url)  {
		try {
			return new ImageIcon(new URL(url));
		} catch (MalformedURLException e) {
			return new ImageIcon();
		}
	}

	private Map<String,JTextField> addSection(JTabbedPane pane, Property[] properties, String name){
        // subsection layout
        JPanel textControlsPane = new JPanel(new GridBagLayout());
        pane.add(name, textControlsPane);

        Map<String,JTextField> fields1 = new HashMap<String,JTextField>();
        // all the subsection contents here
        for (Property p : properties){
	        JTextField textField = new JTextField(20);
	        textField.setToolTipText(p.getDescription());
	        JLabel textFieldLabel = new JLabel(p.getName());
//	        if (p.getName().equals("Password") || p.getName().equals("User Agent Password")) {
//	        	JPasswordField passwordField = new JPasswordField(20);
//	        	passwordField.enableInputMethods(true);
//	        	passwordField.setToolTipText(p.getDescription());
//	        	textFieldLabel.setLabelFor(passwordField);
//	        	addLabelTextRows(textFieldLabel, passwordField, textControlsPane);
//	        	fields1.put(p.getProperty(), passwordField);
//	        }
//	        else {
	        	textFieldLabel.setLabelFor(textField);
	        	addLabelTextRows(textFieldLabel, textField, textControlsPane);
	        	fields1.put(p.getProperty(), textField);
//	        }
        }
        return fields1;
	}
	
	private void addLabelTextRows(JLabel label, JComponent textField, Container container) {
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;

		c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
		c.fill = GridBagConstraints.NONE; // reset to default
		c.weightx = 0.0; // reset to default
		container.add(label, c);

		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		container.add(textField, c);
	}

	public RetsClientConfig getSelectedRetsConfig(WireLogConsoleOutputStream wire) {
		if (getRetsServiceName() == null) {
			setRetsServiceName(this.fields.get("customName").getText());
		}
		Properties p = new Properties();
		for (String key : this.fields.keySet()) p.setProperty(key, this.fields.get(key).getText().trim());
		
		return new WireLoggedRetsClientConfig(p, getRetsServiceName(), wire, wire);
	}
	
	private void setRetsServiceName(String name) {
		this.retsServiceName = name;
	}
	
	public String getRetsServiceName() {
		return this.retsServiceName;
	}
	
	public JButton getLoginButton() {
		return this.loginButton;
	}
	
	public JButton getRefreshButton() {
		return this.refreshButton;
	}
}