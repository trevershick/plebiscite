package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotClosePolicy;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.QuorumClosePolicy;
import org.trevershick.plebiscite.model.SuperUserClosePolicy;
import org.trevershick.plebiscite.model.TimeoutPolicy;

import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.QueryRequest;
import com.amazonaws.services.dynamodb.model.QueryResult;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class DataServiceBallotTest extends AWSTest {

	
	private static DynamoDbDataService svc;


	@Before
	public void clean() {
		svc = new DynamoDbDataService();
		svc.setEnv(env);
		svc.setDb(client);

		svc.ballots(new BallotCriteria(), new Predicate<Ballot>() {
			public boolean apply(Ballot input) {
				svc.delete(input);
				return true;
			}
		});
		for (BallotState bs: BallotState.values()) {
			Key startKey = null;
			do {
				QueryResult query = client.query(new QueryRequest()
					.withTableName(env.qualifyTableName("SecondaryIndex"))
					.withHashKeyValue(new AttributeValue("Ballot#State#" + bs.name()))
					.withExclusiveStartKey(startKey));
				
				startKey = query.getLastEvaluatedKey();
				List<Map<String, AttributeValue>> items = query.getItems();
				for (Map<String,AttributeValue> item : items) {
					client.deleteItem(new DeleteItemRequest()
						.withTableName(env.qualifyTableName("SecondaryIndex"))
						.withKey(new Key(item.get("IndexValue"), item.get("IndexRef"))));
				}
			} while (startKey != null);
			
		}
	}

	@Test(expected=RuntimeException.class)
	public void test_cant_update_state_to_null() {
		DynamoDbBallot b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		svc.save(b);
		svc.updateState(b, null); // can't pass in null
	}
	
	
	@Test
	public void test_update_state() {
		DynamoDbBallot b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		svc.save(b);
		b = (DynamoDbBallot) svc.getBallot(b.getId());
		assertEquals(b.getState(), BallotState.Closed);
		
		
		svc.updateState(b, BallotState.Open);
		b = (DynamoDbBallot) svc.getBallot(b.getId());
		assertEquals(b.getState(), BallotState.Open);

		svc.updateState(b, BallotState.Closed);
		b = (DynamoDbBallot) svc.getBallot(b.getId());
		assertEquals(b.getState(), BallotState.Closed);

		svc.updateState(b, BallotState.Rejected);
		b = (DynamoDbBallot) svc.getBallot(b.getId());
		assertEquals(b.getState(), BallotState.Rejected);

		svc.updateState(b, BallotState.TimedOut);
		b = (DynamoDbBallot) svc.getBallot(b.getId());
		assertEquals(b.getState(), BallotState.TimedOut);
	}
	
	@Test
	public void test_create_save() {
		
		DynamoDbBallot b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(BallotState.Open);
		b.setExpirationDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));
//		b.addClosePolicy(new QuorumClosePolicy(5,false));
//		b.addClosePolicy(new SuperUserClosePolicy());
		svc.save(b);

		b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(BallotState.Closed);
		svc.save(b);

		b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(BallotState.Closed);
		svc.save(b);

		b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(BallotState.Accepted);
		svc.save(b);

		final List<Ballot> ls = new ArrayList<Ballot>();
		Predicate<Ballot> callback = new Predicate<Ballot>() {
			public boolean apply(Ballot input) {
				ls.add(input);
				return true;
			}
		};

		
		
		BallotCriteria crit = new BallotCriteria();
		crit.addState(BallotState.Open);
		crit.addState(BallotState.Closed);
		svc.ballots(crit, callback);
		assertEquals(3, ls.size()); // query for open and closed
		
		ls.clear();
		crit = new BallotCriteria();
		crit.addState(BallotState.Open);
		svc.ballots(crit, callback);
		assertEquals(1, ls.size());

	}

	
	@Test
	public void test_create_save_with_policy() {
		
		DynamoDbBallot b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(BallotState.Open);
		b.setExpirationDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));
		b.addPolicy(new TimeoutPolicy().withStateOnTimeout(BallotState.Accepted));
		b.addPolicy(new SuperUserClosePolicy().withAcceptOnYes(true).withRejectOnNo(true).withUser("trevershick@gmail.com"));
		b.addPolicy(new QuorumClosePolicy().withNumberRequired(3).withRequiredVotersOnly(true));
		svc.save(b);
		
		Ballot ballot = svc.getBallot(b.getId());
		Collection<BallotClosePolicy> policies = ballot.getPolicies();
		assertEquals(b.getTitle(), ballot.getTitle());
		assertEquals(BallotState.Open, ballot.getState());
		assertTrue("Should hvae saved/restored the timeout policy", Iterables.any(policies, new Predicate<BallotClosePolicy>() {
			@Override
			public boolean apply(BallotClosePolicy input) {
				return (input instanceof TimeoutPolicy) 
						&& (((TimeoutPolicy)input).getStateOnTimeout() == BallotState.Accepted);
			}
		}));
		assertTrue("Should hvae saved/restored the su policy", Iterables.any(policies, new Predicate<BallotClosePolicy>() {
			@Override
			public boolean apply(BallotClosePolicy input) {
				return (input instanceof SuperUserClosePolicy) 
						&& (((SuperUserClosePolicy)input).getAcceptOnYes() == true)
						&& (((SuperUserClosePolicy)input).getRejectOnNo() == true)
						&& (((SuperUserClosePolicy)input).getUser().equals("trevershick@gmail.com"));
			}
		}));
		assertTrue("Should hvae saved/restored the su policy", Iterables.any(policies, new Predicate<BallotClosePolicy>() {
			@Override
			public boolean apply(BallotClosePolicy input) {
				return (input instanceof SuperUserClosePolicy) 
						&& (((SuperUserClosePolicy)input).getAcceptOnYes() == true)
						&& (((SuperUserClosePolicy)input).getRejectOnNo() == true)
						&& (((SuperUserClosePolicy)input).getUser().equals("trevershick@gmail.com"));
			}
		}));
		assertTrue("Should hvae saved/restored the quorum policy", Iterables.any(policies, new Predicate<BallotClosePolicy>() {
			@Override
			public boolean apply(BallotClosePolicy input) {
				return (input instanceof QuorumClosePolicy) 
						&& (((QuorumClosePolicy)input).getNumberRequired() == 3)
						&& (((QuorumClosePolicy)input).isRequiredVotersOnly() == true);
			}
		}));
		
	}
}
