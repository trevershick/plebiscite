package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.QuorumClosePolicy;
import org.trevershick.plebiscite.model.SuperUserClosePolicy;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.QueryRequest;
import com.amazonaws.services.dynamodb.model.QueryResult;
import com.google.common.base.Predicate;

public class DataServiceTest {

	
	private static DynamoDbDataService svc;
	private static AmazonDynamoDBClient client;
	private static PlebisciteEnvironment env;

	@BeforeClass
	public static void setup() {
		String accessKey = System.getProperty("accessKey");
		String secretKey = System.getProperty("secretKey");
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		env = new PlebisciteEnvironment();
//		env.setQualifier("FUNC");
		
		client = new AmazonDynamoDBClient(awsCredentials);
		svc = new DynamoDbDataService();
		svc.setEnv(env);
		svc.setDb(client);
	}

	@Before
	public void clean() {
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
	
	
	
	@Test
	public void test() {
		
		DynamoDbBallot b = svc.createBallot();
		b.setTitle("Test " + System.currentTimeMillis());
		b.setState(BallotState.Open);
		b.setExpirationDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));
		b.addClosePolicy(new QuorumClosePolicy(5,false));
		b.addClosePolicy(new SuperUserClosePolicy());
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

}
