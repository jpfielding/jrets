package org.realtors.rets.ext.retsexplorer.util;

import org.realtors.rets.common.metadata.Metadata;

public interface QueryManager {
	public String createStatusQuery(String retsServiceName, String resource, String className, Metadata metadata, String...fields);
}