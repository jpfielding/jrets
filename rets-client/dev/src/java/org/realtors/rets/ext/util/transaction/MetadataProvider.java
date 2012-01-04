package org.realtors.rets.ext.util.transaction;

import java.io.Serializable;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.ext.util.RetsClient;
import org.realtors.rets.ext.util.RetsTransaction;

import com.google.common.base.Function;

public enum MetadataProvider implements Function<RetsClient,RetsTransaction<Metadata>>,Serializable{
	CACHED(){
		@Override
		public RetsTransaction<Metadata> apply(RetsClient client) {
			return new CachedMetadataTransaction(client);
		}
	},
	FULL(){
		@Override
		public RetsTransaction<Metadata> apply(RetsClient retsClient) {
			return new GetMetadata(retsClient.getRetsProfile().getMetadataFormat());
		}
	},
	INCREMENTAL(){
		@Override
		public RetsTransaction<Metadata> apply(RetsClient retsClient) {
			return new GetIncrementalMetadata(retsClient);
		}
	};
	
	public abstract RetsTransaction<Metadata> apply(RetsClient retsClient);
}


