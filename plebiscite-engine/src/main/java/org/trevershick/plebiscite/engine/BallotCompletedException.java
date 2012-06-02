package org.trevershick.plebiscite.engine;

/**
 * Ballot completed exception represents the exceptional case where the ballot is no longer in
 * a closed (draft) or open state and thus is not editable.
 * 
 * @author trevershick
 *
 */
public class BallotCompletedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4966638261431163273L;

}
