package org.trevershick.plebiscite.model;

public enum VoteType {
	Yay,Nay,Abstain,None;
	
	public boolean isYay() {
		return this == Yay;
	}
	public boolean isNay() {
		return this == Nay;
	}
	public boolean isAbstain() {
		return this == Abstain;
	}
	public boolean isAVote(){
		return this != None;
	}
	public boolean isNone() {
		return this == None;
	}
}
