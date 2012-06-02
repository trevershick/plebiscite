package org.trevershick.plebiscite.engine;

/**
 * Simple email service abstraction. This is used by the engine to send email. Implementors of this
 * interface needs to provide all the functionality defined in this interface (there's not much to implement)
 * @author trevershick
 */
public interface EmailService {
	void sendEmail(String to, String subject, String body);
}
