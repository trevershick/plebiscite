package org.trevershick.plebiscite.model;

public interface Vote {
	VoteType getType();
	String getComments();
	String getBallotId();
	String getUserId();
	boolean isRequired();
}
