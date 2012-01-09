package org.realtors.rets.ext.transaction.metadata;

import org.apache.commons.lang.StringUtils;
import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MLookupType;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.RetsField;
import org.realtors.rets.ext.RetsMetadataTransaction;
import org.realtors.rets.ext.transaction.metadata.exceptions.FieldNotFoundException;
import org.realtors.rets.ext.transaction.metadata.exceptions.NoLookupNameException;


public class GetLookupValues implements RetsMetadataTransaction<MLookupType[]>{
	
	private GetLookup getLookup;
	private RetsField field;
	private GetField getField;

	public GetLookupValues(RetsField field) {
		this.getLookup = new GetLookup(field.getResourceClass().getResource());
		this.field = field;
		this.getField = new GetField(this.field);
	}
	
	public MLookupType[] execute(Metadata metadata) throws Exception {
		MTable fieldMetadata = this.getField.execute(metadata);
		if (fieldMetadata == null)
			throw new FieldNotFoundException(this.field);
		String lookupName = fieldMetadata.getLookupName();
		if(StringUtils.isBlank(lookupName))
			throw new NoLookupNameException(this.field);
		this.getLookup.setLookupName(lookupName);
		MLookup lookup = this.getLookup.execute(metadata);
		return lookup.getMLookupTypes();
	}
	
}
