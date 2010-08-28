package org.realtors.rets.util;

import java.io.Serializable;

import org.realtors.rets.client.GetMetadataRequest;


public enum RetsMetadataFormat implements Serializable{
	Compact{
		@Override
		public void setMetadataResultFormat(GetMetadataRequest request) {
			request.setCompactFormat();
		}
	},
	StandardXML{
		@Override
		public void setMetadataResultFormat(GetMetadataRequest request){
			/** noop */
		}
	};
	
	public abstract void setMetadataResultFormat(GetMetadataRequest request);

}
