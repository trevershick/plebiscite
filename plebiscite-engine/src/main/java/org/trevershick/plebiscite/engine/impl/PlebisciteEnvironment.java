package org.trevershick.plebiscite.engine.impl;


/**
 * Used as a simple object that provides a logical environment name that can be used
 * to prefix tables or other string values with the logical name.  DynamoDb does not
 * have a sandbox environment so you must use different table names per environment.
 * 
 * @author trevershick
 */
public class PlebisciteEnvironment {
	enum Qualifier {
		DEV,FUNC,PROD
	}

	
	private Qualifier qualifier = Qualifier.DEV;
	
	public PlebisciteEnvironment() {
		setQualifier(System.getProperty("plebiscite.env",Qualifier.DEV.name()));
	}
	
	public void setQualifier(String value) {
		this.qualifier = Qualifier.valueOf(value);
	}
	
	public String qualifyTableName(String table) {
		return qualifyObjectName(table);
	}
	public String qualifyObjectName(String objName) {
		return qualifier == Qualifier.PROD ? objName : qualifier.name() + "_" + objName;
	}

}
