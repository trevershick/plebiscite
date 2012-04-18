package org.trevershick.plebiscite.engine.impl;

import java.util.Collection;

import org.trevershick.plebiscite.model.BallotClosePolicy;

import com.amazonaws.services.dynamodb.datamodeling.JsonMarshaller;

public class PolicyMarshaller extends JsonMarshaller<Collection<BallotClosePolicy>> {

	@Override
	public String marshall(Collection<BallotClosePolicy> obj) {
		String marshall = super.marshall(obj);
		System.out.println(marshall);
		
		
		
		return marshall;
	}

	@Override
	public Collection<BallotClosePolicy> unmarshall(
			Class<Collection<BallotClosePolicy>> clazz, String obj) {

		Collection<BallotClosePolicy> unmarshall = super.unmarshall(clazz, obj);
		return unmarshall;
	}

}
