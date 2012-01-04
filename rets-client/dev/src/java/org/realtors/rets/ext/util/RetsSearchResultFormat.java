package org.realtors.rets.ext.util;

import org.realtors.rets.client.SearchRequest;


public enum RetsSearchResultFormat {
	Compact{
		@Override
		public void setSearchResultFormat(SearchRequest searchRequest) {
			searchRequest.setFormatCompact();
		}
	},
	CompactDecoded{
		@Override
		public void setSearchResultFormat(SearchRequest searchRequest) {
			searchRequest.setFormatCompactDecoded();
		}
	},
	StandardXML{
		@Override
		public void setSearchResultFormat(SearchRequest searchRequest) {
			searchRequest.setFormatStandardXml();
		}
	};
	
	public abstract void setSearchResultFormat(SearchRequest searchRequest);
	
}
