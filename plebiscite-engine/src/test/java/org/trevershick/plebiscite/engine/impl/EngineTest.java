package org.trevershick.plebiscite.engine.impl;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.trevershick.plebiscite.engine.AlreadyExistsException;
import org.trevershick.plebiscite.engine.BallotCompletedException;
import org.trevershick.plebiscite.engine.Engine;
import org.trevershick.plebiscite.engine.InvalidDataException;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.Vote;

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

}
