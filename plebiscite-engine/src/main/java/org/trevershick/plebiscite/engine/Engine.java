package org.trevershick.plebiscite.engine;

import java.util.Map;

import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;

import com.google.common.base.Predicate;


public interface Engine {
	
	/**
	 * {@value}
	 */
	String EMAIL_LOGIN_URL = "loginLink";
	/**
	 * {@value}
	 */
	String EMAIL_PASSWORD = "password";
	/**
	 * {@value}
	 */
	String EMAIL_VERIFY_URL = "verifyLink";
	
	/**
	 * CRUD Management operation for creating/updating ballots
	 */
	User addUserToBallot(Ballot b, String emailAddress, boolean required) throws AlreadyExistsException, BallotCompletedException;
	/**
	 * CRUD Management operation for creating/updating ballots
	 */
	void removeUserFromBallot(Ballot b, String emailAddress) throws BallotCompletedException;
	/**
	 * CRUD Management operation for creating/updating ballots
	 */
	Ballot createBallot(User owner, String title) throws InvalidDataException;
	/**
	 * CRUD Management operation for creating/updating ballots
	 */
	void deleteBallot(User who, Ballot b);
	/**
	 * CRUD Management operation for creating/updating ballots
	 */
	Ballot updateBallot(User updater, Ballot b);
	
	/**
	 * cancels a ballot
	 * @param ballot
	 * @throws BallotCompletedException
	 */
	void cancel(Ballot ballot) throws BallotCompletedException;
	/**
	 * marks the ballot state to open which makes it available for voting. this 'open' state is the state and not the ballot attribute.
	 *  The 'open' attribute means that anyone can vote on it
	 *  The 'open' state means it's in a state where voters can vote as opposed to'closed' where it's not available for voting.
	 * @param ballot
	 * @param emailparams
	 */
	void open(Ballot ballot, Map<String,Object> emailparams);
	/**
	 * If the ballot is an 'open' ballot, then will return true.
	 * If the ballot is not an 'open' ballot but the user is a registered voter on the ballot, will return true
	 * 
	 * @param ballot
	 * @param emailAddress
	 * @return true if the user can vote on the ballot
	 */
	boolean userCanVoteOn(Ballot ballot, String emailAddress);

	void ballotListForAdmin(User user, BallotCriteria criteria, Predicate<Ballot> b);
	/**
	 * Iterates over the ballots the user 'user' owns and calls back to b with the results
	 * stops if b returns false
	 * @param b
	 */
	void ballotsIOwn(User user, Predicate<Ballot> b);
	/**
	 * Iterates over the ballots the user 'user' has voted on and calls back to b with the results
	 * stops if b returns false
	 * @param b
	 */
	void ballotsIVotedOn(User user,Predicate<Map<Ballot,Vote>> b);
	/**
	 * Iterates over open ballots can calls back to 'b'
	 * stops if b returns false
	 * @param b
	 */
	void ballotsThatAreOpen(Predicate<Ballot> b);
	/**
	 * Locates all ballots where 'user' is a required voter and hasn't yet voted and calls back to 'b'
	 * with the results.
	 * 
	 * stops if b returns false
	 * @param user
	 * @param b
	 */
	public void ballotsINeedToVoteOn(User user,Predicate<Map<Ballot,Vote>> b);
	/**
	 * Iterate over the open ballots and determine if any of them should be closed. this is meant to 
	 * handle ballots htat have an expiration policy assigned to them.
	 */
	void processTimedOutBallots();
	/**
	 * returns the full user list for the admin user. this is a potentially expensive operation
	 * stops if b returns false
	 * @param user
	 * @param criteria
	 * @param b
	 */
	void userListForAdmin(User user, UserCriteria criteria, Predicate<User> b);
	/**
	 * updates a user
	 * @param user
	 */
	void updateUser(User user);
	/**
	 * deactivates a user
	 * @param user
	 */
	void deactivate(User user);
	/**
	 * reactivates a banned user
	 * @param user
	 */
	void reactivate(User user);
	/**
	 * bans a user from the system
	 * @param user
	 */
	void ban(User user);
	
	/**
	 * Registers a user with the plebiscite system.
	 * @param emailAddress
	 * @param emailParams
	 * @throws AlreadyExistsException
	 * @throws InvalidDataException
	 */
	void registerUser(String emailAddress, Map<String,Object> emailParams) throws AlreadyExistsException, InvalidDataException;
	/**
	 * Authenticates the given user with the userId and their credentials. 
	 * @param userId
	 * @param credentials
	 * @return a user object if authentication succeeds, null otherwise
	 * @throws BannedUserException
	 */
	User authenticate(String userId, String credentials) throws BannedUserException;
	/**
	 * called by clients (web layer) to verify that the email and link passed in are correct.
	 * @param emailAddress
	 * @param verificationToken
	 * @return
	 */
	boolean verifyEmail(String emailAddress, String verificationToken);
	
	/**
	 * Creates and sends an email to the given user notifying the user of their temporary password.  The email
	 * supplies a link for the user to click which is a required member of the emailParams parameter.
	 * 
	 * @param emailAddress
	 * @param emailParams {{@link #EMAIL_LOGIN_URL} {@link #EMAIL_PASSWORD}} are required
	 * 
	 */
	void sendTemporaryPassword(String emailAddress, Map<String,Object> emailParams);
	/**
	 * Sends an email verification email to the person specified by 'emailAddress'. 
	 * @param emailAddress
	 * @param emailParams - parameters used by the email templating engine, currently 'site' is required
	 */
	void sendEmailVerificationEmail(String emailAddress, Map<String,Object> emailParams);
	/**
	 * Changes the password for the supplied user to 'password'
	 * @param user not null
	 * @param password not null
	 */
	void changePassword(User user, String password);
	/**
	 * Returns the user instance identified by <em>userId</em> or null
	 * @param userId
	 * @return
	 */
	User getUser(String userId);
	/**
	 * Returns the ballot instance identified by <em>ballotId</em> or null
	 * 
	 * @param ballotId
	 * @return
	 */
	Ballot getBallot(String ballotId);
	/**
	 * Returns all of the votes on the provided ballot through the vote predicate.
	 * @param ballot
	 * @param vote
	 */
	void votes(Ballot ballot, Predicate<Vote> vote);
	/**
	 * Returns all of the users vote through the 'vote' predicate
	 * @param forUser
	 * @param vote
	 */
	void votes(User forUser, Predicate<Vote> vote);
	/**
	 * @return my vote for this ballot, or null
	 */
	Vote myVote(User me, Ballot onBallot);
	/**
	 * Registers a vote for the votingUser on the supplied ballot.
	 * @param onBallot
	 * @param votingUser
	 * @param vote
	 */
	void vote(Ballot onBallot, User votingUser, VoteType vote);

}
