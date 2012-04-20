package org.trevershick.plebiscite.engine.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.trevershick.plebiscite.engine.AlreadyExistsException;
import org.trevershick.plebiscite.engine.BallotClosedException;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.engine.EmailService;
import org.trevershick.plebiscite.engine.Engine;
import org.trevershick.plebiscite.engine.InvalidDataException;
import org.trevershick.plebiscite.engine.QueueingService;
import org.trevershick.plebiscite.engine.UserCriteria;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;
import org.trevershick.plebiscite.model.VoteType;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class EngineImpl implements Engine, InitializingBean {
	DataService dataService;
	EmailService emailService;
	QueueingService queueingService;

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setQueueingService(QueueingService queueingService) {
		this.queueingService = queueingService;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dataService, "dataService has not been set on "
				+ getClass().getSimpleName());
		Assert.notNull(queueingService, "queueingService has not been set on "
				+ getClass().getSimpleName());
		Assert.notNull(emailService, "emailService has not been set on "
				+ getClass().getSimpleName());
	}

	public User authenticate(String userId, String credentials) {
		User user = this.dataService.getUser(userId);
		return this.dataService.credentialsMatch(user, credentials) ? user : null;
	}

	public Ballot createBallot(User owner, String title)
			throws InvalidDataException {
		// TODO Auto-generated method stub
		return null;
	}

	public User addEmailToBallot(String emailAddress, boolean required)
			throws AlreadyExistsException, BallotClosedException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteBallot(User who, Ballot b) {
		// TODO Auto-generated method stub
		
	}

	public User getUser(String userId) {
		return dataService.getUser(userId);
	}

	public Ballot getBallot(String ballotId) {
		return dataService.getBallot(ballotId);
	}

	public Ballot updateBallot(User updater, Ballot b) {
		Ballot u = this.dataService.getBallot(b.getId());
		u.setDescription(b.getDescription());
		u.setTitle(b.getTitle());
		return this.dataService.save(u);
	}

	public void cancel(Ballot b) {
		Ballot u = this.dataService.getBallot(b.getId());
		if (u.getState().isCancellable()) {
			this.dataService.updateState(u, BallotState.Cancelled);
		}
		// TODO send notification
	}

	public void deactivate(User user) {
		this.dataService.updateState(user, UserStatus.Inactive);
	}

	public User addUserToBallot(String emailAddress, boolean required)
			throws AlreadyExistsException, BallotClosedException {
		throw new RuntimeException("addUserToBallot not implemented");
	}

	public void removeUserFromBallot(String emailAddress)
			throws BallotClosedException {
		throw new RuntimeException("removeUserFromBallot not implemented");
	}

	public void processVote(Ballot ballot, String emailAddress, VoteType vote) {
		throw new RuntimeException("processVote not implemented");
	}

	public void open(Ballot ballot) {
		throw new RuntimeException("open not implemented");
	}

	public boolean userCanVoteOn(Ballot ballot, String emailAddress) {
		throw new RuntimeException("userCanVoteOn not implemented");
		//return false;
	}

	public void ballotsIOwn(User user, Predicate<Ballot> b) {
		throw new RuntimeException("ballotsIOwn not implemented");
	}

	public void ballotsIVotedOn(User user, Predicate<Ballot> b) {
		throw new RuntimeException("ballotsIVotedOn not implemented");
	}

	public void ballotsThatAreOpen(Predicate<Ballot> b) {
		throw new RuntimeException("ballotsThatAreOpen not implemented");
		
	}

	public void processTimedOutBallots() {
		throw new RuntimeException("processTimedOutBallot not implemented");
	}

	public void reactivate(User user) {
		this.dataService.updateState(user, UserStatus.Active);
	}

	public void ban(User user) {
		Preconditions.checkArgument(user != null && user.getEmailAddress() != null);
		this.dataService.updateState(user, UserStatus.Banned);
	}

	public void registerUser(String emailAddress)
			throws AlreadyExistsException, InvalidDataException {
		// TODO creates a user unless one is there
		throw new RuntimeException("registerUser not yet implemented");

	}

	public boolean verifyEmail(String emailAddress, String verificationToken) {
		throw new RuntimeException("verifyEmail not yet implemented");
		// see if the token matches teh stored token
		// if it does mark him registered
		// remove the stored token
//		return false;
	}

	public void reconfirmEmail() {
		
		// TODO send email out to user
		// TODO update the user record with a generated 'token'
		throw new RuntimeException("reconfirmEmail not yet implemented");
	}

	public void updateUser(User user) {
		Preconditions.checkArgument(user != null && user.getEmailAddress() != null);
		User u = this.dataService.getUser(user.getEmailAddress());
		u.setAdmin(user.isAdmin());
		u.setServicesEnabled(user.isServicesEnabled());
		u.setSlug(user.getSlug());
		this.dataService.save(user);
	}

	public void changePassword(User user, String password) {
		Preconditions.checkArgument(user != null && user.getEmailAddress() != null && password != null);
		// TODO password rules
		this.dataService.updatePassword(user, password);
	}

	public void ballotListForAdmin(User user, BallotCriteria criteria,
			Predicate<Ballot> b) {
		// TODO @aop this
		ensureIsAdmin(user);
		this.dataService.ballots(criteria, b);
	}

	public void userListForAdmin(User user, UserCriteria criteria,
			Predicate<User> b) {
		// TODO @aop this
		ensureIsAdmin(user);
		this.dataService.users(criteria,b);
	}

	private void ensureIsAdmin(User user) {
		User u = this.dataService.getUser(user.getEmailAddress());
		if (!u.isAdmin()) {
			throw new SecurityException();
		}
	}

}
