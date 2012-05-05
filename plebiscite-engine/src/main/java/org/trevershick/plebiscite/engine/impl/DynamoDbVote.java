package org.trevershick.plebiscite.engine.impl;

import java.util.Date;

import org.trevershick.plebiscite.model.Vote;
import org.trevershick.plebiscite.model.VoteType;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBVersionAttribute;

@DynamoDBTable(tableName="Votes")
public class DynamoDbVote implements Vote {
	
	
	private String ballotId;
	private String userId;
	private boolean required;
	private VoteType type = VoteType.None;
	private String comments;
	private Integer version;
	private Date when;
	
	@DynamoDBHashKey(attributeName = "BallotId")
	public String getBallotId() {
		return ballotId;
	}
	
	
    
    @DynamoDBVersionAttribute(attributeName="Version")
    public Integer getVersion() {
    	return this.version;
    }
    public void setVersion(Integer value) {
    	this.version = value;
    }

	@DynamoDBRangeKey(attributeName = "UserId")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@DynamoDBAttribute(attributeName="Required")
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@DynamoDBMarshalling(marshallerClass=VoteTypeMarshaller.class)
	@DynamoDBAttribute(attributeName="Vote")
	public VoteType getType() {
		return type;
	}

	public void setType(VoteType type) {
		this.type = type;
	}

	@DynamoDBAttribute(attributeName="Comments")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setBallotId(String ballotId) {
		this.ballotId = ballotId;
	}


    @DynamoDBMarshalling(marshallerClass=DateTimeMarshaller.class)
	@DynamoDBAttribute(attributeName="When")
	public Date getWhen() {
		return this.when;
	}
	public void setWhen(Date d) {
		this.when = d;
	}
	@Override
	public Date when() {
		return when;
	}



	public DynamoDbVote withType(VoteType yay) {
		setType(yay);
		return this;
	}



	public Vote withRequired(boolean b) {
		setRequired(b);
		return this;
	}
    
    
}
