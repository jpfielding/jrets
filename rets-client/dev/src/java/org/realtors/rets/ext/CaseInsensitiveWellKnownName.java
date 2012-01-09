package org.realtors.rets.ext;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/** The strange enum case insensitiviy towards names is forced by Marketlinx RETS servers */
public abstract class CaseInsensitiveWellKnownName implements Serializable {
    private static Map<Class<? extends CaseInsensitiveWellKnownName>, Map<String, ? extends CaseInsensitiveWellKnownName>> enumMap =
		Collections.synchronizedMap(new HashMap<Class<? extends CaseInsensitiveWellKnownName>, Map<String, ? extends CaseInsensitiveWellKnownName>>());
    
    public static <T extends CaseInsensitiveWellKnownName> T forName(String name, Class<T> wellKnownNameType) {
		if(name == null) return null;
		
        T ciwknEnumValue = CaseInsensitiveWellKnownName.getEnumMap(wellKnownNameType).get(name);
        if(ciwknEnumValue != null && ciwknEnumValue.name.equals(name))
            return ciwknEnumValue;
        
		try {
			return wellKnownNameType.getDeclaredConstructor(new Class[] { String.class }).newInstance(new Object[] { name });
		} catch (Exception e) {
			throw new RuntimeException("Couldn't find required 1 String arg constructor for " + wellKnownNameType.getName());
		}
    }

    public static <T extends CaseInsensitiveWellKnownName> T[] getAll(Class<T> wellKnownNameType) {
	    Collection<T> all = CaseInsensitiveWellKnownName.getEnumMap(wellKnownNameType).values();
        return all.toArray((T[]) Array.newInstance(wellKnownNameType, all.size()));
	}

	private static <T extends CaseInsensitiveWellKnownName> Map<String, T> getEnumMap(final Class<T> wellKnownNameType) {
		synchronized (wellKnownNameType) {
			Map<String, T> ciwknEnumMap = (Map<String, T>) CaseInsensitiveWellKnownName.enumMap.get(wellKnownNameType);
			if(ciwknEnumMap == null) {
	            ciwknEnumMap = CaseInsensitiveWellKnownName.createCiwknEnumMap(wellKnownNameType);
				CaseInsensitiveWellKnownName.enumMap.put(wellKnownNameType, ciwknEnumMap);
			}
	        return ciwknEnumMap;
		}
    }

	private static <T extends CaseInsensitiveWellKnownName> Map<String, T> createCiwknEnumMap(final Class<T> wellKnownNameType) {
		Map<String, T> ciwknEnumMap;
		Iterable<Field> ciwknFields = Iterables.filter(Lists.newArrayList(wellKnownNameType.getDeclaredFields()), new Predicate<Field>() {
		    public boolean apply(Field field) {
		        Class fieldType = field.getType();
		        return wellKnownNameType.equals(fieldType);
		    }
		});
		
		ciwknEnumMap = new HashMap<String, T>();
		for(Field field : ciwknFields) {
			T ciwknEnumValue;
		    try {
				ciwknEnumValue = (T) field.get(null);
		    } catch (Exception e) {
		        throw new RuntimeException("Problem building enum map for " + wellKnownNameType, e);
		    }
			ciwknEnumMap.put(ciwknEnumValue.name, ciwknEnumValue);
		}
		return ciwknEnumMap;
	}
    
	private String name;
    
    protected CaseInsensitiveWellKnownName(String name) {
		assert !StringUtils.isBlank(name) : "name cannot be blank";
		this.name = name;
	}

    @Override
    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return this.name.toLowerCase().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
		if(obj == null) return false;
        if(!this.getClass().equals(obj.getClass())) return false;
        
        CaseInsensitiveWellKnownName other = (CaseInsensitiveWellKnownName) obj;
        return this.name.equalsIgnoreCase(other.name);
    }
}
