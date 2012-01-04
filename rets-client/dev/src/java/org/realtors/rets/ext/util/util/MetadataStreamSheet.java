package org.realtors.rets.ext.util.util;

import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MLookupType;
import org.realtors.rets.common.metadata.types.MResource;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.util.ResourceClass;
import org.realtors.rets.ext.util.RetsSearchType;
import org.realtors.rets.ext.util.transaction.metadata.GetClasses;
import org.realtors.rets.ext.util.transaction.metadata.GetFields;
import org.realtors.rets.ext.util.transaction.metadata.GetResources;

public class MetadataStreamSheet {
	private static final String[] RETS_FIELD_ATTRIBS = new String[]{
    	MTable.METADATAENTRYID,
    	MTable.SYSTEMNAME,
    	MTable.STANDARDNAME,
    	MTable.LONGNAME,
    	MTable.DBNAME,
    	MTable.SHORTNAME,
    	MTable.MAXIMUMLENGTH,
    	MTable.DATATYPE,
    	MTable.PRECISION,
    	MTable.SEARCHABLE,
    	MTable.INTERPRETATION,
    	MTable.ALIGNMENT,
    	MTable.USESEPARATOR,
    	MTable.EDITMASKID,
    	MTable.LOOKUPNAME,
    	MTable.MAXSELECT,
    	MTable.UNITS,
    	MTable.INDEX,
    	MTable.MINIMUM,
    	MTable.MAXIMUM,
    	MTable.DEFAULT,
    	MTable.REQUIRED,
    	MTable.SEARCHHELPID,
    	MTable.UNIQUE,
	};
	private static final String[] RETS_LOOKUP_TYPE_ATTRIBS = new String[]{
		MLookupType.VALUE,
		MLookupType.LONGVALUE,
		MLookupType.SHORTVALUE,
	};
	private final Metadata metadata;

	public MetadataStreamSheet(Metadata metadata){
		this.metadata = metadata;
	}
	
    public void writeFile(OutputStream output) throws Exception {
		// make the workbook
		HSSFWorkbook wb = new HSSFWorkbook();
        for( MResource resource : new GetResources().execute(this.metadata) ){
        	this.addResourceLookups(wb, resource);
        	this.addResourceClass(wb, resource);
        }
        // write it to the user
        wb.write(output);
    }

	private void addResourceLookups(HSSFWorkbook wb, MResource resource) {
		MLookup[] lookups = this.metadata.getResource(resource.getId()).getMLookups();
		if (lookups == null || lookups.length == 0) return;
		
	    short rowCount = 0;
	    short colCount = 0;

	    // add the resource lookups as a sheet
		HSSFSheet sheet = wb.createSheet(this.validSheetName(String.format("%s - Lookups", resource.getResourceID())));
		// a header style
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
		style.setFont(font);
		
		// add the header
		HSSFRow headerRow = sheet.createRow(rowCount++);
		this.addColumn(colCount++, style, headerRow, "LOOKUP_ID");
		this.addColumn(colCount++, style, headerRow, "LOOKUP_NAME");
		for( String attrib : RETS_LOOKUP_TYPE_ATTRIBS ) {
			this.addColumn(colCount++, style, headerRow, attrib);
		}
	    // add the values
		for( MLookup lookup : lookups ){
			for( MLookupType lookupType : lookup.getMLookupTypes() ) {
			    HSSFRow row = sheet.createRow(rowCount++);
			    colCount = 0;
		    	this.addColumn(colCount++, null, row, lookup.getId());
		    	this.addColumn(colCount++, null, row, lookup.getLookupName());
			    for( String attrib : RETS_LOOKUP_TYPE_ATTRIBS ) {
			    	this.addColumn(colCount++, null, row, lookupType.getAttributeAsString(attrib));
			    }
			}
		}
	}

	private void addResourceClass(HSSFWorkbook wb, MResource resource) throws Exception {
		// add the class metadata as a sheet
		RetsSearchType searchType = RetsSearchType.forName(resource.getResourceID());
		for (MClass resourceClass : new GetClasses(searchType).execute(this.metadata)) {
		    HSSFSheet sheet = wb.createSheet(this.validSheetName(String.format("%s - %s", resource.getResourceID(),resourceClass.getId())));

		    String resourceClassId = resourceClass.getId();
		    short rowCount = 0;
		    short colCount = 0;

		    // a header style
			HSSFCellStyle style = wb.createCellStyle();
			HSSFFont font = wb.createFont();
			font.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
			style.setFont(font);
			
			// add the header
		    HSSFRow headerRow = sheet.createRow(rowCount++);
		    for( String attrib : RETS_FIELD_ATTRIBS ) {
		    	this.addColumn(colCount++, style, headerRow, attrib);
		    }
		    
		    // add the values
		    for( MTable table : new GetFields(new ResourceClass(searchType, resourceClassId)).execute(this.metadata) ){
			    HSSFRow row = sheet.createRow(rowCount++);
			    colCount = 0;
			    for( String attrib : RETS_FIELD_ATTRIBS ) {
			    	this.addColumn(colCount++, null, row, table.getAttributeAsString(attrib));
			    }
		    }
		}
	}

	private String validSheetName(String name) {
		return StringUtils.abbreviate(name.replaceAll("\\/\\\\\\*\\?\\[\\]]", " "),31);
	}

	private void addColumn(int colCount, HSSFCellStyle style, HSSFRow row, String attrib) {
		HSSFCell cell = row.createCell(colCount);
		cell.setCellValue(attrib);
		if( style != null ) cell.setCellStyle(style);
	}

}
