package org.trevershick.plebiscite.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

public class QuorumClosePolicy extends BallotClosePolicy {

	int numberRequired = 1;
	boolean requiredVotersOnly = false;

	public QuorumClosePolicy() {

	}

	public QuorumClosePolicy(
			int numberOfVotesRequired,
			boolean requiredVotersOnly) {
		
		setNumberRequired(numberOfVotesRequired);
		setRequiredVotersOnly(requiredVotersOnly);
	}

	@JsonProperty("nr")
	public int getNumberRequired() {
		return numberRequired;
	}

	public void setNumberRequired(int numberRequired) {
		this.numberRequired = numberRequired;
	}

	@JsonProperty("rv")
	public boolean isRequiredVotersOnly() {
		return requiredVotersOnly;
	}

	public void setRequiredVotersOnly(boolean requiredVotersOnly) {
		this.requiredVotersOnly = requiredVotersOnly;
	}

	public boolean shouldClose(Ballot ballot) {
		// TODO Auto-generated method stub
		return false;
	}

	@JsonIgnore
	public String getDescription() {
		return String.format("Will close when %d voters have voted",
				numberRequired);
	}

}
