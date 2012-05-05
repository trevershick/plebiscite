package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.SuperUserClosePolicy;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;


public class SuperUserVoteTest {

	@Test
	public void test_acceptonno_su_votes_no() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);

		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setRejectOnNo(true);
		policy.setUser("trevershick@yahoo.com");
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.Nay);
		votes.add(v1);
		
		assertEquals("SU Voted Nay", BallotState.Rejected, policy.shouldClose(b, votes));
	}

	@Test
	public void test_acceptonno_su_votes_yes() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setRejectOnNo(true);
		policy.setUser("trevershick@yahoo.com");
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.Yay);
		votes.add(v1);
		
		assertEquals("SU Voted Yay - no effect", BallotState.Open, policy.shouldClose(b, votes));
	}
	@Test
	public void test_acceptonno_su_votes_abstain() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setRejectOnNo(true);
		policy.setUser("trevershick@yahoo.com");
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.Abstain);
		votes.add(v1);
		
		assertEquals("SU Voted Abstain - no effect", BallotState.Open, policy.shouldClose(b, votes));
	}
	@Test
	public void test_acceptonno_su_hasnt_voted() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setRejectOnNo(true);
		policy.setUser("trevershick@yahoo.com");
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.None);
		votes.add(v1);
		
		assertEquals("SU Voted Abstain - no effect", BallotState.Open, policy.shouldClose(b, votes));
	}

	
	
	@Test
	public void test_super_user_votes_yes() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);

		
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setAcceptOnYes(true);
		policy.setUser("trevershick@yahoo.com");
		
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.None);
		votes.add(v1);
		
		assertEquals("Hasn't voted", BallotState.Open, policy.shouldClose(b, votes));
		
		
		v1.setType(VoteType.Yay);
		assertEquals("Super User Voted Yes", BallotState.Accepted, policy.shouldClose(b, votes));
		
	}

	@Test
	public void test_super_user_abstains() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);

		
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setAcceptOnYes(true);
		policy.setUser("trevershick@yahoo.com");
		
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.Abstain);
		votes.add(v1);
		assertEquals("SU Abstained",BallotState.Open,  policy.shouldClose(b, votes));
	}

	@Test
	public void test_super_user_votesno() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);

		
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setAcceptOnYes(true);
		policy.setUser("trevershick@yahoo.com");
		
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.Nay);
		votes.add(v1);
		assertEquals("SU Abstained", BallotState.Open, policy.shouldClose(b, votes));
	}

	
	@Test
	public void test_nonsuper_user_votes_yes() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);

		
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setAcceptOnYes(true);
		policy.setUser("trevershick@yahoo.com");
		
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("tshick@hotmail.com");
		v1.setType(VoteType.None);
		votes.add(v1);
		
		assertEquals("Hasn't voted",BallotState.Open, policy.shouldClose(b, votes));
		
		
		v1.setType(VoteType.Yay);
		assertEquals("Non-Super User Voted Yes",BallotState.Open, policy.shouldClose(b, votes));
		
	}

}
