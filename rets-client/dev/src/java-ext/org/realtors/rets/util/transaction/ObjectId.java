package org.realtors.rets.util.transaction;

import java.io.Serializable;


public final class ObjectId implements Serializable{
	public static final ObjectId ALL = new ObjectId("*");
	public static final ObjectId PREFERRED = new ObjectId("0");
	
	/** A strange non-constant enum allowing user-defined object id's but also providing well known symbols. */
	public ObjectId(int id) {
		this(String.valueOf(id));
		if(id <= 0)//fail slow!
			throw new IllegalArgumentException("id must be > 0");
	}


	private String name;
	private ObjectId(String name) {
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
	
	@Override
	public String toString() {
	    return this.name;
    }
	@Override
	public boolean equals(Object obj) {
		if( obj == null ) return false;
		if( !(obj instanceof ObjectId) ) return false;
		return this.name.equals(((ObjectId)obj).name);
	}
}