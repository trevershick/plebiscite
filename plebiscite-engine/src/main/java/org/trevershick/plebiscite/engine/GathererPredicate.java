package org.trevershick.plebiscite.engine;

import java.util.ArrayList;

import com.google.common.base.Predicate;

/**
 * Implementation of the Predicate interface, this implementation stores the 
 * value passed to it and always returns true.   Since the ballot engine uses
 * the predicate as a search result callback, this class makes it easy to 
 * gather the values that were used as callback arguments
 * 
 *	@author trevershick
 */
public class GathererPredicate<T> extends ArrayList<T> implements Predicate<T> {

	private static final long serialVersionUID = 231833614482704827L;

	@Override
	public boolean apply(T input) {
		this.add(input);
		return true;
	}
}
