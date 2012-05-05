package org.trevershick.plebiscite.engine;

import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;

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
	void users(UserCriteria criteria, Predicate<User> users);
	void votes(Ballot ballot, Predicate<Vote> vote);
	void votes(User forUser, Predicate<Vote> vote);
	
	
	
	void delete(Vote vote);
	Vote getVote(Ballot ballot, User user);
	Vote createVote(Ballot ballot, User user, VoteType vote);
	Vote save(Vote vote);
	
	
	
	/**
	 * basic factory method, does NOT persist a ballot
	 * @param string 
	 * @return
	 */
	Ballot createBallot();
	
	Ballot getBallot(String id);
	void updateState(Ballot u, BallotState cancelled);
	void delete(Ballot ballot);
	Ballot save(Ballot ballot);

	User createUser(String emailAddress);
	User save(User ballot);
	void delete(User user);
	User getUser(String id);
	void markEmailVerified(User user);
	void updateState(User user, UserStatus inactive);
	void updatePassword(User user, String password);
	boolean credentialsMatch(User user, String credentials);
}
