package org.trevershick.plebiscite.engine;

import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;

import com.google.common.base.Predicate;
/**
 * Abstraction of the data layer.  The engine uses the 'data service' to perform it's persistent operations.
 * This can be implemented in just about anything really provided it supplies all the defined operations.
 * 
 * @author trevershick
 */
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
	/**
	 * Returns user that satisfy the specified criteria, through the predicate
	 * 
	 * @param criteria
	 * @param users
	 */
	void users(UserCriteria criteria, Predicate<User> users);
	/**
	 * Returns votes for the ballot, through the predicate
	 * @param ballot
	 * @param vote
	 */
	void votes(Ballot ballot, Predicate<Vote> vote);
	/**
	 * Return votes for the specified user, through the predicate
	 * @param forUser
	 * @param vote
	 */
	void votes(User forUser, Predicate<Vote> vote);
	
	
	/**
	 * Deletes a vote. All child data and relationships should be deleted as well.
	 * @param vote
	 */
	void delete(Vote vote);
	/**
	 * returns the vote that the given user specified for the ballot. if none is found this should return null;
	 * @param ballot
	 * @param user
	 * @return
	 */
	Vote getVote(Ballot ballot, User user);
	/**
	 * Creates a vote for the given user, assumes it doesn't exist.
	 * @param ballot
	 * @param user
	 * @param vote
	 * @return
	 */
	Vote createVote(Ballot ballot, User user, VoteType vote);
	/**
	 * Update the vote (one that already exists)
	 * @param vote
	 * @return
	 */
	Vote save(Vote vote);
	
	/**
	 * basic factory method, does NOT persist a ballot
	 * @param string 
	 * @return
	 */
	Ballot createBallot();

	/**
	 * Returns the ballot by id or null
	 * @param id
	 * @return
	 */
	Ballot getBallot(String id);
	/**
	 * updates the ballot with the supplied state value
	 * @param u
	 * @param cancelled
	 */
	void updateState(Ballot u, BallotState newState);
	/**
	 * Deletes the ballot and all associated data
	 * @param ballot
	 */
	void delete(Ballot ballot);
	/**
	 * Updates the ballots state from the supplied ballot object
	 * @param ballot
	 * @return
	 */
	Ballot save(Ballot ballot);

	/**
	 * CRUD Style Operation for a User
	 */
	User createUser(String emailAddress);
	/**
	 * CRUD Style Operation for a User
	 */
	User save(User ballot);
	/**
	 * CRUD Style Operation for a User
	 */
	void delete(User user);
	/**
	 * CRUD Style Operation for a User
	 */
	User getUser(String id);

	/**
	 * Marks a user's email verified attribute to true
	 * @param user
	 */
	void markEmailVerified(User user);
	/**
	 * Updates a user's status
	 * @param user
	 * @param inactive
	 */
	void updateState(User user, UserStatus inactive);
	/**
	 * Updates a user's password
	 * @param user
	 * @param password
	 */
	void updatePassword(User user, String password);
	/**
	 * Returns true if the supplied credentials match the users's current credentials
	 * @param userEmailAddress
	 * @param credentials
	 * @return
	 */
	boolean credentialsMatch(String userEmailAddress, String credentials);
}
