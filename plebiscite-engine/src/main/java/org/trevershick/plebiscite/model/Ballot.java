package org.trevershick.plebiscite.model;

import java.util.Collection;
import java.util.Date;



public interface Ballot {
	String getId();
	
    String getTitle();
    void setTitle(String title);

    String getDescription();
    void setDescription(String description);
	
	BallotState getState();
	
	int getVoteCount();
	String getLastVoter();
	
	/**
	 * An open ballot is a ballot in which anyone can vote on.
	 * @return
	 */
	boolean isOpenBallot();
	/**
	 * A ballot that allows users to change their vote (before the ballot has closed)
	 * will return true from this method.
	 * @return
	 */
	boolean isVoteChangeable();
	
	Date getExpirationDate();
	/**
	 * Not all ballots expire.  Some will run indefinitely until cancelled or 
	 * until the voting requirements are satisfied.
	 * @return true if this ballot actually expires
	 * 
	 */
	boolean expires();
	
	Collection<BallotClosePolicy> getClosePolicies();
	boolean isComplete();
	Collection<User> getRequiredVoters();
}
