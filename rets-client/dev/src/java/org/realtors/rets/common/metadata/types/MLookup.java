package org.realtors.rets.common.metadata.types;

import java.util.Date;
import java.util.Map;

import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.MetadataType;

public class MLookup extends MetaObject {
	private static final MetadataType[] CHILDREN = { MetadataType.LOOKUP_TYPE };
	private static final MLookupType[] EMPTYLOOKUPTYPES = {};
	public static final String METADATAENTRYID = "MetadataEntryID";
	public static final String LOOKUPNAME = "LookupName";
	public static final String VISIBLENAME = "VisibleName";
	public static final String VERSION = "Version";
	public static final String DATE = "Date";

	public MLookup() {
		this(DEFAULT_PARSING);
	}

	public MLookup(boolean strictParsing) {
		super(strictParsing);
	}

	public String getMetadataEntryID() {
		return getStringAttribute(METADATAENTRYID);
	}

	public String getLookupName() {
		return getStringAttribute(LOOKUPNAME);
	}

	public String getVisibleName() {
		return getStringAttribute(VISIBLENAME);
	}

	public int getVersion() {
		return getIntAttribute(VERSION);
	}

	public Date getDate() {
		return getDateAttribute(DATE);
	}

	public MLookupType getMLookupType(String value) {
		return (MLookupType) getChild(MetadataType.LOOKUP_TYPE, value);
	}

	public MLookupType[] getMLookupTypes() {
		return (MLookupType[]) getChildren(MetadataType.LOOKUP_TYPE).toArray(EMPTYLOOKUPTYPES);
	}

	@Override
	public MetadataType[] getChildTypes() {
		return CHILDREN;
	}

	@Override
	protected String getIdAttr() {
		return LOOKUPNAME;
	}

	@Override
	protected void addAttributesToMap(Map attributeMap) {
		attributeMap.put(METADATAENTRYID, sAttrMetadataEntryId);
		attributeMap.put(LOOKUPNAME, sAlphanum32);
		attributeMap.put(VISIBLENAME, sPlaintext32);
		attributeMap.put(VERSION, sAttrVersion);
		attributeMap.put(DATE, sAttrDate);
	}

}
