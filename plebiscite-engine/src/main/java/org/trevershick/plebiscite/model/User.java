package org.trevershick.plebiscite.model;

public interface User {
	String getEmailAddress();
	
	boolean isAdmin();
	boolean canCreateBallot();
	
	boolean isRegistered();
	String getSlug();
	UserStatus getUserStatus();
	boolean isServicesEnabled();
}
