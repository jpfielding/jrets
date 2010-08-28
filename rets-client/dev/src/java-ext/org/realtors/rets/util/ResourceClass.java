package org.realtors.rets.util;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ResourceClass implements Serializable {
    private RetsSearchType resource;
    private String className;
    
    public ResourceClass(String fullyQualifiedResourceClass) {
        this(fullyQualifiedResourceClass.split("\\.")[0], fullyQualifiedResourceClass.split("\\.")[1]);
    }

    public ResourceClass(String resource, String resourceClass) {
        this(RetsSearchType.forName(resource), resourceClass);
    }
    
    public ResourceClass(RetsSearchType resource, String resourceClass) {
        this.resource = resource;
        this.className = resourceClass;
    }
    
    public RetsSearchType getResource() {
        return this.resource;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    @Override
	public String toString() {
        return this.resource.getName() + "." + this.className;
    }
    
    @Override
	public int hashCode() {
        return new HashCodeBuilder()
        			.append(this.resource)
        			.append(this.className)
        		.toHashCode();
    }
    
    @Override
	public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof ResourceClass)) return false;
        
        ResourceClass other = (ResourceClass) obj;
        return new EqualsBuilder()
        			.append(other.resource, this.resource)
        			.append(other.className, this.className)
        		.isEquals();
    }
}