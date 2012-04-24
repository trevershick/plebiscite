package org.trevershick.plebiscite.model;

public interface User {
	String getEmailAddress();
	
	boolean isAdmin();
	void setAdmin(boolean b);
	
	boolean canCreateBallot();
	
	/**
	 * A 'registered' user has a password
	 * @return
	 */
	boolean isRegistered();
	void setRegistered(boolean registered);
	
	String getSlug();
	void setSlug(String slug);
	
	UserStatus getUserStatus();
	
	boolean isServicesEnabled();
	void setServicesEnabled(boolean b);

	boolean isEmailVerified();
	boolean verificationTokenMatches(String verificationToken);
	public String getVerificationToken();

	/**
	 * Generate a token for email verification.
	 * @return
	 */
	String generateVerificationToken();
	/**
	 * generates a temporary password string - this does NOT mutate the object
	 *
	 * @return
	 */
	// TODO - move this to the controller...
	String generateTemporaryPassword();

	boolean hasPassword();
}
