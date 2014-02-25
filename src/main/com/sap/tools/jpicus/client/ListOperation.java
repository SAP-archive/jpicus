package com.sap.tools.jpicus.client;

public interface ListOperation {
	
	int getLength();
	
	/**
	 * 
	 * @return the duration of the operation in nano seconds
	 */
	long getDuration();
	
	/**
	 * @return information about the thread attempting the operation
	 */
	ThreadInfo getThreadInfo();

}
