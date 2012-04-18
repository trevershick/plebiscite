package org.trevershick.plebiscite.model;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.CLASS,include=As.WRAPPER_OBJECT)
//@JsonTypeInfo(  
//	    use = Id.NAME,  
//	    include = As.PROPERTY,  
//	    property = "type")  
@JsonSubTypes({@Type(value = QuorumClosePolicy.class, name = "quorum"),
	@Type(value = SuperUserClosePolicy.class, name = "super")  })  
public abstract class BallotClosePolicy {
	public abstract boolean shouldClose(Ballot ballot);
	public abstract String getDescription();
}
