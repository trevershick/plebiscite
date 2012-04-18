package org.trevershick.plebiscite.model;

public interface User {
	String getEmailAddress();
	boolean isRegistered();
	String getSlug();
	UserStatus getUserStatus();
	boolean isServicesEnabled();
}
