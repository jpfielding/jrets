package org.realtors.rets.ext.util;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RetsCredentials implements Serializable {
    private String username;
    private String password;
    
    public RetsCredentials() {
    	super();
    }

    public RetsCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
        		.append("username", this.username)
        		.append("password", this.password)
        	.toString();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        		.append(this.username)
        		.append(this.password)
        	.toHashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof RetsCredentials)) return false;
        
        RetsCredentials other = (RetsCredentials) obj;
        return new EqualsBuilder()
        		.append(this.username, other.username)
        		.append(this.password, other.password)
        	.isEquals();
    }
}
