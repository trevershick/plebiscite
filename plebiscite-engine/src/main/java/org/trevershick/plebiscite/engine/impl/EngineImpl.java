package org.trevershick.plebiscite.engine.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.trevershick.plebiscite.engine.DataService;
import org.trevershick.plebiscite.engine.EmailService;
import org.trevershick.plebiscite.engine.Engine;
import org.trevershick.plebiscite.engine.QueueingService;

public class EngineImpl implements Engine, InitializingBean {
	DataService dataService;
	EmailService emailService;
	QueueingService queueingService;

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setQueueingService(QueueingService queueingService) {
		this.queueingService = queueingService;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(dataService, "dataService has not been set on "
				+ getClass().getSimpleName());
		Assert.notNull(queueingService, "queueingService has not been set on "
				+ getClass().getSimpleName());
		Assert.notNull(emailService, "emailService has not been set on "
				+ getClass().getSimpleName());
	}

}
