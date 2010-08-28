package org.realtors.rets.retsexplorer.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.commons.lang.StringUtils;

public abstract class ErrorPopupActionListener implements ActionListener {

	private final String title;

	public ErrorPopupActionListener(String title) {
		this.title = title;
	}
	
	public ErrorPopupActionListener() {
		this("Error");
	}

	public void actionPerformed(ActionEvent ae) {
		try{
			action();
		}catch(Exception e){
			GuiUtils.exceptionPopup(this.title,e);	
		}
	}

	public abstract void action() throws Exception;
	
	public static void main(String... args) throws Exception {
		
		ErrorPopupActionListener error = new ErrorPopupActionListener("Foo") {
			@Override
			public void action() throws Exception {
				throw new RuntimeException(StringUtils.repeat(String.format("%s\n",StringUtils.repeat("Bar", 100)),100));
			}
		};
		error.actionPerformed(null);
	}
}
