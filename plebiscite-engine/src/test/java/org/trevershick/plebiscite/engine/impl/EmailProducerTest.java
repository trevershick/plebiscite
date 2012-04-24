package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EmailProducerTest {

	@Test
	public void test() throws Exception {
		EmailProducer p = new EmailProducer();
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("verifyLink", "http://verifyme.com/verify");
		params.put("loginLink", "http://verifyme.com/login");
		
		Map<String,String> vlp = new HashMap<String, String>();
		vlp.put("email", "em");
		vlp.put("token", "tk");
		
		params.put("verifyLinkParams", vlp);
		params.put("email", "trevershick@yahoo.com");
		params.put("token", "abc123");
		
		
		Map<String, String> m = p.buildMailMessage("emailverification", params);
		assertNotNull(m.get(EmailProducer.SUBJECT));
		assertNotNull(m.get(EmailProducer.BODY));
		String body = m.get(EmailProducer.BODY);
		
		assertTrue(body.contains("tk=abc123"));
		assertTrue(body.contains("em=trevershick@yahoo.com"));
		assertTrue(body.contains("http://verifyme.com/verify?"));
	}

}
