package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.State;
import org.trevershick.plebiscite.model.Ballot;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.google.common.base.Predicate;

public class DataServiceTest {

	
	private DynamoDbDataService svc;

	@Before
	public void setup() {
		String accessKey = System.getProperty("accessKey");
		String secretKey = System.getProperty("secretKey");
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		final AmazonDynamoDBClient client = new AmazonDynamoDBClient(awsCredentials);
		svc = new DynamoDbDataService();
		svc.setDb(client);
	
		svc.ballots(new BallotCriteria(), new Predicate<Ballot>() {
			public boolean apply(Ballot input) {
				new DynamoDBMapper(client).delete(input);
				return true;
			}
		});
	}
	
	
	
	@Test
	public void test() {
		
		DynamoDbBallot b = svc.create();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(State.Open);
		svc.save(b);

		b = svc.create();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(State.Closed);
		svc.save(b);

		b = svc.create();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(State.Accepted);
		svc.save(b);

		final List<Ballot> ls = new ArrayList<Ballot>();
		Predicate<Ballot> callback = new Predicate<Ballot>() {
			public boolean apply(Ballot input) {
				ls.add(input);
				return true;
			}
		};

		
		
		BallotCriteria crit = new BallotCriteria();
		crit.addState(State.Open);
		crit.addState(State.Closed);
		svc.ballots(crit, callback);
		assertEquals(2, ls.size()); // query for open and closed

		
		
		
		ls.clear();
		crit = new BallotCriteria();
		crit.addState(State.Open);
		svc.ballots(crit, callback);
		assertEquals(1, ls.size());

	}

}
