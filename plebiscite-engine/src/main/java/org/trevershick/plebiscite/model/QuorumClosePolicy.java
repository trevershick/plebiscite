package org.trevershick.plebiscite.model;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class QuorumClosePolicy extends BallotClosePolicy {

	int numberRequired = 1;
	boolean requiredVotersOnly = false;

	
	public QuorumClosePolicy() {
		super(PRIORITY_NORMAL);
	}

	public QuorumClosePolicy(
			int numberOfVotesRequired,
			boolean requiredVotersOnly) {
		this();
		setNumberRequired(numberOfVotesRequired);
		setRequiredVotersOnly(requiredVotersOnly);
	}

	public int getNumberRequired() {
		return numberRequired;
	}

	public void setNumberRequired(int numberRequired) {
		this.numberRequired = numberRequired;
	}
	public boolean isRequiredVotersOnly() {
		return requiredVotersOnly;
	}

	public void setRequiredVotersOnly(boolean requiredVotersOnly) {
		this.requiredVotersOnly = requiredVotersOnly;
	}

	@JsonIgnore
	public String getDescription() {
		return String.format("Will close when %d voters have voted",
				numberRequired);
	}

	@Override
	public BallotState shouldClose(Ballot ballot, Collection<Vote> votes) {
		// filter out required voters only
		Iterable<Vote> filtered = Iterables.filter(votes, new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				return requiredVotersOnly ? input.isRequired() : true;
			}
		});
		
		// filter out non voters, just yays and nays
		filtered = Iterables.filter(filtered, new Predicate<Vote>() {
			@Override
			public boolean apply(Vote input) {
				return input.getType().isNay() || input.getType().isYay();
			}});
		
		Multimap<VoteType, Vote> byVoteType = Multimaps.index(filtered, new Function<Vote, VoteType>() {
			@Override
			public VoteType apply(Vote input) {
				return input.getType();
			}
		});
		
		int yayCount = byVoteType.containsKey(VoteType.Yay) ? byVoteType.get(VoteType.Yay).size() : 0;
		int nayCount = byVoteType.containsKey(VoteType.Nay) ? byVoteType.get(VoteType.Nay).size() : 0;
		
		if (yayCount >= numberRequired) {
			return BallotState.Accepted;
		}
		if (nayCount >= numberRequired) {
			return BallotState.Rejected;
		}

		return ballot.getState();
	}

	public QuorumClosePolicy withNumberRequired(int i) {
		setNumberRequired(i);
		return this;
	}
	public QuorumClosePolicy withRequiredVotersOnly(boolean b) {
		setRequiredVotersOnly(b);
		return this;
	}

	
	public void fromJSONObject(JSONObject o) throws JSONException {
		super.fromJSONObject(o);
		this.numberRequired = o.getInt("nr");
		this.requiredVotersOnly = o.getBoolean("rv");
	}
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject o = super.toJSONObject();
		o.put("nr", this.numberRequired);
		o.put("rv", this.requiredVotersOnly);
		return o;
	}


}
