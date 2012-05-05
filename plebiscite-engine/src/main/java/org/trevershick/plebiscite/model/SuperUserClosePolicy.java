package org.trevershick.plebiscite.model;

import java.util.Collection;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class SuperUserClosePolicy extends BallotClosePolicy {

	private static final Predicate<Vote> ALWAYS_FALSE = Predicates.alwaysFalse();
	
	private Predicate<Vote> acceptOnYes = ALWAYS_FALSE;
	private String user;
	private Predicate<Vote> rejectOnNo = ALWAYS_FALSE;

	
	
	public SuperUserClosePolicy() {
		super(PRIORITY_NORMAL);
	}

	@Override
	public BallotState shouldClose(Ballot ballot, Collection<Vote> votes) {
		boolean any = Iterables.any(votes, acceptOnYes);
		if (any) {
			return BallotState.Accepted;
		}
		
		any = Iterables.any(votes, rejectOnNo);
		if (any) {
			return BallotState.Rejected;
		}
		
		return ballot.getState();
	}

	public void setAcceptOnYes(final boolean b) {
		this.acceptOnYes = b ? new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				return b && input.getUserId().equals(user) && input.getType().isYay();
			}
		} : ALWAYS_FALSE;
	}

	public void setUser(String string) {
		this.user = string;
	}

	public void setRejectOnNo(final boolean b) {
		
		this.rejectOnNo = b ? new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				return b && input.getUserId().equals(user) && input.getType().isNay();
			}
		} : ALWAYS_FALSE;
	}
	public boolean getRejectOnNo() {
		return rejectOnNo != ALWAYS_FALSE;
	}
	public boolean getAcceptOnYes() {
		return acceptOnYes != ALWAYS_FALSE;
	}
	public void fromJSONObject(JSONObject o) throws JSONException {
		super.fromJSONObject(o);
		setRejectOnNo(o.getBoolean("ron"));
		setAcceptOnYes(o.getBoolean("aoy"));
		setUser(o.getString("u"));
	}
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject o = super.toJSONObject();
		o.put("ron", getRejectOnNo());
		o.put("aoy", getAcceptOnYes());
		o.put("u", this.user);
		return o;
	}

	public SuperUserClosePolicy withAcceptOnYes(boolean b) {
		this.setAcceptOnYes(b);
		return this;
	}
	public SuperUserClosePolicy withRejectOnNo(boolean b) {
		this.setRejectOnNo(b);
		return this;
	}
	public SuperUserClosePolicy withUser(String s) {
		this.setUser(s);
		return this;
	}

	public String getUser() {
		return this.user;
	}
	

}
