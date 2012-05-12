package org.trevershick.plebiscite.model;

import java.util.Collection;
import java.util.Date;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class TimeoutPolicy extends BallotClosePolicy {

	
	@Override
	public String getDescription() {
		return "Will set the ballot to " + stateOnTimeout.name() + " when the ballot expires";
	}
	private BallotState stateOnTimeout;
	public TimeoutPolicy() {
		super(PRIORITY_LOWEST);
	}
	@Override
	public BallotState shouldClose(Ballot ballot, Collection<Vote> votes) {
		if (ballot.getExpirationDate() == null) {
			// no effect
			return ballot.getState();
		}
		Date expirationDate = ballot.getExpirationDate();
		Date today = new Date();
		if (today.after(expirationDate)) {
			return stateOnTimeout;
		} else {
			return ballot.getState();
		}
	}
	public void setStateOnTimeout(BallotState accepted) {
		this.stateOnTimeout = accepted;
	}
	public TimeoutPolicy withStateOnTimeout(BallotState accepted) {
		this.setStateOnTimeout(accepted);
		return this;
	}
	public BallotState getStateOnTimeout() {
		return this.stateOnTimeout;
	}
	public void fromJSONObject(JSONObject o) throws JSONException {
		super.fromJSONObject(o);
		this.stateOnTimeout =BallotState.valueOf(o.getString("sot"));
	}
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject o = super.toJSONObject();
		o.put("sot", this.stateOnTimeout.name());
		return o;
	}


}
