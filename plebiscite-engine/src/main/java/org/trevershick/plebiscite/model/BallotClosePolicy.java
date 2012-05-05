package org.trevershick.plebiscite.model;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;


public abstract class BallotClosePolicy implements Comparable<BallotClosePolicy> {
	public static final int PRIORITY_HIGHEST = Integer.MIN_VALUE;
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_LOWEST = Integer.MAX_VALUE;
	
	int order = 0;
	// the id is 'scoped' within a ballot, so it can be just about anything. random should
	// suffice for uniqueness within a ballot
	long id = 0;
	private static final Random random = new SecureRandom();
	
	protected BallotClosePolicy(int priority) {
		id = random.nextLong();
		this.order = priority;
	}
	public int priority() {
		return order;
	}
	public void setId(long value) {
		this.id = value;
	}
	public long getId() {
		return this.id;
	}
	public abstract BallotState shouldClose(Ballot ballot,Collection<Vote> votes);
	public String getDescription() {
		return "No Description";
	}
	@Override
	public int compareTo(BallotClosePolicy o) {
		return new CompareToBuilder().append(this.order, o.order).toComparison();
	}
	public JSONObject toJSONObject() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("_id", this.id);
		o.put("_order", this.order);
		return o;
	}
	public void fromJSONObject(JSONObject o) throws JSONException {
		this.id = o.getLong("_id");
		this.order = o.getInt("_order");
	}
	
	
}
