package net.java.dev.jminimizer.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Thiago Leão Moreira <thiago.leão.moreira@terra.com.br>
 * 
 */
public class InstructionSet extends AbstractSet {
	
	protected LinkedList list;

	/**
	 * 
	 */
	public InstructionSet() {
		super();
		list= new LinkedList();
	}

	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return list.size();
	}

	/**
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		return list.iterator();
	}

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o) {
		if (list.contains(o)) {
			return false;
		} else {
			list.add(o);
			return true;
		}
	}

}
