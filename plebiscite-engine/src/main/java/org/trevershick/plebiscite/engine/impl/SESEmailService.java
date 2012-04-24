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

public class SESEmailService implements EmailService, InitializingBean {

	AmazonSimpleEmailServiceClient client;
	public void sendEmail(String to, String subject, String body) {
		SendEmailRequest r = new SendEmailRequest()
			.withSource("trevershick@yahoo.com")
			.withDestination(new Destination().withToAddresses(to))
			.withMessage(new Message().withSubject(new Content(subject)).withBody(new Body(new Content(body))));
		
		client.sendEmail(r);
	}
	
	public void setClient(AmazonSimpleEmailServiceClient client) {
		this.client = client;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(client,"client has not been set on " + getClass().getSimpleName());
	}

}
