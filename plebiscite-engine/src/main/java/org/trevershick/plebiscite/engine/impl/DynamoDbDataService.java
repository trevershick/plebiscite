package org.trevershick.plebiscite.engine.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.trevershick.plebiscite.model.VoteType;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.BatchGetItemRequest;
import com.amazonaws.services.dynamodb.model.BatchGetItemResult;
import com.amazonaws.services.dynamodb.model.BatchResponse;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.amazonaws.services.dynamodb.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.DeleteItemResult;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
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
import com.google.common.collect.Iterators;
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
		return new DynamoDBMapperConfig(SaveBehavior.UPDATE, ConsistentReads.CONSISTENT, new TableNameOverride(tableName(clazz)));
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
	
	private void updateStateIndex(Ballot ballot, BallotState newState) {
		
		boolean existing = db.getItem(new GetItemRequest(
				tableName(DynamoDbSecondaryIndex.class), 
				new DynamoDbSecondaryIndex("Ballot", "State", newState.name(), ballot.getId()).getKey())
		).getItem() != null;
		
		if (existing) {
			return;
		}
		for (BallotState bs : BallotState.values()) {
			if (bs == ballot.getState()) continue;
			Key index = new DynamoDbSecondaryIndex("Ballot", "State", bs.name(), ballot.getId()).getKey();
			DeleteItemResult r = db.deleteItem(new DeleteItemRequest().withTableName(tableName("SecondaryIndex")).withKey(index));
		}

		Map<String, AttributeValue> currIndex = new DynamoDbSecondaryIndex("Ballot", "State", ballot.getState().name(), ballot.getId()).getMap();
		db.putItem(new PutItemRequest().withTableName(tableName("SecondaryIndex")).withItem(currIndex));
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
		this.updateStateIndex(ballot, ballot.getState());
		
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


	public DynamoDbUser save(User user) {
		DynamoDbUser u = (DynamoDbUser) user;
		DynamoDBMapper mapper = new DynamoDBMapper(db,configFor(DynamoDbUser.class));
		mapper.save(user);
		return u;
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

	
	public void ballots(final BallotCriteria criteria, Predicate<Ballot> cb) {
		Preconditions.checkArgument(criteria != null, "criteria must not be null");
		DynamoDBScanExpression scanexp = new DynamoDBScanExpression();

		List<Predicate<Ballot>> filters = new ArrayList<Predicate<Ballot>>();
		
		if (criteria.hasOpenBallots()) {
			filters.add(new Predicate<Ballot>(){
				@Override
				public boolean apply(Ballot input) {
					return input.isOpenBallot() == criteria.getOpenBallots();
				}});
		}
		
		final Predicate<Ballot> filteredCallback = filters.isEmpty() ? cb : Predicates.and(Predicates.and(filters), cb);
		
		
		if (criteria.getStates().size() > 0) {
			Set<Ballot> ballotset = new TreeSet<Ballot>();
			for (BallotState bs : criteria.getStates()) {
				DynamoDBQueryExpression query = new DynamoDBQueryExpression(new AttributeValue().withS("Ballot#State#" + bs.name()));
				PaginatedQueryList<DynamoDbSecondaryIndex> results = mapper(DynamoDbSecondaryIndex.class).query(DynamoDbSecondaryIndex.class, query);

				Iterator<Ballot> ballots = Iterators.transform(results.iterator(), new Function<DynamoDbSecondaryIndex, Ballot>() {
					@Override
					public Ballot apply(DynamoDbSecondaryIndex input) {
						return getBallot(input.getRefId());
					}
				});
				Iterator<Ballot> filtered = Iterators.filter(ballots, Predicates.notNull());
				while (filtered.hasNext()) {
					ballotset.add(filtered.next());
				}
				
			}
			for (Ballot ballot : ballotset) {
				filteredCallback.apply(ballot);
			}
			return;
		}
//		in(scanexp, "State", criteria.getStates(), new Function<BallotState,String>(){
//			public String apply(BallotState input) {
//				return input.name();
//			}
//		});
		
		
		if (criteria.getOwners().size() > 0) {
			for (String owner : criteria.getOwners()) {
				DynamoDbUser o = getUser(owner);
				if (o.getBallotsIOwn() == null) continue;

				Iterables.tryFind(
					Iterables.filter(o.getBallotsIOwn(),Predicates.notNull()), 
					new Predicate<String>() {
						@Override
						public boolean apply(String ballotId) {
							Ballot b = getBallot(ballotId);
							if (b != null) {
								return !filteredCallback.apply(b);
							} else {
								return false; // return false to keep iterating ddue to 'tryFind's usage
							}
						}}); 
			}
			return;
		}

//		if (scanexp.getScanFilter() == null || scanexp.getScanFilter().isEmpty()) {
//			scanexp.addFilterCondition(
//					hashKeyAttributeName(DynamoDbUser.class), 
//					new Condition().withComparisonOperator(ComparisonOperator.NOT_NULL));
//		}

		
		PaginatedScanList<DynamoDbBallot> list = new DynamoDBMapper(db,configFor(DynamoDbBallot.class)).scan(
				DynamoDbBallot.class, scanexp);
		
		applyWhileTrue(filteredCallback, list);
	}
	
	
	
	public void users(Predicate<User> users) {
		DynamoDBScanExpression scanexp = new DynamoDBScanExpression();
		
		PaginatedScanList<DynamoDbUser> list = new DynamoDBMapper(db,configFor(DynamoDbUser.class)).scan(
				DynamoDbUser.class, scanexp);
		applyWhileTrue(users, list);
	}
	
	
	public void votes(Ballot ballot, Predicate<Vote> vote) {
		DynamoDBQueryExpression queryexp = new DynamoDBQueryExpression(new AttributeValue(ballot.getId()));
		PaginatedQueryList<DynamoDbVote> query = mapper(DynamoDbVote.class).query(DynamoDbVote.class, queryexp);
		applyWhileTrue(vote, query);
	}

	public void updateState(Ballot b, BallotState st) {
		DynamoDbBallot ballot = (DynamoDbBallot) getBallot(b.getId());
		ballot.setState(st);
		save(ballot);
		((DynamoDbBallot)b).setState(st);
		updateStateIndex(b, st);
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
	
	

	public boolean credentialsMatch(String emailAddress, String credentials) {
		DynamoDbUser u = getUser(emailAddress);
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
			Iterable<A> list) {
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
	
	public Vote createVote(Ballot ballot, User user, VoteType vote) {
		try {
			DynamoDbVote v = new DynamoDbVote();
			v.setBallotId(ballot.getId());
			v.setUserId(user.getEmailAddress());
			v.setType(vote);
			v.setWhen(new Date());
			return v;
		} catch (ConditionalCheckFailedException c){
			return null;
		}
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
