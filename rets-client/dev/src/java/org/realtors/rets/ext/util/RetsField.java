package org.realtors.rets.ext.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.util.transaction.metadata.GetField;


public class RetsField {
    private ResourceClass resourceClass;
	private RetsFieldNameType fieldNameType;
	private String fieldName;
	
    public RetsField(ResourceClass resourceClass, RetsFieldNameType fieldNameType, String fieldName) {
        this.resourceClass = resourceClass;
        this.fieldNameType = fieldNameType;
		this.fieldName = fieldName;
    }

    public ResourceClass getResourceClass() {
        return this.resourceClass;
    }
    public RetsFieldNameType getFieldNameType() {
		return this.fieldNameType;
	}
	public String getFieldName() {
		return this.fieldName;
	}
	
	public MTable getMetadata(RetsClient client) throws Exception {
		return new GetField(this).execute(client.getMetadata());
	}
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        		.append(this.resourceClass)
        		.append(this.fieldNameType)
        		.append(this.fieldName)
        	.toHashCode();
    }
    
    @Override
	public boolean equals(Object obj) {
	    if(obj == this) return true;
	    if(!(obj instanceof RetsField)) return false;
	    
	    RetsField other = (RetsField) obj;
        return new EqualsBuilder()
        		.append(this.resourceClass, other.resourceClass)
        		.append(this.fieldNameType, other.fieldNameType)
        		.append(this.fieldName, other.fieldName)
        	.isEquals();
    }
	
    @Override
    public String toString() {
        return new ToStringBuilder(this)
        		.append("resourceClass", this.resourceClass)
        		.append("fieldNameType", this.fieldNameType)
        		.append("fieldName", this.fieldName)
        	.toString();
    }
}
