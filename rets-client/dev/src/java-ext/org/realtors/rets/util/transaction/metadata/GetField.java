package org.realtors.rets.util.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.util.RetsField;
import org.realtors.rets.util.RetsFieldNameType;
import org.realtors.rets.util.RetsMetadataTransaction;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GetField implements RetsMetadataTransaction<MTable> {
	private GetClass getClass;
	private String systemFieldName;
	private RetsField field;

	public GetField(RetsField field) {
		this.getClass = new GetClass(field.getResourceClass());

		String fieldName = field.getFieldName();
		if(RetsFieldNameType.SystemName.equals(field.getFieldNameType()))
			this.systemFieldName = fieldName;
		else
			this.field = field;
	}

	public MTable execute(Metadata metadata) throws Exception {
		MClass resourceClass = this.getClass.execute(metadata);
		if( resourceClass == null ) return null;
		return resourceClass.getMTable(this.getSystemFieldName(resourceClass));
	}

	private String getSystemFieldName(MClass resourceClass) {
		if(this.systemFieldName == null)
			this.systemFieldName = this.fetchSystemFieldName(resourceClass);
		return this.systemFieldName;
	}

	private String fetchSystemFieldName(MClass resourceClass) {
		final RetsFieldNameType fieldNameType = this.field.getFieldNameType();
		final String fieldName = GetField.this.field.getFieldName();

		MTable[] fields = resourceClass.getMTables();
		MTable match = Iterables.find(Lists.newArrayList(fields), new Predicate<MTable>() {
			public boolean apply(MTable fieldToTest) {
				return fieldName.equals(fieldNameType.getFieldName(fieldToTest));
			}});
		
		if(match == null)
			throw new IllegalStateException(this.field + " is not available.");
		return RetsFieldNameType.SystemName.getFieldName(match);
	}
}
