package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class DataServiceUserTest extends AWSTest {

	private static DynamoDbDataService svc;


	@Before
	public void clean() {
		svc = new DynamoDbDataService();
		svc.setEnv(env);
		svc.setDb(client);
		// remove all users that have a numeric @yahoo address.
		svc.users(new Predicate<User>() {
			public boolean apply(User input) {
				if (input.getEmailAddress().matches("\\d+@yahoo\\.com")
						|| input.getEmailAddress().matches("\\d+@plebiscite\\.com")
						) {
					svc.delete(input);
				}
				return true;
			}
		});
	}

	@Test
	public void test_user_votes() {
		String em = System.currentTimeMillis() + "@yahoo.com";
		DynamoDbUser u = svc.createUser(em);
		svc.save(u);
		
		u.setVotedOnBallots(Sets.newHashSet("x","y","z"));
		svc.save(u);
		
		DynamoDbUser user = svc.getUser(u.getEmailAddress());
		Set<String> vs = user.getVotedOnBallots();
		assertEquals(Sets.newHashSet("x","y","z"),vs);
	}
	
	
	
	@Test
	public void testSaveUser() {
		String em = System.currentTimeMillis() + "@yahoo.com";
		User u = svc.createUser(em);
		assertNull(u.getSlug());
		assertEquals(UserStatus.Active, u.getUserStatus());
		assertFalse(u.isAdmin());
		assertFalse(u.isRegistered());
		assertFalse(u.isServicesEnabled());
		
		
		u.setSlug("slug");
		u.setAdmin(true);
		u.setRegistered(true);
		u.setServicesEnabled(true);
		svc.save(u);

		
		u = svc.getUser(u.getEmailAddress());
		assertEquals("slug", u.getSlug());
		assertEquals(UserStatus.Active, u.getUserStatus());
		assertTrue(u.isAdmin());
		assertTrue(u.isRegistered());
		assertTrue(u.isServicesEnabled());
		
	}


	@Test
	public void testDeleteUser() {
		String em = System.currentTimeMillis() + "@yahoo.com";
		User u = svc.createUser(em);
		u = svc.getUser(em);
		assertNotNull(u);
		
		svc.delete(u);
		u = svc.getUser(u.getEmailAddress());
		assertNull(u);
	}

	
	
	@Test
	public void testUsers() {
		final List<String> addresses = new ArrayList<String>();
		
		for (int i=0;i < 19;i++) {
			String em = System.currentTimeMillis() + "@yahoo.com";
			addresses.add(em);
			svc.createUser(em);
		}
		final List<User> us = new ArrayList<User>();
		
		svc.users(new Predicate<User>(){
			public boolean apply(User input) {
				//remove the address of the user, an empty list will mean we got them all back.
				addresses.remove(input.getEmailAddress());
				return true;
			}
		});
		
		assertTrue("Should have gotten back all the users we created", addresses.isEmpty());
		
		for (User user : us) {
			svc.delete(user);
		}
	}

	@Test
	public void testUpdateStateUserUserStatus() {
		String em = System.currentTimeMillis() + "@yahoo.com";
		User u = svc.createUser(em);
		assertEquals(UserStatus.Active, u.getUserStatus());
		
		svc.updateState(u, UserStatus.Banned);
		u = svc.getUser(em);
		assertEquals(UserStatus.Banned, u.getUserStatus());
		
		svc.updateState(u, UserStatus.Inactive);
		u = svc.getUser(em);
		assertEquals(UserStatus.Inactive, u.getUserStatus());
		
		svc.delete(u);
	}

	@Test
	public void testUpdatePassword() {
		String em = System.currentTimeMillis() + "@yahoo.com";
		User u = svc.createUser(em);
		svc.updatePassword(u, "newpass");
		DynamoDbUser us = svc.getUser(em);
		assertFalse("newpass".equals(us.getCredentials()));
		System.out.println(us.getCredentials());
	}
	
	@Test
	public void test_hash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		http://www.online-convert.com/result/c681b5d6708fbbb39398f2bef54d5f78
			
		assertEquals("5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8", svc.hash("password"));
	}

}
