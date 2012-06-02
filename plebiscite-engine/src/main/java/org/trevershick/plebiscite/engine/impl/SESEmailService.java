package org.trevershick.plebiscite.engine.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.trevershick.plebiscite.engine.EmailService;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
/**
 * Implementation of the {@link EmailService} interface. This implementation uses 
 * Amazon's SES to send email.  SES must be setup properly to use this.
 * 
 * @author trevershick
 *
 */
public class SESEmailService implements EmailService, InitializingBean {

	String fromEmail = "trevershick@yahoo.com";
	
	AmazonSimpleEmailServiceClient client;
	public void sendEmail(String to, String subject, String body) {
		SendEmailRequest r = new SendEmailRequest()
			.withSource(getFromEmail())
			.withDestination(new Destination().withToAddresses(to))
			.withMessage(new Message().withSubject(new Content(subject)).withBody(new Body(new Content(body))));
		
		client.sendEmail(r);
	}
	
	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public void setClient(AmazonSimpleEmailServiceClient client) {
		this.client = client;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(client,"client has not been set on " + getClass().getSimpleName());
	}

}
