package com.sap.tools.jpicus.impl.serializable;

import java.util.ArrayList;
import java.util.Set;

/**
 * A lightweight collection with a set semantics that uses an array as its internal representation
 * @author pavel
 *
 * @param <E>
 */
public class ArraySet<E> extends ArrayList<E> implements Set<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public ArraySet(int size){
		super(size);
	}

	public boolean add(E e) {
		if(this.contains(e)){
			return false;
		}
		return super.add(e);
	};

}
