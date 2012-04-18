package org.trevershick.plebiscite.model;

import org.trevershick.plebiscite.engine.State;


public interface Ballot {
    String getTitle();
    void setTitle(String title);

    public String getDescription();
	public void setDescription(String description);
	
	public State getState();
	

	
}
