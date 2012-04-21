package org.trevershick.plebiscite.engine;

import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;

import com.google.common.base.Predicate;


public interface Engine {

	// ballot CRUD operations
	User addUserToBallot(Ballot b, String emailAddress, boolean required) throws AlreadyExistsException, BallotCompletedException;
	void removeUserFromBallot(Ballot b, String emailAddress) throws BallotCompletedException;
	
	Ballot createBallot(User owner, String title) throws InvalidDataException;
	void deleteBallot(User who, Ballot b);
	Ballot updateBallot(User updater, Ballot b);
	void processVote(Ballot ballot, String emailAddress, VoteType vote);
	
	// extended ballot operations
	void cancel(Ballot ballot) throws BallotCompletedException;
	void open(Ballot ballot);
	boolean userCanVoteOn(Ballot ballot, String emailAddress);

	void ballotListForAdmin(User user, BallotCriteria criteria, Predicate<Ballot> b);
	void ballotsIOwn(User user, Predicate<Ballot> b);
	void ballotsIVotedOn(User user,Predicate<Ballot> b);
	void ballotsThatAreOpen(Predicate<Ballot> b);
	/**
	 * Iterate over the open ballots and find timed out ballots
	 */
	void processTimedOutBallots();
	
	
	void userListForAdmin(User user, UserCriteria criteria, Predicate<User> b);
	void updateUser(User user);
	void deactivate(User user);
	void reactivate(User user);
	void ban(User user);
	
	void registerUser(String emailAddress) throws AlreadyExistsException, InvalidDataException;
	User authenticate(String userId, String credentials) throws BannedUserException;
	boolean verifyEmail(String emailAddress, String verificationToken);
	void reconfirmEmail();
	void changePassword(User user, String password);
	User getUser(String userId);
	Ballot getBallot(String ballotId);
	void votes(Ballot ballot, Predicate<Vote> vote);
	void votes(User forUser, Predicate<Vote> vote);

}
