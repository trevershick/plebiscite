package org.trevershick.plebiscite.engine.impl;



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
