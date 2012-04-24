package org.trevershick.plebiscite.engine.impl;

import org.junit.Test;

public class EmailServiceTest extends AWSTest {

	@Test
	public void stupid_test_just_doesnt_fail() {
		SESEmailService s = new SESEmailService();
		s.setClient(ses);
		s.sendEmail("trevershick@gmail.com", 
				"Test EMail "+ System.currentTimeMillis(),
				"Body " + System.currentTimeMillis());
	}
}
