package org.trevershick.plebiscite.engine.impl;

import java.util.logging.Logger;

import org.trevershick.plebiscite.model.VoteType;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshaller;

public class VoteTypeMarshaller implements DynamoDBMarshaller<VoteType> {

	Logger log = Logger.getLogger(VoteTypeMarshaller.class.getName());

	public String marshall(VoteType getterReturnResult) {
		return getterReturnResult.name();
	}

	public VoteType unmarshall(Class<VoteType> clazz, String obj) {
		if (obj == null) {
			return null;
		}
		return VoteType.valueOf(obj);
	}

}
