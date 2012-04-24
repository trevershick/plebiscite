package org.trevershick.plebiscite.engine.impl;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.trevershick.plebiscite.model.Ballot;
import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBVersionAttribute;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
@DynamoDBTable(tableName="Users")
public class DynamoDbUser implements User {
	String emailAddress;
	String credentials;
	boolean registered;
	String slug;
	boolean emailVerified;
	String verificationToken;
	UserStatus userStatus = UserStatus.Active;
	boolean servicesEnabled;
	private Integer version;
	private boolean admin;
	private Set<String> ballotsIOwn = new HashSet<String>();
	private Set<String> votedOnBallots = new HashSet<String>();
	
	
    @DynamoDBVersionAttribute(attributeName="Version")
    public Integer getVersion() {
    	return this.version;
    }
    public void setVersion(Integer value) {
    	this.version = value;
    }

	
	@DynamoDBHashKey(attributeName="Id")
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String value) {
		Preconditions.checkState(emailAddress == null,"you cannot change an email address on a user");
		this.emailAddress = value;
	}

	
	
	@DynamoDBAttribute(attributeName="slug")
	public String getSlug() {
		return slug;
	}
	public void setSlug(String s) {
		this.slug = s;
	}
	
	
	@DynamoDBAttribute(attributeName="status")
	public String getUserStatusString() {
		return this.userStatus.name();
	}
	public void setUserStatusString(String s) {
		try {
			this.userStatus = UserStatus.valueOf(s);
		} catch (Exception e){ 
			this.userStatus = UserStatus.Active;
		}
	}
	
	
	
	@DynamoDBMarshalling(marshallerClass=YNMarshaller.class)
	@DynamoDBAttribute(attributeName="Registered")
	public boolean isRegistered() {
		return registered;
	}
	public void setRegistered(boolean value) {
		this.registered = value;
		if (this.registered) {
			this.verificationToken = null;
		}
	}

	@DynamoDBIgnore
	public UserStatus getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(UserStatus us) {
		Preconditions.checkArgument(us != null, "user status cannot be null");
		this.userStatus = us;
	}

	@DynamoDBMarshalling(marshallerClass=YNMarshaller.class)
	@DynamoDBAttribute(attributeName="ServicesEnabled")
	public boolean isServicesEnabled() {
		return servicesEnabled;
	}
	public void setServicesEnabled(boolean value) {
		this.servicesEnabled = value;
	}
	
	@DynamoDBMarshalling(marshallerClass=YNMarshaller.class)
	@DynamoDBAttribute(attributeName="AdminUser")
	public boolean isAdmin() {
		return admin;
	}
	
	@DynamoDBAttribute(attributeName="Credentials")
	public String getCredentials() {
		return credentials;
	}
	
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	
	@DynamoDBAttribute(attributeName="EmailVerificationToken")
	public String getVerificationToken() {
		return verificationToken;
	}
	public void setVerificationToken(String verificationToken) {
		this.verificationToken = verificationToken;
	}
	
	
	
	public void setAdmin(boolean value) {
		this.admin = value;
	}
	public boolean canCreateBallot() {
		return isAdmin() || isRegistered();
	}
	
	@DynamoDBAttribute(attributeName="VotedOn")
	public Set<String> getVotedOnBallots() {
		return votedOnBallots.size() == 0 ? null : new HashSet<String>(votedOnBallots);
	}
	public void setVotedOnBallots(Set<String> votedOnBallots) {
		this.votedOnBallots.clear();
		this.votedOnBallots.addAll(votedOnBallots);
	}

	@DynamoDBAttribute(attributeName="BallotsIOwn")
	public Set<String> getBallotsIOwn() {
		return ballotsIOwn.size() == 0 ? null : new HashSet<String>(ballotsIOwn);
	}
	public void setBallotsIOwn(Set<String> bs) {
		this.ballotsIOwn.clear();
		this.ballotsIOwn.addAll(votedOnBallots);
	}
	public void addBallotIOwn(String ballotId) {
		this.ballotsIOwn.add(ballotId);
	}
	public void addVotedOnBallot(String ballotId) {
		this.votedOnBallots.add(ballotId);
	}
	public boolean verificationTokenMatches(String verificationToken) {
		return StringUtils.equals(verificationToken, this.verificationToken);
	}
	
	public String generateVerificationToken() {
		this.verificationToken = UUID.randomUUID().toString();
		return this.verificationToken;
	}
	
	public String generateTemporaryPassword() {
		SecureRandom r = new SecureRandom();
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<8;i++) {
			int c = (r.nextInt() % 25);
			sb.append((char) ('a' + c));
		}
		String p = sb.toString();
		return p;
	}
	
	@DynamoDBMarshalling(marshallerClass=YNMarshaller.class)
	@DynamoDBAttribute(attributeName="EmailVerified")
	public boolean isEmailVerified() {
		return emailVerified;
	}
	public void setEmailVerified(boolean b) {
		this.emailVerified = b;
	}
	public boolean hasPassword() {
		return StringUtils.isNotBlank(credentials);
	}
	
}
