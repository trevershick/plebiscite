package org.trevershick.plebiscite.engine.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.Key;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
/**
 * The secondary index object represents a secondary index within DynamoDb. Since DynamoDb doesn't support
 * secondary indexes one has to maintain their own indexes, which is what this is for.
 * 
 * Values are stored in DynamoDb as "Entity#Column#Value -> ReferenceId".  The entire thing is a composite key.
 *   
 * The first value "Entity#Column#Value" is a Hash Key and the value is a Range key.  Iterating over range keys
 * based on a hash key is a 'cheap' operation in DynamoDb so it was implemented in this way.
 * 
 * @author trevershick
 */
@DynamoDBTable(tableName="SecondaryIndex")
public class DynamoDbSecondaryIndex {
	/**
	 * The entity this index is on, like "ballot"
	 */
	private String entity;
	/**
	 * The column this index is for like 'state'
	 */
	private String column;
	/**
	 * The value of the 'column' on the given 'entity'
	 */
	private String value;
	/**
	 * the id of the referenced object.
	 */
	private String refId;

	public DynamoDbSecondaryIndex() {}
	public DynamoDbSecondaryIndex(String entity, String column, String value, String refId) {
		this.entity = entity;
		this.column = column;
		this.value = value;
		this.refId = refId;
	}

	public Key getKey() {
		return new Key(new AttributeValue().withS(getIndexValue()), new AttributeValue().withS(getRefId()));
	}
	
	@DynamoDBHashKey(attributeName = "IndexValue")
	public String getIndexValue() {
		return Joiner.on('#').join(entity,column,value).toString();
	}
	public void setIndexValue(String value){
		Iterable<String> split = Splitter.on('#').split(value);
		Iterator<String> iterator = split.iterator();
		entity = iterator.next();
		column = iterator.next();
		value = iterator.next();
	}


    @DynamoDBRangeKey(attributeName = "IndexRef")
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}

	public Map<String,AttributeValue> getMap() {
		HashMap<String, AttributeValue> newHashMap = Maps.newHashMap();
		newHashMap.put("IndexValue", new AttributeValue().withS(getIndexValue()));
		newHashMap.put("IndexRef", new AttributeValue().withS(getRefId()));
		return newHashMap;
	}
}
