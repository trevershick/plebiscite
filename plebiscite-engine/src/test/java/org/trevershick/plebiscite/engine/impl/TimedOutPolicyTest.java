package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.SuperUserClosePolicy;
import org.trevershick.plebiscite.model.TimeoutPolicy;
import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;


public class TimedOutPolicyTest {

	@Test
	public void test_timedout() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -30);
		b.setExpirationDate(c.getTime()); // 30 days ago
		
		TimeoutPolicy timeoutPolicy = new TimeoutPolicy();
		timeoutPolicy.setStateOnTimeout(BallotState.Rejected);

		
		assertEquals("SU Voted Nay", BallotState.Rejected, timeoutPolicy.shouldClose(b, new ArrayList<Vote>()));
	}


	@Test
	public void test_not_timedout() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, +30);
		b.setExpirationDate(c.getTime()); // 30 days from now
		
		TimeoutPolicy timeoutPolicy = new TimeoutPolicy();
		timeoutPolicy.setStateOnTimeout(BallotState.Rejected);

		
		assertEquals("SU Voted Nay", BallotState.Open, timeoutPolicy.shouldClose(b, new ArrayList<Vote>()));
	}

	
	
	
	@Test
	public void test_timeout_priority_low_su_policy_shoudl_take_precendence() {
		DynamoDbBallot b = new DynamoDbBallot();
		b.setState(BallotState.Open);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -30);
		b.setExpirationDate(c.getTime()); // 30 days ago
		
		SuperUserClosePolicy policy = new SuperUserClosePolicy();
		policy.setRejectOnNo(true);
		policy.setUser("trevershick@yahoo.com");
		
		TimeoutPolicy timeoutPolicy = new TimeoutPolicy();
		timeoutPolicy.setStateOnTimeout(BallotState.Accepted);

		b.addPolicy(timeoutPolicy);
		b.addPolicy(policy);
		
		
		Collection<Vote> votes = new ArrayList<Vote>();
		DynamoDbVote v1 = new DynamoDbVote();
		v1.setUserId("trevershick@yahoo.com");
		v1.setType(VoteType.Nay);
		votes.add(v1);
		
		assertEquals("SU Voted Nay even though it's timed out", BallotState.Rejected, b.tallyVotes(votes).getState());
	}


}
