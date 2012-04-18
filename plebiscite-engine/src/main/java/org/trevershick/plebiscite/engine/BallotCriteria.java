package org.trevershick.plebiscite.engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BallotCriteria {
	private Set<State> states = new HashSet<State>();
	
	
	public BallotCriteria addState(State state) {
		states.add(state);
		return this;
	}
	public Set<State> getStates() {
		return Collections.unmodifiableSet(states);
	}
	public boolean hasState() {
		return states.size() == 1;
	}
	public boolean hasStates() {
		return states.size() > 1;
	}
}
