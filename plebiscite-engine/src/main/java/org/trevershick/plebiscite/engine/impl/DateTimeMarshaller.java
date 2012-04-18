package org.trevershick.plebiscite.engine.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshaller;

public class DateTimeMarshaller implements DynamoDBMarshaller<Date> {
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	Logger log = Logger.getLogger(DateTimeMarshaller.class.getName());

	public String marshall(Date getterReturnResult) {
		return getterReturnResult == null ? null : new SimpleDateFormat(DATE_FORMAT).format(getterReturnResult);
	}

	public Date unmarshall(Class<Date> clazz, String obj) {
		if (obj == null) {
			return null;
		}
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse(obj);
		} catch (ParseException e) {
			log.warning(e.toString());
			return null;
		}
	}

}
