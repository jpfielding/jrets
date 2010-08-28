package org.realtors.rets.retsexplorer.util;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.realtors.rets.retsexplorer.RetsExplorer;
import org.realtors.rets.retsexplorer.login.MainTabbedPane;


public class GuiKeyBindings {
	private static int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	private static Action click(final JButton button) {
		return new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		    	button.doClick();
		    }
		};
	}
	
	public static void setCloseTabAction(final MainTabbedPane tabbedPane, final RetsExplorer frameToClose){
		ActionMap actionMap = tabbedPane.getActionMap();
		InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask), "close");
		actionMap.put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getTabCount() == 1 && tabbedPane.getTitleAt(0).equals("login")) {
					frameToClose.dispose(); //if the only tab to close is the first login screen, then close window
				}
				else {
					tabbedPane.closeCurrentTab();
				}
			}
		});
	}
	
	public static void setEnterKeyAction(JButton buttonToClick, JComponent object){
		ActionMap actionMap = object.getActionMap();
		InputMap inputMap = object.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke("ENTER"), "enter");
		actionMap.put("enter", click(buttonToClick));
	}

	public static void setLoginAction(final MainTabbedPane pane) {
		InputMap inmap = pane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		pane.getActionMap().put("openLoginPane", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pane.createTab();
				pane.setSelectedIndex(pane.getTabCount() - 1);
			}
		});
		inmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask), "openLoginPane");
		inmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, mask), "openLoginPane");
	}
}