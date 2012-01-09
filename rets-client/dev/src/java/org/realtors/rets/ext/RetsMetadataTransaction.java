package org.realtors.rets.ext;

import org.realtors.rets.common.metadata.Metadata;

public interface RetsMetadataTransaction<T> {
	public T execute(Metadata metadata) throws Exception;
}
