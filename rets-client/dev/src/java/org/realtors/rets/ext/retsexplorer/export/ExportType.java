package org.realtors.rets.ext.retsexplorer.export;


public enum ExportType {

	XLS {
		@Override
		public String getHumanPresentableDescription() {
			return "Microsoft Office Excel Spreadsheet";
		}
		@Override
		public String getHumanPresentableName() {
			return "XLS";
		}
	},
	
	CSV {
		@Override
		public String getHumanPresentableDescription() {
			return "Comma-separated Values Document";
		}
		@Override
		public String getHumanPresentableName() {
			return "CSV";
		}
	},
	
	HTML {
		@Override
		public String getHumanPresentableDescription() {
			return "HTML Document";
		}
		@Override
		public String getHumanPresentableName() {
			return "HTML";
		}
	};
		
	public abstract String getHumanPresentableName();
	
	public abstract String getHumanPresentableDescription();
}
