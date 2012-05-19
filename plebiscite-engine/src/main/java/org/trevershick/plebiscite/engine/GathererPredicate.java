package org.trevershick.plebiscite.engine;

import java.util.ArrayList;

import com.google.common.base.Predicate;

public class GathererPredicate<T> extends ArrayList<T> implements Predicate<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 231833614482704827L;

	@Override
	public boolean apply(T input) {
		this.add(input);
		return true;
	}
}
