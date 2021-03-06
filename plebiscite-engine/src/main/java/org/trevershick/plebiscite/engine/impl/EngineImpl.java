package org.trevershick.plebiscite.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.trevershick.plebiscite.engine.AlreadyExistsException;
import org.trevershick.plebiscite.engine.BallotCompletedException;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.engine.EmailService;
import org.trevershick.plebiscite.engine.Engine;
import org.trevershick.plebiscite.engine.InvalidDataException;
import org.trevershick.plebiscite.engine.UserCriteria;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

public class EngineImpl implements Engine, InitializingBean {
	DataService dataService;
	EmailService emailService;

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dataService, "dataService has not been set on "
				+ getClass().getSimpleName());
		Assert.notNull(emailService, "emailService has not been set on "
				+ getClass().getSimpleName());
	}

	public User authenticate(String email, String credentials) {
		if (this.dataService.credentialsMatch(email, credentials)) {
			return dataService.getUser(email);
		} else {
			return null;
		}
	}

	public Ballot createBallot(User owner, String title)
			throws InvalidDataException {
		Ballot b = dataService.createBallot();
		b.setOwner(owner.getEmailAddress());
		b.setTitle(title);
		this.dataService.save(b);
		return b;
	}

	public void deleteBallot(User who, Ballot b) {
		throw new RuntimeException("Not Implemented");
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
		u.setExpirationDate(b.getExpirationDate());
		u.setVoteChangeable(b.isVoteChangeable());
		u.setOpenBallot(b.isOpenBallot());
		u.setPolicies(b.getPolicies());
		return this.dataService.save(u);
	}

	public void cancel(Ballot u) {
		BallotState oldState = u.getState();
		// Ballot u = this.dataService.getBallot(b.getId());
		if (u.getState().isCancellable()) {
			this.dataService.updateState(u, BallotState.Cancelled);
			sendBallotStateChangeNotificationEmail(u, BallotState.Open, oldState, new HashMap<String,Object>());
		}
	}

	public void deactivate(User user) {
		this.dataService.updateState(user, UserStatus.Inactive);
	}

	public User addUserToBallot(Ballot ballot, String emailAddress,
			boolean required) throws AlreadyExistsException,
			BallotCompletedException {

		if (ballot.getState() != BallotState.Open
				&& ballot.getState() != BallotState.Closed) {
			throw new BallotCompletedException();
		}

		User userToAdd = this.dataService.createUser(emailAddress);
		this.dataService.save(userToAdd);

		Vote v = dataService.createVote(ballot, userToAdd, VoteType.None);
		if (v != null) {
			v.setRequired(required);
			this.dataService.save(v);
		}
		return userToAdd;
	}

	// TODO - add security, only admin or the ballot owner can do this
	public void removeUserFromBallot(Ballot ballot, String emailAddress)
			throws BallotCompletedException {
		if (ballot.getState().isComplete()) {
			throw new BallotCompletedException();
		}

		User u = dataService.getUser(emailAddress);
		if (u == null) {
			return;
		}
		Vote vote = dataService.getVote(ballot, u);
		if (vote != null) {
			dataService.delete(vote);
		}
	}

	public void open(Ballot b, Map<String, Object> emailParams) {
		BallotState oldState = b.getState();
		if (b.getState().equals(BallotState.Closed)) {
			this.dataService.updateState(b, BallotState.Open);
			sendBallotOpenNotificationEmail(b, emailParams);
			sendBallotStateChangeNotificationEmail(b, BallotState.Open, oldState, emailParams);
		}
	}

	public boolean userCanVoteOn(Ballot ballot, String emailAddress) {
		if (ballot.isOpenBallot()) {
			return true;
		}
		User u = getUser(emailAddress);
		if (u == null) {
			// if the user isn't in the system then he/she wasn't added to a
			// ballot
			return false;
		}
		return this.dataService.getVote(ballot, u) != null;
	}

	public void ballotsIOwn(User user, Predicate<Ballot> b) {
		BallotCriteria bc = new BallotCriteria();
		bc.addOwner(user.getEmailAddress());
		this.dataService.ballots(bc, b);
	}

	@Override
	public void ballotsINeedToVoteOn(User user, Predicate<Map<Ballot, Vote>> b) {
		final List<Vote> votes = new ArrayList<Vote>();
		this.dataService.votes(user, new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				votes.add(input);
				return true;
			}
		});
		Iterable<Vote> nonvotes = Iterables.filter(votes,
				new Predicate<Vote>() {
					@Override
					public boolean apply(Vote input) {
						return input.getType().isNone() && input.isRequired();
					}
				});
		Iterable<Map<Ballot, Vote>> ballots = Iterables.transform(nonvotes,
				new Function<Vote, Map<Ballot, Vote>>() {
					@Override
					public Map<Ballot, Vote> apply(Vote input) {
						Map<Ballot, Vote> m = new MapMaker().initialCapacity(1)
								.makeMap();
						Ballot b = dataService.getBallot(input.getBallotId());
						if (b != null) {
							m.put(b, input);
						}
						return m;
					}
				});
		for (Map<Ballot, Vote> bal : ballots) {
			b.apply(bal);
		}
	}

	public void ballotsIVotedOn(User user, Predicate<Map<Ballot, Vote>> b) {
		final List<Vote> votes = new ArrayList<Vote>();
		this.dataService.votes(user, new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				votes.add(input);
				return true;
			}
		});
		Iterable<Vote> votesonly = Iterables.filter(votes,
				new Predicate<Vote>() {
					@Override
					public boolean apply(Vote input) {
						return input.getType().isAVote();
					}
				});
		Iterable<Map<Ballot, Vote>> ballots = Iterables.transform(votesonly,
				new Function<Vote, Map<Ballot, Vote>>() {
					@Override
					public Map<Ballot, Vote> apply(Vote input) {
						Map<Ballot, Vote> m = new MapMaker().initialCapacity(1)
								.makeMap();
						Ballot b = dataService.getBallot(input.getBallotId());
						if (b != null) {
							m.put(b, input);
						}
						return m;
					}
				});
		for (Map<Ballot, Vote> bal : ballots) {
			b.apply(bal);
		}
	}

	public void ballotsThatAreOpen(Predicate<Ballot> b) {
		BallotCriteria bc = new BallotCriteria();
		bc.addState(BallotState.Open);
		bc.setOpenBallots(true);
		this.dataService.ballots(bc, b);

	}

	public void processTimedOutBallots() {
		ballotsThatAreOpen(new Predicate<Ballot>() {
			@Override
			public boolean apply(Ballot ballot) {
				final List<Vote> vs = new ArrayList<Vote>();
				votes(ballot, new Predicate<Vote>() {
					@Override
					public boolean apply(Vote input) {
						vs.add(input);
						return true;
					}
				});
				BallotState currentState = ballot.getState();
				BallotState newState = ballot.tallyVotes(vs).getState();
				if (newState.equals(currentState)) {
					return true; // don't change the ballot's state
				}
				sendBallotStateChangeNotificationEmail(ballot, newState, currentState, new HashMap<String, Object>());
				dataService.save(ballot);
				return true;
			}});
	}

	public void reactivate(User user) {
		this.dataService.updateState(user, UserStatus.Active);
	}

	public void ban(User user) {
		Preconditions.checkArgument(user != null
				&& user.getEmailAddress() != null);
		this.dataService.updateState(user, UserStatus.Banned);
	}

	/**
	 * doesn't mark the user as registered. just creates a user
	 */
	public void registerUser(String emailAddress,
			Map<String, Object> emailParams) throws AlreadyExistsException,
			InvalidDataException {
		User user = this.dataService.getUser(emailAddress);

		if (user != null) {
			throw new AlreadyExistsException();
		}
		user = this.dataService.createUser(emailAddress);
		sendEmailVerificationEmail(user, emailParams);
		this.dataService.save(user);
	}

	private void sendEmailVerificationEmail(User user,
			Map<String, Object> emailParams) {
		// construct a token
		String token = user.generateVerificationToken();
		// send an email
		String to = user.getEmailAddress();

		try {
			HashMap<String, Object> p = Maps.newHashMap(emailParams);
			p.put("email", to);
			p.put("token", token);
			Map<String, String> m = new EmailProducer().buildMailMessage(
					"emailverification", p);
			this.dataService.save(user);
			if (emailService != null) {
				emailService.sendEmail(to, m.get(EmailProducer.SUBJECT),
						m.get(EmailProducer.BODY));
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

	}

	private void sendBallotOpenNotificationEmail(final Ballot ballot,
			final Map<String, Object> emailParams) {

		this.votes(ballot, new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				HashMap<String, Object> p = Maps.newHashMap(emailParams);
				p.put("ballot", ballot);
				p.put("voter", input);
				Map<String, String> m = new EmailProducer().buildMailMessage(
						"ballotopennotification", p);
				if (emailService != null) {
					emailService.sendEmail(input.getUserId(),
							m.get(EmailProducer.SUBJECT),
							m.get(EmailProducer.BODY));
				}
				return true;
			}
		});

		try {
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

	}

	private void sendBallotStateChangeNotificationEmail(final Ballot ballot,
			BallotState newState, BallotState oldState,
			final Map<String, Object> emailParams) {

		HashMap<String, Object> p = Maps.newHashMap(emailParams);
		p.put("ballot", ballot);
		p.put("old", oldState);
		p.put("new", newState);

		Map<String, String> m = new EmailProducer().buildMailMessage(
				"ballotstatechangednotification", p);
		if (emailService != null) {
			emailService.sendEmail(ballot.getOwner(),
					m.get(EmailProducer.SUBJECT), m.get(EmailProducer.BODY));
		}

	}

	public boolean verifyEmail(String emailAddress, String verificationToken) {
		User user = this.getUser(emailAddress);
		if (user == null) {
			return false;
		}

		// see if the token matches teh stored token
		// if it does mark him registered
		// remove the stored token
		if (user.verificationTokenMatches(verificationToken)) {
			this.dataService.markEmailVerified(user);
			return true;
		}
		return false;

	}

	@Override
	public void sendEmailVerificationEmail(String emailAddress,
			Map<String, Object> emailParams) {
		Preconditions.checkArgument(emailAddress != null,
				"emailAddress cannot be null");
		User u = getUser(emailAddress);
		this.sendEmailVerificationEmail(u, emailParams);
	}

	public void updateUser(User user) {
		Preconditions.checkArgument(user != null
				&& user.getEmailAddress() != null);

		this.dataService.save(user);
	}

	public void changePassword(User user, String password) {
		Preconditions.checkArgument(user != null
				&& user.getEmailAddress() != null && password != null);
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
		this.dataService.users(criteria, b);
	}

	private void ensureIsAdmin(User user) {
		User u = this.dataService.getUser(user.getEmailAddress());
		if (!u.isAdmin()) {
			throw new SecurityException();
		}
	}

	public void votes(Ballot ballot, Predicate<Vote> vote) {
		dataService.votes(ballot, vote);
	}

	public void votes(User forUser, Predicate<Vote> vote) {
		dataService.votes(forUser, vote);
	}

	@Override
	public void sendTemporaryPassword(String emailAddress,
			Map<String, Object> emailParams) {
		Preconditions.checkArgument(emailAddress != null,
				"emailAddress is required");
		User user = this.dataService.getUser(emailAddress);
		Preconditions.checkNotNull(user, "user not found");
		String pwd = user.generateTemporaryPassword();
		this.dataService.updatePassword(user, pwd);

		try {
			HashMap<String, Object> p = Maps.newHashMap(emailParams);
			p.put("user", user);
			p.put("password", pwd);

			Map<String, String> m = new EmailProducer().buildMailMessage(
					"temporarypassword", p);
			if (emailService != null) {
				emailService
						.sendEmail(user.getEmailAddress(),
								m.get(EmailProducer.SUBJECT),
								m.get(EmailProducer.BODY));
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}

	}

	@Override
	public void vote(Ballot onBallot, User votingUser, VoteType vote) {
		Ballot ballot = getBallot(onBallot.getId());
		if (ballot.isComplete()) {
			return;
		}
		Vote v = dataService.getVote(onBallot, votingUser);
		if (v != null && (v.getType().isNone() || ballot.isVoteChangeable())) {
			v.setType(vote);
			dataService.save(v);
		} else if (v == null) {
			v = dataService.createVote(ballot, votingUser, vote);
			dataService.save(v);
		} else {
			return;
		}
		final List<Vote> vs = new ArrayList<Vote>();
		votes(ballot, new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				vs.add(input);
				return true;
			}
		});
		BallotState currentState = ballot.getState();
		BallotState newState = ballot.tallyVotes(vs).getState();
		if (newState.equals(currentState)) {
			return;
		}
		sendBallotStateChangeNotificationEmail(ballot, newState, currentState, new HashMap<String, Object>());
		dataService.save(ballot);

	}

	@Override
	public Vote myVote(User me, Ballot onBallot) {
		return dataService.getVote(onBallot, me);
	}

}
