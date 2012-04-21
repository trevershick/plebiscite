package org.trevershick.plebiscite.model;

public interface Vote {
	VoteType getType();
	void setType(VoteType vote);
	String getComments();
	void setComments(String comments);
	String getBallotId();
	String getUserId();
	boolean isRequired();
	void setRequired(boolean b);
}
