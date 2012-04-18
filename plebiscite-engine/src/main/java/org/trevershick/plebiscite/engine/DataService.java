package org.trevershick.plebiscite.engine;

import org.trevershick.plebiscite.model.Ballot;

import com.google.common.base.Predicate;

public interface DataService {
	/**
	 * Queries for ballots given the specified criteria and executes
	 * apply on the call back for each ballot.  returning 'false' from
	 * the predicate will stop the iteration over the ballots
	 * 
	 * @param criteria
	 * @param callback
	 */
	void ballots(BallotCriteria criteria, Predicate<Ballot> callback);
	
	Ballot getBallot(String id);
	void delete(Ballot ballot);
	Ballot save(Ballot ballot);
	
	/**
	 * basic factory method, does NOT persist a ballot
	 * @return
	 */
	Ballot create();
	
	
}
