package org.realtors.rets.ext.retsexplorer.find;

import org.jdesktop.swingx.JXFindPanel;
import org.jdesktop.swingx.search.SearchFactory;

public class RetsSearchFactory extends SearchFactory {

	
	@Override
	public JXFindPanel createFindPanel() {
		return new RetsFindPanel();
	}

}
