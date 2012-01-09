package org.realtors.rets.retsexplorer;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ProgressBarDialog {
	private static JDialog progDialog;
	private static JProgressBar pb;
	private static JLabel messageArea;
	
	public static void CreateProgDialog(JFrame parent, String title) {
		pb = new JProgressBar(0,1000);
		pb.setIndeterminate(false);
		
		progDialog = new JDialog(parent, title);
		messageArea = new JLabel("");
		progDialog.getContentPane().add(messageArea, BorderLayout.NORTH);
		progDialog.getContentPane().add(pb, BorderLayout.CENTER);
	    progDialog.setSize(400, 65);
	    progDialog.setLocationRelativeTo(null);
	    progDialog.setVisible(true);
	}
	
	public static int getValue() {
		return pb.getValue();
	}
	
	public static void setMaxBoundry(int boundry) {
		pb.setMaximum(boundry);
	}
	
	public static void setMinBoundry(int boundry) {
		pb.setMinimum(boundry);
	}
	
	public static void update(int amount, String message) {
		pb.setValue(amount);
		messageArea.setText(message);
	}
	
	public static void setProgress(int amount) {
		pb.setValue(amount);
	}
	
	public static void setMessage(String message) {
		messageArea.setText(message);
	}
	
	public static void dispose() {
		progDialog.dispose();
	}
}
