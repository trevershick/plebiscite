package org.trevershick.plebiscite.engine.impl;

import org.trevershick.plebiscite.model.User;
import org.trevershick.plebiscite.model.UserStatus;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBVersionAttribute;
import com.google.common.base.Preconditions;
@DynamoDBTable(tableName="Users")
public class DynamoDbUser implements User {
	String emailAddress;
	boolean isRegistered;
	String slug;
	UserStatus userStatus = UserStatus.Active;
	boolean servicesEnabled;
	private Integer version;
	
	
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

	@DynamoDBAttribute(attributeName="services")
	public String getServicesEnabled() {
		return String.valueOf(servicesEnabled);
	}
	public void setServicesEnabled(String value) {
		this.servicesEnabled = Boolean.valueOf(value);
	}
	

	@DynamoDBAttribute(attributeName="registered")
	public String getRegistered() {
		return String.valueOf(isRegistered);
	}
	public void setRegistered(String registered) {
		this.isRegistered = Boolean.valueOf(registered);
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
	
	
	
	@DynamoDBIgnore
	public boolean isRegistered() {
		return isRegistered;
	}
	public void setRegistered(boolean value) {
		this.isRegistered = value;
	}

	@DynamoDBIgnore
	public UserStatus getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(UserStatus us) {
		Preconditions.checkArgument(us != null, "user status cannot be null");
		this.userStatus = us;
	}

	@DynamoDBIgnore
	public boolean isServicesEnabled() {
		return servicesEnabled;
	}
	public void setServicesEnabled(boolean value) {
		this.servicesEnabled = value;
	}
}
