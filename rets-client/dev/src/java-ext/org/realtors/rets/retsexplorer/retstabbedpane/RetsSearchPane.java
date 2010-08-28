package org.realtors.rets.retsexplorer.retstabbedpane;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.realtors.rets.retsexplorer.util.GuiComponentUtils;
import org.realtors.rets.retsexplorer.util.GuiKeyBindings;
import org.realtors.rets.util.RetsFieldNameType;

public class RetsSearchPane extends JPanel {
	JTextField limitField;
	JCheckBox enablePagingCheckBox;
	JCheckBox countOnlyCheckBox;
	JTextField selectField;
	JTextField keyFieldField;
	JTextField queryField;
	JComboBox fieldNameCombo;
	JCheckBox hideEmptyCheckBox;
	JCheckBox writeToFileCheckBox;
	
	JButton searchButton;
	JButton objectsButton;
	
	public RetsSearchPane() {
		initialize();
	}
	
	public void initialize() {
		//setting up the fields
		this.limitField = GuiComponentUtils.createTextField("1000", true);
		this.countOnlyCheckBox = GuiComponentUtils.createCheckBox("Count Request Only", false);
		this.enablePagingCheckBox = GuiComponentUtils.createCheckBox("Enable paging", false);
		this.selectField = GuiComponentUtils.createTextField("", true);
		this.keyFieldField = GuiComponentUtils.createTextField("", true);
		this.queryField = GuiComponentUtils.createTextField("", true);
		this.queryField.setColumns(50);
		this.fieldNameCombo = GuiComponentUtils.createCombo(false, RetsFieldNameType.values()[0], RetsFieldNameType.values());
		this.hideEmptyCheckBox = GuiComponentUtils.createCheckBox("Hide empty columns", false);
		this.writeToFileCheckBox = GuiComponentUtils.createCheckBox("Write results to file", false);
		
		JLabel limitLabel = GuiComponentUtils.createLabel("Limit", getLimitField());
		Component componentSpacer = Box.createRigidArea(new Dimension(1,1));
		JLabel selectLabel = GuiComponentUtils.createLabel("Columns", getSelectField());
		JLabel keyFieldLabel = GuiComponentUtils.createLabel("Key Field", getKeyFieldField());
		JLabel queryLabel = GuiComponentUtils.createLabel("Query", getQueryField());
		JLabel fieldNameLabel = GuiComponentUtils.createLabel("Field Name", getFieldNameCombo());
		
		this.searchButton = GuiComponentUtils.createButton("Search");
		this.objectsButton = GuiComponentUtils.createButton("Objects");
		
        GuiKeyBindings.setEnterKeyAction(getSearchButton(), this);
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		Group hGroup = layout.createParallelGroup(Alignment.CENTER)
		.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addComponent(limitLabel)
						.addComponent(componentSpacer)
						.addComponent(selectLabel)
						.addComponent(keyFieldLabel)
						.addComponent(queryLabel)
						.addComponent(fieldNameLabel)
						.addComponent(componentSpacer)
						.addComponent(componentSpacer))
				.addGroup(layout.createParallelGroup()
						.addComponent(getLimitField())
						.addComponent(getEnablePagingCheckBox())
						.addComponent(getCountOnlyCheckBox())
						.addComponent(getSelectField())
						.addComponent(getKeyFieldField())
						.addComponent(getQueryField())
						.addComponent(getFieldNameCombo())
						.addComponent(getHideEmptyCheckBox())
						.addComponent(getWriteToFileCheckBox())))
		.addGroup(layout.createSequentialGroup()
				.addComponent(getSearchButton())
				.addComponent(getObjectsButton()));

		Group vGroup = layout.createSequentialGroup()
			.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(limitLabel)
							.addComponent(getLimitField()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(componentSpacer)
							.addComponent(getEnablePagingCheckBox()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(componentSpacer)
							.addComponent(getCountOnlyCheckBox()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(selectLabel)
							.addComponent(getSelectField()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(keyFieldLabel)
							.addComponent(getKeyFieldField()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(queryLabel)
							.addComponent(getQueryField()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(fieldNameLabel)
							.addComponent(getFieldNameCombo()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(componentSpacer)
							.addComponent(getHideEmptyCheckBox()))
					.addGroup(layout.createBaselineGroup(false,true)
							.addComponent(componentSpacer)
							.addComponent(getWriteToFileCheckBox())))
			.addGroup(layout.createParallelGroup(Alignment.LEADING)
					.addComponent(getSearchButton())
					.addComponent(getObjectsButton()));
	
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
	
		layout.linkSize(limitLabel, selectLabel, keyFieldLabel, queryLabel, fieldNameLabel);
		layout.linkSize(SwingConstants.HORIZONTAL, getLimitField(), getSelectField(), getKeyFieldField(), getQueryField(), getFieldNameCombo());
	
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHonorsVisibility(false);
	}
	
	public void enableAll(boolean enabled) {
		getSearchButton().setEnabled(enabled);	
		getHideEmptyCheckBox().setEnabled(enabled);
		getFieldNameCombo().setEnabled(enabled);
		getObjectsButton().setEnabled(enabled);
		getLimitField().setEnabled(enabled);
		getSelectField().setEnabled(enabled);
		getKeyFieldField().setEnabled(enabled);
		getQueryField().setEnabled(enabled);
		getEnablePagingCheckBox().setEnabled(enabled);
		getCountOnlyCheckBox().setEnabled(enabled);
		getWriteToFileCheckBox().setEnabled(enabled);
	}
	
	public JTextField getLimitField() {
		return this.limitField;
	}
	
	public JCheckBox getEnablePagingCheckBox() {
		return this.enablePagingCheckBox;
	}
	
	public JTextField getSelectField() {
		return this.selectField;
	}
	
	public JTextField getKeyFieldField() {
		return this.keyFieldField;
	}
	
	public JTextField getQueryField() {
		return this.queryField;
	}
	
	public JComboBox getFieldNameCombo () {
		return this.fieldNameCombo;
	}
	
	public JCheckBox getHideEmptyCheckBox() {
		return this.hideEmptyCheckBox;
	}
	
	public JCheckBox getWriteToFileCheckBox() {
		return this.writeToFileCheckBox;
	}
	
	public JButton getSearchButton() {
		return this.searchButton;
	}
	
	public JButton getObjectsButton() {
		return this.objectsButton;
	}

	public JCheckBox getCountOnlyCheckBox() {
		return this.countOnlyCheckBox;
	}
	
}
