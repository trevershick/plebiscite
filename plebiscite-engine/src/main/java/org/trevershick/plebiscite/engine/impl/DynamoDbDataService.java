package org.trevershick.plebiscite.engine.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.engine.UserCriteria;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;
import org.trevershick.plebiscite.model.Vote;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.BatchGetItemRequest;
import com.amazonaws.services.dynamodb.model.BatchGetItemResult;
import com.amazonaws.services.dynamodb.model.BatchResponse;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.KeysAndAttributes;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DynamoDbDataService implements DataService,InitializingBean {
	AmazonDynamoDB db;
	PlebisciteEnvironment env;
	
	public void setDb(AmazonDynamoDB db) {
		this.db = db;
	}
	public void setEnv(PlebisciteEnvironment env) {
		this.env = env;
	}
	

	private DynamoDBMapperConfig configFor(Class<?> clazz) {
		return new DynamoDBMapperConfig(new TableNameOverride(tableName(clazz)));
	}
	
	private String tableName(Class<?> clazz) {
		DynamoDBTable annotation = clazz.getAnnotation(DynamoDBTable.class);
		Preconditions.checkNotNull(annotation,"missing annotation on " + clazz);
		String tableName = env.qualifyTableName(annotation.tableName());
		return tableName;
	}
	private String tableName(String tableName) {
		return env.qualifyTableName(tableName);
	}
	
	private void updateStateIndex(Ballot ballot, BallotState prev, BallotState curr) {
		Key prevIndex = new DynamoDbSecondaryIndex("Ballot", "State", prev.name(), ballot.getId()).getKey();
		Map<String, AttributeValue> currIndex = new DynamoDbSecondaryIndex("Ballot", "State", curr.name(), ballot.getId()).getMap();
		
		db.putItem(new PutItemRequest().withTableName(tableName("SecondaryIndex")).withItem(currIndex));
		if (curr != prev) {
			db.deleteItem(new DeleteItemRequest().withTableName(tableName("SecondaryIndex")).withKey(prevIndex));
		}
	}
	
	


	private <T> void in(DynamoDBScanExpression s, String attributeName, final Set<T> states,
			final Function<T, String> function) {
		if (states.isEmpty()) return;
		Iterable<AttributeValue> attrValues = Iterables.transform(states, new Function<T, AttributeValue>() {
			public AttributeValue apply(T input) {
				return new AttributeValue(function.apply(input));
			}
		});

		if (states.size() == 1) {
			s.addFilterCondition(attributeName, 
					new Condition()
						.withComparisonOperator(ComparisonOperator.EQ)
						.withAttributeValueList(Iterables.getFirst(attrValues,null)));
		}
		if (states.size() > 1) {
			AttributeValue[] array = Iterables.toArray(attrValues, AttributeValue.class);
			s.addFilterCondition(attributeName, 
					new Condition().withComparisonOperator(ComparisonOperator.IN).withAttributeValueList(array));
		}

	}

	public DynamoDbBallot createBallot() {
		return new DynamoDbBallot();
	}

	public Ballot save(Ballot ballot) {
		
		DynamoDBMapper mapper = new DynamoDBMapper(db,configFor(DynamoDbBallot.class));
		mapper.save(ballot);
		if (ballot.getOwner() != null) {
			DynamoDbUser owner = getUser(ballot.getOwner());
			owner.addBallotIOwn(ballot.getId());
			save(owner);
		}
		
		updateStateIndex(ballot, ballot.getState(), ballot.getState()); // TODO this is obviously not correct, it doesn't have the previous state
		return ballot;
	}

	/**
	 * @return the object or null
	 */
	public Ballot getBallot(String id) {
		Preconditions.checkArgument(id != null,"id is not null");
		DynamoDBMapper mapper = new DynamoDBMapper(db,configFor(DynamoDbBallot.class));
		return mapper.load(DynamoDbBallot.class, id);
	}
	
//	public void cancel(Ballot b) {
//		Preconditions.checkArgument(b != null, "ballot cannot be null");
//		DynamoDbBallot db = (DynamoDbBallot) b;
//		db.setState(BallotState.Cancelled);
//		save(db);
//	}


	public User save(User user) {
		DynamoDBMapper mapper = new DynamoDBMapper(db,configFor(DynamoDbUser.class));
		mapper.save(user);
		return user;
	}


	public DynamoDbUser getUser(String id) {
		Preconditions.checkArgument(id != null,"id is not null");
		DynamoDBMapper mapper = new DynamoDBMapper(db,configFor(DynamoDbUser.class));
		return mapper.load(DynamoDbUser.class, id);
	}


//	public void deactivate(User user) {
//		Preconditions.checkArgument(user != null, "ballot cannot be null");
//		DynamoDbUser dbu = (DynamoDbUser) user;
//		dbu.setUserStatus(UserStatus.Inactive);
//		save(user);
//	}
	
	public void delete(Ballot b) {
		Preconditions.checkArgument(b != null, "ballot cannot be null");
		new DynamoDBMapper(db).delete(b,configFor(DynamoDbBallot.class));
	}

	public void delete(User user) {
		Preconditions.checkArgument(user != null, "user cannot be null");
		new DynamoDBMapper(db).delete(user,configFor(DynamoDbUser.class));
	}



	public DynamoDbUser createUser(String emailAddress) {
		Preconditions.checkArgument(emailAddress != null,"email address is required");
		DynamoDbUser user = getUser(emailAddress);
		if (user == null) {
			user = new DynamoDbUser();
			user.setEmailAddress(emailAddress);
			save(user);
		}
		return user;
	}
	public void afterPropertiesSet() throws Exception {

		Assert.notNull(env,"env must be provided");
		Assert.notNull(db, "db has not been set");
	}
	
	public void users(UserCriteria criteria, Predicate<User> users) {
		Preconditions.checkArgument(criteria != null, "criteria must not be null");
		DynamoDBScanExpression scanexp = new DynamoDBScanExpression();
		PaginatedScanList<DynamoDbUser> list = new DynamoDBMapper(db,configFor(DynamoDbUser.class)).scan(
				DynamoDbUser.class, scanexp);
		applyWhileTrue(users, list);
	}

	
	public void ballots(BallotCriteria criteria, Predicate<Ballot> callback) {
		Preconditions.checkArgument(criteria != null, "criteria must not be null");
		DynamoDBScanExpression scanexp = new DynamoDBScanExpression();

				
		in(scanexp, "State", criteria.getStates(), new Function<BallotState,String>(){
			public String apply(BallotState input) {
				return input.name();
			}
		});
		

//		if (scanexp.getScanFilter() == null || scanexp.getScanFilter().isEmpty()) {
//			scanexp.addFilterCondition(
//					hashKeyAttributeName(DynamoDbUser.class), 
//					new Condition().withComparisonOperator(ComparisonOperator.NOT_NULL));
//		}

		
		PaginatedScanList<DynamoDbBallot> list = new DynamoDBMapper(db,configFor(DynamoDbBallot.class)).scan(
				DynamoDbBallot.class, scanexp);
		
		applyWhileTrue(callback, list);
	}
	
	
	
	public void users(Predicate<User> users) {
		DynamoDBScanExpression scanexp = new DynamoDBScanExpression();
		
//		scanexp.addFilterCondition(
//				hashKeyAttributeName(DynamoDbUser.class), 
//				new Condition().withComparisonOperator(ComparisonOperator.NOT_NULL));
		
		PaginatedScanList<DynamoDbUser> list = new DynamoDBMapper(db,configFor(DynamoDbUser.class)).scan(
				DynamoDbUser.class, scanexp);
		applyWhileTrue(users, list);
	}
	
	
	public void votes(Ballot ballot, Predicate<Vote> vote) {
		DynamoDBQueryExpression queryexp = new DynamoDBQueryExpression(new AttributeValue(ballot.getId()));
		PaginatedQueryList<DynamoDbVote> query = mapper(DynamoDbVote.class).query(DynamoDbVote.class, queryexp);
		applyWhileTrue(vote, query);
	}

	public void updateState(Ballot u, BallotState cancelled) {
		DynamoDbBallot b = (DynamoDbBallot) getBallot(u.getId());
		b.setState(cancelled);
		save(b);
	}
	
	public void updateState(User user, UserStatus inactive) {
		DynamoDbUser u = (DynamoDbUser) getUser(user.getEmailAddress());
		u.setUserStatus(inactive);
		save(u);
	}
	
	public void updatePassword(User user, String password) {
		DynamoDbUser u = (DynamoDbUser) getUser(user.getEmailAddress());
		try {
			u.setCredentials(hash(password));
			u.setRegistered(true);
		} catch (Exception e) { 
			throw Throwables.propagate(e);
		}
		save(u);

	}
	
	

	public boolean credentialsMatch(User user, String credentials) {
		DynamoDbUser u = getUser(user.getEmailAddress());
		try {
			return hash(credentials).equals(u.getCredentials());
		} catch (Exception e) { 
			throw Throwables.propagate(e);
		}

	}
	
	
	// TODO - move to pwd algorithm which should be pluggable via spring config
	static String hash(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException{
	    MessageDigest md = MessageDigest.getInstance("SHA-1"); 
	    return byteArray2Hex(md.digest(text.getBytes("UTF-8")));
	}

	private static String byteArray2Hex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    return formatter.toString();
	}
	
	
	private <A> void applyWhileTrue(Predicate<? super A> callback,
			List<A> list) {
		Iterables.tryFind(list, Predicates.not(callback));
	}

	private String hashKeyAttributeName(Class<DynamoDbUser> clazz) {
		List<Method> methods = Lists.newArrayList(clazz.getMethods());
		Iterable<DynamoDBHashKey> hks = Iterables.transform(methods, new Function<Method,DynamoDBHashKey>() {
			public DynamoDBHashKey apply(Method input) {
				return input.getAnnotation(DynamoDBHashKey.class);
			}});
		Optional<DynamoDBHashKey> hk = Iterables.tryFind(hks, Predicates.notNull());
		return hk.isPresent() ? hk.get().attributeName() : null;
	}
	
	public void votes(User forUser, Predicate<Vote> vote) {
		Preconditions.checkArgument(forUser != null,"forUser must not be null");
		
		final DynamoDbUser user = getUser(forUser.getEmailAddress());
		Preconditions.checkNotNull(user, "couldnt' get user");
		

		List<Key> newArrayList = Lists.newArrayList(Iterables.transform(user.getVotedOnBallots(), new Function<String,Key>() {
			public Key apply(String input) {
				return new Key().withHashKeyElement(new AttributeValue().withS(input)).withRangeKeyElement(new AttributeValue().withS(user.getEmailAddress()));
			}}));
		
		Map<String, KeysAndAttributes> requestItems = new HashMap<String, KeysAndAttributes>();
		requestItems.put(tableName(DynamoDbVote.class),
		        new KeysAndAttributes()
		            .withKeys(newArrayList));
		BatchGetItemRequest batchGetItemRequest = new BatchGetItemRequest().withRequestItems(requestItems);
		BatchGetItemResult result = db.batchGetItem(batchGetItemRequest);
		
		BatchResponse br = result.getResponses().get(tableName(DynamoDbVote.class));
		List<DynamoDbVote> votes = mapper(DynamoDbVote.class).marshallIntoObjects(DynamoDbVote.class, br.getItems());
		applyWhileTrue(vote, votes);
	}
	public void delete(Vote vote) {
		mapper(DynamoDbVote.class).delete(vote);
	}
	public Vote getVote(Ballot ballot, User user) {
		DynamoDBMapper m = mapper(DynamoDbVote.class);
		DynamoDbVote vote = m.load(DynamoDbVote.class, ballot.getId(), user.getEmailAddress());
		return vote;
	}
	
	public Vote createVote(Ballot ballot, User user) {
		DynamoDbVote v = new DynamoDbVote();
		v.setBallotId(ballot.getId());
		v.setUserId(user.getEmailAddress());
		return v;
	}
	
	public Vote save(Vote vote) {
		mapper(DynamoDbVote.class).save(vote);
		DynamoDbUser uz = getUser(vote.getUserId());
		uz.addVotedOnBallot(vote.getBallotId());
		save(uz);
		return vote;
	}
	private DynamoDBMapper mapper(Class<?> clazz) {
		return new DynamoDBMapper(db, configFor(clazz));
	}
	
	public void markEmailVerified(User user) {
		Preconditions.checkArgument(user != null,"user cannot be null");
		Preconditions.checkArgument(user.getEmailAddress() != null,"user cannot be null");
		
		DynamoDbUser u = getUser(user.getEmailAddress());
		Preconditions.checkNotNull(u);
		
		
		u.setEmailVerified(true);
		u.setVerificationToken(null);
		save(u);
	}
	
	
}
