package org.trevershick.plebiscite.engine.impl;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

public class AWSTest {
	protected static AmazonDynamoDBClient client;
	protected static PlebisciteEnvironment env;
	protected static AmazonSimpleEmailServiceClient ses;

	
	
	@BeforeClass
	public static void setup() {
		String accessKey = System.getProperty("AWS_ACCESS_KEY_ID");
		String secretKey = System.getProperty("AWS_SECRET_KEY");
		assertTrue("Got the access key", accessKey != null && accessKey.trim().length() > 0);
		assertTrue("Got the secret key", secretKey != null && secretKey.trim().length() > 0);
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		env = new PlebisciteEnvironment();
//		env.setQualifier("FUNC");
		
		client = new AmazonDynamoDBClient(awsCredentials);
		ses = new AmazonSimpleEmailServiceClient(awsCredentials);
	}

}
