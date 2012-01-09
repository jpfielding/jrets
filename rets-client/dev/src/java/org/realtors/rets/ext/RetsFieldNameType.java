package org.realtors.rets.ext;

import org.realtors.rets.client.SearchRequest;
import org.realtors.rets.common.metadata.types.MTable;


public enum RetsFieldNameType {
	SystemName {
	    @Override
		public String produceFieldName(MTable field) {
			return field.getSystemName();
		}
	},
	
	StandardName {
	    @Override
		public void setSearchType(SearchRequest searchRequest) {
			searchRequest.setStandardNames();
		}
	    @Override
		public String produceFieldName(MTable field) {
			return field.getStandardName();
		}
	},
	
	LongName {
	    @Override
		public String produceFieldName(MTable field) {
			return field.getLongName();
		}
	},
	
	DBName {
	    @Override
		public String produceFieldName(MTable field) {
			return field.getDBName();
		}
	},
	
	ShortName {
	    @Override
		public String produceFieldName(MTable field) {
			return field.getShortName();
		}
	},;

	public final void setSearchNameType(SearchRequest request) {
	    this.setSearchType(request);
	}

    public final String getFieldName(MTable field) {
	    if(field == null) return null;
	    return this.produceFieldName(field);
	}
    
	public void setSearchType(SearchRequest searchRequest) {
		searchRequest.setSystemNames();
	}
    public abstract String produceFieldName(MTable field);
}

