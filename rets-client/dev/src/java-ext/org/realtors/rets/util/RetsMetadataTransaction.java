package org.realtors.rets.util;

import org.realtors.rets.common.metadata.Metadata;

public interface RetsMetadataTransaction<T> {
	public T execute(Metadata metadata) throws Exception;
}
