package org.realtors.rets.ext.retsexplorer.util;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXPanel;

public class GuiComponentUtils {
	public static JPanel createPanel() {
		JXPanel panel = new JXPanel();
		panel.setDoubleBuffered(true);
		panel.setOpaque(true);
		panel.setBackground(UIManager.getColor("Panel.background"));
		panel.setScrollableTracksViewportHeight(false);
		panel.setScrollableTracksViewportWidth(false);
		return panel;
	}
	
	public static JTextField createTextField(String text, boolean editable) {
		JTextField field = new JTextField();
		field.setDoubleBuffered(true);
		field.setEditable(editable)
;		field.setText(StringUtils.defaultString(text));
		return field;
	}

	public static JLabel createLabel(String text,Component labelFor) {
		JLabel label = new JLabel();
		label.setText(StringUtils.defaultString(text));
		label.setDoubleBuffered(true);
		label.setLabelFor(labelFor);
		return label;
	}

	public static JButton createButton(String text) {
		JButton button = new JButton();
		button.setText(StringUtils.defaultString(text));
		button.setDoubleBuffered(true);
		return button;
	}
	
	public static JComboBox createCombo(boolean editable, Object selected, Object... data) {
		JComboBox combo = new JComboBox(data);
		combo.setDoubleBuffered(true);
		combo.setEditable(editable);
		if (selected != null) {
			combo.setSelectedItem(selected);
		}
		return combo;
	}
	
	public static JCheckBox createCheckBox(String text, boolean checked) {
		JCheckBox check = new JCheckBox();
		check.setDoubleBuffered(true);
		check.setText(text);
		check.setSelected(checked);
		return check;
	}
}
