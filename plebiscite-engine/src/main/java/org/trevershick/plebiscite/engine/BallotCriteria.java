package org.trevershick.plebiscite.engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.trevershick.plebiscite.model.BallotState;

public class BallotCriteria {
	private Set<BallotState> states = new HashSet<BallotState>();
	private Set<String> owners = new HashSet<String>();
	private Set<String> voters = new HashSet<String>();
	
	
	public BallotCriteria addState(BallotState state) {
		states.add(state);
		return this;
	}
	public Set<BallotState> getStates() {
		return Collections.unmodifiableSet(states);
	}
	public boolean hasState() {
		return states.size() == 1;
	}
	public boolean hasStates() {
		return states.size() > 1;
	}
	public Set<String> getOwners() {
		return Collections.unmodifiableSet(owners);
	}
	public boolean hasOwners() {
		return this.owners.size() > 1;
	}
	public boolean hasOwner(){ 
		return this.owners.size() == 1;
	}
	public BallotCriteria addOwner(String emailAddress) {
		this.owners.add(emailAddress);
		return this;
	}
	
	
	public Set<String> getVoters() {
		return Collections.unmodifiableSet(voters);
	}
	public boolean hasVoters() {
		return this.voters.size() > 1;
	}
	public boolean hasVoter(){ 
		return this.voters.size() == 1;
	}
	public BallotCriteria addVoter(String emailAddress) {
		this.voters.add(emailAddress);
		return this;
	}

}
