package org.trevershick.plebiscite.engine.impl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.BallotState;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

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
		DynamoDBTable annotation = clazz.getAnnotation(DynamoDBTable.class);
		Preconditions.checkNotNull(annotation,"missing annotation on " + clazz);
		String tableName = annotation.tableName();
		return new DynamoDBMapperConfig(new TableNameOverride(env.qualifyTableName(tableName)));
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
	
	
	public void ballots(BallotCriteria criteria, Predicate<Ballot> callback) {
		Preconditions.checkArgument(criteria != null, "criteria must not be null");
		DynamoDBScanExpression scanexp = new DynamoDBScanExpression();

				
		in(scanexp, "State", criteria.getStates(), new Function<BallotState,String>(){
			public String apply(BallotState input) {
				return input.name();
			}
		});
		

		if (scanexp.getScanFilter() == null || scanexp.getScanFilter().isEmpty()) {
			scanexp.addFilterCondition("Id", new Condition()
			.withComparisonOperator(ComparisonOperator.NOT_NULL));
		}

		
		PaginatedScanList<DynamoDbBallot> list = new DynamoDBMapper(db,configFor(DynamoDbBallot.class)).scan(
				DynamoDbBallot.class, scanexp);
		
		try {
			Iterables.find(list, Predicates.not(callback));
		} catch (NoSuchElementException nsee) {
			// ignore, stupid AWS API
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
	
	public void cancel(Ballot b) {
		Preconditions.checkArgument(b != null, "ballot cannot be null");
		DynamoDbBallot db = (DynamoDbBallot) b;
		db.setState(BallotState.Cancelled);
		save(db);
	}


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




	public void deactivate(User user) {
		Preconditions.checkArgument(user != null, "ballot cannot be null");
		DynamoDbUser dbu = (DynamoDbUser) user;
		dbu.setUserStatus(UserStatus.Inactive);
		save(user);
	}
	public void delete(Ballot b) {
		Preconditions.checkArgument(b != null, "ballot cannot be null");
		new DynamoDBMapper(db).delete(b,configFor(DynamoDbBallot.class));
	}

	public void delete(User user) {
		Preconditions.checkArgument(user != null, "user cannot be null");
		new DynamoDBMapper(db).delete(user,configFor(DynamoDbUser.class));
	}



	public User createUser(String emailAddress) {
		Preconditions.checkArgument(emailAddress != null,"email address is required");
		DynamoDbUser user = getUser(emailAddress);
		if (user == null) {
			user = new DynamoDbUser();
			user.setEmailAddress(emailAddress);
		}
		return user;
	}
	public void afterPropertiesSet() throws Exception {

		Assert.notNull(env,"env must be provided");
		Assert.notNull(db, "db has not been set");
	}

}
