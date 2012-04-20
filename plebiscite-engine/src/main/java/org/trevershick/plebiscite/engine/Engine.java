package org.trevershick.plebiscite.engine;

import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.VoteType;

import com.google.common.base.Predicate;


public interface Engine {

	// ballot CRUD operations
	User addUserToBallot(String emailAddress, boolean required) throws AlreadyExistsException, BallotClosedException;
	void removeUserFromBallot(String emailAddress) throws BallotClosedException;
	
	Ballot createBallot(User owner, String title) throws InvalidDataException;
	void deleteBallot(User who, Ballot b);
	Ballot updateBallot(User updater, Ballot b);
	void processVote(Ballot ballot, String emailAddress, VoteType vote);
	
	// extended ballot operations
	void cancel(Ballot ballot) throws BallotClosedException;
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
	
}
