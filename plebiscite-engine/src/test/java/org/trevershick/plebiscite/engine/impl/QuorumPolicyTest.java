package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.QuorumClosePolicy;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;


public class QuorumPolicyTest {

	@Test
	public void test_quorum_not_satisfied_no_votes() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(false));

		// no votes
		Collection<Vote> votes = new ArrayList<Vote>();
		
		assertEquals("Quorum Not Satisfied - no votes", BallotState.Open, b.tallyVotes(votes).getState());
	}
	
	@Test
	public void test_quorum_satisfied_yay_voter() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(false));

		// no votes
		Collection<Vote> votes = new ArrayList<Vote>();
		votes.add(new DynamoDbVote().withType(VoteType.Yay));
		
		assertEquals("Quorum Satisfied by a 'yay' voter", BallotState.Accepted, b.tallyVotes(votes).getState());
	}
	
	@Test
	public void test_quorum_satisfied_nay_voter() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(false));

		// no votes
		Collection<Vote> votes = new ArrayList<Vote>();
		votes.add(new DynamoDbVote().withType(VoteType.Nay));
		
		assertEquals("Quorum Satisfied by a 'nay' voter", BallotState.Rejected, b.tallyVotes(votes).getState());
	}
	
	@Test
	public void test_quorum_unsatisfied_abstain_voter() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(false));

		// no votes
		Collection<Vote> votes = new ArrayList<Vote>();
		votes.add(new DynamoDbVote().withType(VoteType.Abstain));
		
		assertEquals("Quorum UnSatisfied", BallotState.Open, b.tallyVotes(votes).getState());
	}
	
	@Test
	public void test_quorum_unsatisfied_ignore_none_vote_types() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(false));

		// no votes
		Collection<Vote> votes = new ArrayList<Vote>();
		votes.add(new DynamoDbVote().withType(VoteType.None));
		
		assertEquals("Quorum Unsatisfied -only 'none' votes", BallotState.Open, b.tallyVotes(votes).getState());
	}


	
	
	@Test
	public void test_quorum_satisfied_yay_voter_when_required() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(true));

		// no votes
		Collection<Vote> votes = new ArrayList<Vote>();
		votes.add(new DynamoDbVote().withType(VoteType.Yay).withRequired(true));
		
		assertEquals("Quorum Satisfied with 1 required voter", BallotState.Accepted, b.tallyVotes(votes).getState());
	}

	
	
	@Test
	public void test_quorum_unsatisfied_yay_voter_but_not_required_voter() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(1).withRequiredVotersOnly(true));

		// no votes
		Collection<Vote> votes = new ArrayList<Vote>();
		votes.add(new DynamoDbVote().withType(VoteType.Yay).withRequired(false));
		
		assertEquals("Quorum Not Satisfied - need required voter", BallotState.Open, b.tallyVotes(votes).getState());
	}

}
