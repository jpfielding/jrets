package org.realtors.rets.retsexplorer.util;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MLookupType;
import org.realtors.rets.common.metadata.types.MTable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class QueryManager {
	
private static Map<String, String> BASE_QUERIES = new ConcurrentHashMap<String, String>(2000, 0.75f, 2);

	@SuppressWarnings("unused")
	public static void createQuery(RetsSource source) {
//		RetsSource retsSource = source;
		// TODO generate default queries per type
		
//		TableInfo<RowOfAgent> agentInfo = retsSource.getAgentTableInfo();
//		for (DataView dataView : agentInfo.getViews()) {
//			put(StringUtils.lowerCase(String.format("%s.%s",retsSource.getId(), dataView.getView())), QueryManager.createBaseQuery(source,agentInfo,dataView));
//		}
//		TableInfo<RowOfOffice> officeInfo = retsSource.getOfficeTableInfo();
//		for (DataView dataView : officeInfo.getViews()) {
//			put(StringUtils.lowerCase(String.format("%s.%s",retsSource.getId(), dataView.getView())), QueryManager.createBaseQuery(source,officeInfo,dataView));
//		}
//		TableInfo<RowOfListing> listingInfo = retsSource.getListingTableInfo();
//		for (DataView dataView : listingInfo.getViews()) {
//			put(StringUtils.lowerCase(String.format("%s.%s",retsSource.getId(), dataView.getView())), QueryManager.createBaseQuery(source,listingInfo,dataView));
//		}
//		TableInfo<RowOfOpenHouse> openHouseInfo = retsSource.getOpenHouseTableInfo();
//		for (DataView dataView : openHouseInfo.getViews()) {
//			put(StringUtils.lowerCase(String.format("%s.%s",retsSource.getId(), dataView.getView())), QueryManager.createBaseQuery(source,openHouseInfo,dataView));
//		}
//		TableInfo<RowOfVirtualTour> virtualTourInfo = retsSource.getVirtualTourTableInfo();
//		for (DataView dataView : virtualTourInfo.getViews()) {
//			put(StringUtils.lowerCase(String.format("%s.%s",retsSource.getId(), dataView.getView())), QueryManager.createBaseQuery(source,virtualTourInfo,dataView));
//		}
	}
	
	public static String createStatusQuery(String retsServiceName, String resource, String className, Metadata metadata, String...fields) {
		String query = BASE_QUERIES.get(String.format("%s.%s.%s", StringUtils.lowerCase(retsServiceName), StringUtils.lowerCase(resource), StringUtils.lowerCase(className)));
		
		if (query == null) {
			RetsSource source = RetsSource.get(StringUtils.upperCase(retsServiceName));
			if (source != null) {
				createQuery(source);
				query = BASE_QUERIES.get(String.format("%s.%s.%s", StringUtils.lowerCase(retsServiceName), StringUtils.lowerCase(resource), StringUtils.lowerCase(className)));
			}
		}
		
		if (query != null) return query;
		if (metadata==null) return "";
		MClass mClass = metadata.getMClass(resource, className);
		if (mClass==null) return "";
		MTable[] mTables = mClass.getMTables();
		if (mTables==null) return "";
		for (String field : fields) {
			for (MTable table : mTables){
				if (!StringUtils.containsIgnoreCase(table.getStandardName(), field)) continue;
				MLookup lookup = metadata.getLookup(table);
				if (lookup == null) {
					if (StringUtils.containsIgnoreCase(table.getDataType(),"Character")) return String.format("~(%s=UNKNOWN)",table.getSystemName());
					if (StringUtils.containsIgnoreCase(table.getDataType(),"DateTime")) return String.format("(%s=1900-01-01T00:00:00+)",table.getSystemName());
					if (StringUtils.containsIgnoreCase(table.getDataType(),"Int")) return String.format("(%s=0+)",table.getSystemName());
					return String.format("(%s=<%s>)",table.getSystemName(), table.getDataType());
				}
				Iterable<MLookupType> filtered = Iterables.filter(Arrays.asList(lookup.getMLookupTypes()), new Predicate<MLookupType>(){
					public boolean apply(MLookupType from) {
						if (StringUtils.containsIgnoreCase(from.getLongValue(), "active")) return true;
						if (StringUtils.containsIgnoreCase(from.getLongValue(), "current")) return true;
						return false;
					}});
				Iterable<String> values = Iterables.transform(filtered, new Function<MLookupType,String>(){
					public String apply(MLookupType from) {
						return from.getValue();
					}});
				return String.format("(%s=|%s)",table.getSystemName(), Joiner.on(",").join(values));
			}
		}
		return "";
	}
	
}