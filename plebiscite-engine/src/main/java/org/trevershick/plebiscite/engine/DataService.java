package org.trevershick.plebiscite.engine;

import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.User;

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

	void cancel(Ballot ballot);
	void deactivate(User user);

	
	void delete(Ballot ballot);
	void delete(User ballot);
	Ballot save(Ballot ballot);
	User save(User ballot);
	
	/**
	 * basic factory method, does NOT persist a ballot
	 * @return
	 */
	Ballot createBallot();
	User createUser(String emailAddress);
	
	Ballot getBallot(String id);
	User getUser(String id);
	
}
