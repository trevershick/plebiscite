package org.trevershick.plebiscite.model;

public interface User {
	String getEmailAddress();
	
	boolean isAdmin();
	void setAdmin(boolean b);
	
	boolean canCreateBallot();
	
	boolean isRegistered();
	void setRegistered(boolean registered);
	
	String getSlug();
	void setSlug(String slug);
	
	UserStatus getUserStatus();
	
	boolean isServicesEnabled();
	void setServicesEnabled(boolean b);
	
	
}
