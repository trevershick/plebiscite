package org.trevershick.plebiscite.engine.impl;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.trevershick.plebiscite.engine.AlreadyExistsException;
import org.trevershick.plebiscite.engine.BallotCompletedException;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.Engine;
import org.trevershick.plebiscite.engine.InvalidDataException;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.QuorumClosePolicy;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;

import com.google.common.base.Predicate;

public class EngineTest extends AWSTest {

	private Engine engine;
	private DynamoDbUser adminUser;
	private DynamoDbDataService svc;


	@Before
	public void setupEngine() {
		EngineImpl e = new EngineImpl();
		svc = new DynamoDbDataService();
		svc.setDb(client);
		svc.setEnv(env);
		
		e.setDataService(svc);
		this.engine = e;
		this.adminUser = svc.createUser("trevershick@yahoo.com");
	}

	
	@Test(expected=BallotCompletedException.class)
	public void add_user_to_closed_ballot() throws InvalidDataException, AlreadyExistsException, BallotCompletedException {
		DynamoDbUser u = svc.getUser("tshick@hotmail.com");
		if (u != null) {
			this.svc.delete(u);	
		}
		
		
		Ballot b = engine.createBallot(adminUser, "Test Ballot 1");
		engine.cancel(b);
		engine.addUserToBallot(b, "tshick@hotmail.com", true);
	}
	
	@Test
	public void create_user_ballot_with_vote() throws Exception {
		DynamoDbUser u = svc.getUser("tshick@hotmail.com");
		if (u != null) {
			this.svc.delete(u);	
		}
		
		Ballot b = engine.createBallot(adminUser, "Test Ballot 1");
		User u2 = engine.addUserToBallot(b, "tshick@hotmail.com", true);
		assertNotNull(u2);
		assertFalse(u2.isRegistered());
		
		final AtomicReference<Vote> v = new AtomicReference<Vote>();
		engine.votes(u2, new Predicate<Vote>() {
			public boolean apply(Vote input) {
				v.set(input);
				return false;
			}});
		
		assertNotNull("Should have a singel vote record", v.get());
	}

	
	@Test
	public void test_my_ballots() throws Exception {
		DynamoDbUser owner = svc.save(svc.createUser(System.currentTimeMillis() + "@plebiscite.com"));
		DynamoDbUser other = svc.save(svc.createUser((System.currentTimeMillis() + 1) + "@plebiscite.com"));
		DynamoDbUser admin = svc.save(svc.createUser((System.currentTimeMillis() + 2) + "@plebiscite.com"));
		admin.setAdmin(true);
		svc.save(admin);
		
		
		final String title1 = "test1 " + System.currentTimeMillis();
		final String title2 = "test2 " + System.currentTimeMillis();
		engine.updateBallot(owner, engine.createBallot(owner, title1));
		engine.updateBallot(other, engine.createBallot(other, title2));
		
		final AtomicReference<Ballot> ballot1 = new AtomicReference<Ballot>();
		final AtomicReference<Ballot> ballot2 = new AtomicReference<Ballot>();
		
		Predicate<Ballot> p = new Predicate<Ballot>() {
			@Override
			public boolean apply(Ballot input) {
				if (title1.equals(input.getTitle())) ballot1.set(input);
				if (title2.equals(input.getTitle())) ballot2.set(input);
				return true;
			}};
		
		
		engine.ballotListForAdmin(admin, new BallotCriteria(), p);
		assertNotNull(ballot1.get());
		assertNotNull(ballot2.get());
		
		ballot1.set(null);
		ballot2.set(null);
		
		engine.ballotsIOwn(owner, p);
		assertNotNull(ballot1.get());
		assertNull(ballot2.get());
	}

	@Test
	public void test_open_ballots() throws Exception {
		DynamoDbUser owner = svc.save(svc.createUser(System.currentTimeMillis() + "@plebiscite.com"));
		DynamoDbUser other = svc.save(svc.createUser((System.currentTimeMillis() + 1) + "@plebiscite.com"));
		DynamoDbUser admin = svc.save(svc.createUser((System.currentTimeMillis() + 2) + "@plebiscite.com"));
		admin.setAdmin(true);
		svc.save(admin);
		
		
		final String title1 = "test1 " + System.currentTimeMillis();
		final String title2 = "test2 " + System.currentTimeMillis();
		Ballot ballot1 = engine.updateBallot(owner, engine.createBallot(owner, title1));
		Ballot ballot2 = engine.updateBallot(other, engine.createBallot(other, title2));
		ballot1.setOpenBallot(true);
		engine.updateBallot(admin, ballot1);
		
		engine.open(ballot2);
		engine.open(ballot1);
		
		
		final AtomicReference<Ballot> ballot1Ref = new AtomicReference<Ballot>();
		final AtomicReference<Ballot> ballot2Ref = new AtomicReference<Ballot>();
		
		Predicate<Ballot> p = new Predicate<Ballot>() {
			@Override
			public boolean apply(Ballot input) {
				if (title1.equals(input.getTitle())) ballot1Ref.set(input);
				if (title2.equals(input.getTitle())) ballot2Ref.set(input);
				return true;
			}};
		engine.ballotsThatAreOpen(p);
		assertNotNull(ballot1Ref.get());
		assertNull(ballot2Ref.get());

	}
	
	@Test
	public void test_the_vote() throws InvalidDataException, AlreadyExistsException, BallotCompletedException {
		DynamoDbUser u = svc.getUser("tshick@hotmail.com");
		if (u != null) {
			this.svc.delete(u);	
		}
		
		Ballot b = engine.createBallot(adminUser, "Test Ballot 1");
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(true));
		engine.updateBallot(adminUser, b);
		
		User u2 = engine.addUserToBallot(b, "tshick@hotmail.com", true);
		User u3 = engine.addUserToBallot(b, "trevershick@yahoo.com", false);
		engine.open(b);
		assertEquals(BallotState.Open, engine.getBallot(b.getId()).getState());
		
		engine.vote(b, u3, VoteType.Yay);
		assertEquals(BallotState.Open, engine.getBallot(b.getId()).getState());
		
		// the 'required' voter has voted and quorum only rquires one required voter
		engine.vote(b, u2, VoteType.Yay);
		assertEquals(BallotState.Accepted, engine.getBallot(b.getId()).getState());
		
		

	}

	@Test
	public void test_votes_i_need_to_vote_on() throws InvalidDataException, AlreadyExistsException, BallotCompletedException {
		DynamoDbUser u = svc.getUser("tshick@hotmail.com");
		if (u != null) {
			this.svc.delete(u);	
		}
		
		Ballot b = engine.createBallot(adminUser, "Test Ballot 1");
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(true));
		engine.updateBallot(adminUser, b);
		
		User u2 = engine.addUserToBallot(b, "tshick@hotmail.com", true);
		User u3 = engine.addUserToBallot(b, "trevershick@yahoo.com", false);

		final Map<Ballot,Vote> bs = new HashMap<Ballot,Vote>();
		engine.ballotsINeedToVoteOn(u2, new Predicate<Map<Ballot,Vote>>() {
			@Override
			public boolean apply(Map<Ballot,Vote> input) {
				bs.putAll(input);
				return true;
			}});
		assertTrue(bs.containsKey(b));

		bs.clear();
		engine.ballotsINeedToVoteOn(u3, new Predicate<Map<Ballot,Vote>>() {
			@Override
			public boolean apply(Map<Ballot,Vote> input) {
				bs.putAll(input);
				return true;
			}});
		assertFalse(bs.containsKey(b));

	}

	
	
	
	
	@Test
	public void test_ballots_i_voted_on() throws InvalidDataException, AlreadyExistsException, BallotCompletedException {
		DynamoDbUser u = svc.getUser("tshick@hotmail.com");
		if (u != null) {
			this.svc.delete(u);	
		}
		
		Ballot b = engine.createBallot(adminUser, "Test Ballot 1");
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(true));
		engine.updateBallot(adminUser, b);
		
		User u2 = engine.addUserToBallot(b, "tshick@hotmail.com", true);

		final Map<Ballot,Vote> bs = new HashMap<Ballot,Vote>();
		engine.ballotsIVotedOn(u2, new Predicate<Map<Ballot,Vote>>() {
			@Override
			public boolean apply(Map<Ballot,Vote> input) {
				bs.putAll(input);
				return true;
			}});
		assertFalse(bs.containsKey(b));

		
		engine.vote(b, u2, VoteType.Nay);
		
		bs.clear();
		engine.ballotsIVotedOn(u2, new Predicate<Map<Ballot,Vote>>() {
			@Override
			public boolean apply(Map<Ballot,Vote> input) {
				bs.putAll(input);
				return true;
			}});
		assertTrue(bs.containsKey(b));

	}

}
