package org.trevershick.plebiscite.engine.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.trevershick.plebiscite.model.BallotClosePolicy;
import org.trevershick.plebiscite.model.QuorumClosePolicy;
import org.trevershick.plebiscite.model.SuperUserClosePolicy;
import org.trevershick.plebiscite.model.TimeoutPolicy;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshaller;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.google.common.base.Throwables;
/**
 * Complex marshaler for DynamoDb that marshals out a list of {@link BallotClosePolicy} objects.
 *
 * @see DynamoDbBallot#getPolicies()
 * @author trevershick
 */
public class PoliciesMarshaller implements DynamoDBMarshaller<Collection<BallotClosePolicy>> {

	@Override
	public String marshall(Collection<BallotClosePolicy> result) {
		try {
			JSONArray jsonArray = new JSONArray();
			for (BallotClosePolicy p : result){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("_type", p.getClass().getSimpleName());
				jsonObject.put("_object", p.toJSONObject());
				jsonArray.put(jsonObject);
			}
			return jsonArray.toString();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public Collection<BallotClosePolicy> unmarshall(
			Class<Collection<BallotClosePolicy>> clazz, String value) {
		try {
			JSONArray array = new JSONArray(value);
			Collection<BallotClosePolicy> policies = new ArrayList<BallotClosePolicy>();
			for (int i=0;i < array.length(); i++) {
				JSONObject wrapper = array.getJSONObject(i);
				String type = wrapper.getString("_type");
				JSONObject wrapped = wrapper.getJSONObject("_object");
				BallotClosePolicy p = createInstanceFromName(type);
				p.fromJSONObject(wrapped);
				policies.add(p);
			}
			return policies;
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		
		
	}

	private BallotClosePolicy createInstanceFromName(String type) {
		if (QuorumClosePolicy.class.getSimpleName().equals(type)) {
			return new QuorumClosePolicy();
		} else if (TimeoutPolicy.class.getSimpleName().equals(type)) {
			return new TimeoutPolicy();
		} else if (SuperUserClosePolicy.class.getSimpleName().equals(type)) {
			return new SuperUserClosePolicy();
		} else {
			throw new UnsupportedOperationException();
		}
	}

}
