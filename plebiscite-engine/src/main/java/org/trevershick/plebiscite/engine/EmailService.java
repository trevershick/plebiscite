package org.trevershick.plebiscite.engine;

public interface EmailService {

	void sendEmail(String to, String subject, String body);

}
