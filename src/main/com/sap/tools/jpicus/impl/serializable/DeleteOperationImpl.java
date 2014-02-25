package com.sap.tools.jpicus.impl.serializable;

import java.util.Collections;
import java.util.Set;

import com.sap.tools.jpicus.client.DeleteOperation;
import com.sap.tools.jpicus.client.HandleDescriptor;
import com.sap.tools.jpicus.client.ThreadInfo;

public class DeleteOperationImpl implements DeleteOperation{

	
	private static final long serialVersionUID = 1L;
	
	private boolean successful;
	private Set<HandleDescriptor> associatedHandleDescriptors = Collections.emptySet();
	private ThreadInfo attemptingThreadInfo;
	private String description;
	
	public DeleteOperationImpl(boolean successful, String description){
		
		this.attemptingThreadInfo = new ThreadInfoImpl(5);
		this.successful = successful;
		
	}
	
	public void setDescriptors(Set<HandleDescriptor> associatedDescriptors){
		   
			this.associatedHandleDescriptors = associatedDescriptors;
	}

	
	public boolean isSuccessful() {
		
		return this.successful;
	}

	
	public Set<HandleDescriptor> getAssociatedHandleDescriptors() {
		
		return this.associatedHandleDescriptors;
	}

	
	public ThreadInfo getAttemptingThreadInfo() {
		
		return this.attemptingThreadInfo;
	}
	
	
	public int hashCode() {
		
		return this.attemptingThreadInfo.hashCode();
	}

	
	public String getDescription() {

		return this.description;
	}

	
	public String toString() {
		
		StringBuilder buf = new StringBuilder(200);
		
		buf.append("successful:").append(successful)
		   .append(", description:").append(description);
		buf.append(this.attemptingThreadInfo);
		
		buf.append(", associated handles: ").append(this.associatedHandleDescriptors);
		
		return buf.toString();
	}
	
}
