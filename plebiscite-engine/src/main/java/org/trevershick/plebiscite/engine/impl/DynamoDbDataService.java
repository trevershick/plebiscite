package org.trevershick.plebiscite.engine.impl;

import java.util.NoSuchElementException;
import java.util.Set;

import org.trevershick.plebiscite.engine.BallotCriteria;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.engine.State;
import org.trevershick.plebiscite.model.Ballot;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.ComparisonOperator;
import com.amazonaws.services.dynamodb.model.Condition;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class DynamoDbDataService implements DataService {
	AmazonDynamoDB db;

	public void setDb(AmazonDynamoDB db) {
		this.db = db;
	}

	public void ballots(BallotCriteria criteria, Predicate<Ballot> callback) {
		Preconditions.checkArgument(criteria != null, "criteria must not be null");
		DynamoDBScanExpression scanexp = new DynamoDBScanExpression();

		
		in(scanexp, "State", criteria.getStates(), new Function<State,String>(){
			public String apply(State input) {
				return input.name();
			}
		});
		

		if (scanexp.getScanFilter() == null || scanexp.getScanFilter().isEmpty()) {
			scanexp.addFilterCondition("Id", new Condition()
			.withComparisonOperator(ComparisonOperator.NOT_NULL));
		}

		
		PaginatedScanList<DynamoDbBallot> list = new DynamoDBMapper(db).scan(
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

	public DynamoDbBallot create() {
		return new DynamoDbBallot();
	}

	public Ballot save(Ballot ballot) {
		DynamoDBMapper mapper = new DynamoDBMapper(db);
		mapper.save(ballot);
		return ballot;
	}

	/**
	 * @return the object or null
	 */
	public Ballot getBallot(String id) {
		Preconditions.checkArgument(id != null,"id is not null");
		DynamoDBMapper mapper = new DynamoDBMapper(db);
		return mapper.load(DynamoDbBallot.class, id);
	}
	
	public void delete(Ballot b) {
		Preconditions.checkArgument(b != null, "ballot cannot be null");
		new DynamoDBMapper(db).delete(b);
	}

}
