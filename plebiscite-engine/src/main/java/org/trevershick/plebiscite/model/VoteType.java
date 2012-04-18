package org.trevershick.plebiscite.model;

public enum VoteType {
	Yay,Nay,Abstain;
	
	public boolean isYay() {
		return this == Yay;
	}
	public boolean isNay() {
		return this == Nay;
	}
	public boolean isAbstain() {
		return this == Abstain;
	}
}
