package com.sap.tools.jpicus.client;

import java.io.Serializable;

/**
 * 
 * This interface represents a snapshot of a living thread.
 * It contains information like the thread name and stack trace
 * as well as a timestamp of the time the snapshot was taken
 * 
 * @author pavel
 *
 */
public interface ThreadInfo extends Serializable{

	/**
	 * 
	 * @return the name of the thread
	 */
	String getName();
	
	/**
	 * 
	 * @return the priority of the thread
	 */
	int getPriority();
	
	/**
	 * 
	 * @return the stack trace of the thread
	 */
	StackTraceElement [] getStackTrace();
	
	
	/**
	 * 
	 * @return a timestamp from the time when the
	 * snapshot of the thread was taken
	 */
	long getTimestamp();
	

	/**
	 * 
	 * @return the string representation of the thread's context, like context class loader.
	 * This information could be useful in some cases like profiling Java EE servers
	 * because normally the context class loader is set to the one of the responsible
	 *  component/application
	 */
	String getContext();
	
	/**
	 * 
	 * @return true if the thread was a daemon thread
	 */
	boolean isDaemon();
	
	/**
	 * 
	 * @return the id of the thread
	 */
	long getId();
	
}
