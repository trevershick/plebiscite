package org.trevershick.plebiscite.engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.trevershick.plebiscite.model.BallotState;

public class BallotCriteria {
	private Set<BallotState> states = new HashSet<BallotState>();
	
	
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
}
