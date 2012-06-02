package org.trevershick.plebiscite.engine.impl;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshaller;
/**
 * Marshaler for dynamo that converts boolean values to 'Y' or 'N' flags.
 * @see DynamoDbBallot#isOpenBallot()
 * @author trevershick
 *
 */
public class YNMarshaller implements DynamoDBMarshaller<Boolean> {

	public String marshall(Boolean getterReturnResult) {
		return getterReturnResult ? "Y" :"N";
	}

	public Boolean unmarshall(Class<Boolean> clazz, String obj) {
		return "Y".equals(obj);
	}

}
