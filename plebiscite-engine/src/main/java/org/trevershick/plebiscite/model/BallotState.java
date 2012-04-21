package org.trevershick.plebiscite.model;

public enum BallotState {
	Closed,Open,Accepted,Rejected,TimedOut,Cancelled;

	public boolean isOpenable() {
		return this == Closed;
	}
	public boolean isCancellable() {
		return this == Open || this == Closed;
	}
	public boolean isComplete() {
		return this != Closed && this != Open;
	}
}
