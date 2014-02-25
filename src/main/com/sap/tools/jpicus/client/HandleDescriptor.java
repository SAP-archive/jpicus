package com.sap.tools.jpicus.client;

import java.io.Serializable;

/**
 * 
 * This interface represents a descriptor of an I/O handle.
 * Handles are java objects that cause allocation of native
 * OS resources, such as Linux file descriptors. There are
 * two major types of handles known to the developers. 
 * File handles and Sockets. An example for a file handle 
 * is a FileOutputStream. Once the developer creates an instance
 * of this object, this causes allocation of native OS resource
 * (i.e. the file is locked and other threads/processes might not
 * be able to open it as well). In order to release the OS resources
 * the developer has to explicitly close the handle. Failing to
 * do so results in handle leaks for undetermined period of time
 * (i.e. until the object is garbage collected and its finalizer closes
 * the native OS resources)
 * 
 * 
 * @author pavel
 *
 */
public interface HandleDescriptor extends Serializable {
	
	/**
	 * 
	 * @return a unique identifier of this handle
	 */
	long getId();
	
	/**
	 * 
	 * @return the fully qualified name of the handle class
	 * (helps identify the type of handle
	 */
	String getHandleClass();
	
	
	/**
	 * 
	 * @return information about the thread that opened the handle
	 */
	ThreadInfo getOpeningThreadInfo();

	/**
	 * 
	 * @return information about the thread that closed the handle or null 
	 * if the handle is not closed yet
	 */
	ThreadInfo getClosingThreadInfo();
	
	/**
	 * 
	 * @return a counter of the given type or null if 
	 * this option is not enabled or not applicable to
	 * the current handle type handle. The keys in the
	 * counter type, the value is the given counter or
	 * null if this measurement is not enabled/present
	 */
	Counter getCounter(CounterType type);
	
	/**
	 * 
	 * @return the max throughput that was achieved on this handle
	 * or 0 if there haven't been any I/O operations so far
	 */
	double getMaxThroughput();
	
	/**
	 * 
	 * @return the min throughput that was achieved on this handle
	 * or Double.MAX_VALUE if there haven't been any I/O operations so far
	 */
	double getMinThroughput();
	
	/**
	 * 
	 * @return true if the java.io methods were used for the read/write operations
	 */
	boolean isIO();

	/**
	 * 
	 * @return true if the java.nio methods were used for the read/write operations
	 */
	boolean isNIO();
	
	/**
	 * 
	 * @return true if the java.nio methods with direct buffers were used for the read/write operations
	 */
	boolean isNIODirect();
	
	/**
	 * 
	 * @return true if the java.nio methods with memory mapped I/O were used for the read/write operations
	 */
	boolean isNIOMemoryMapped();


	/**
	 * 
	 * @return true if the java.nio methods with direct transfer were used  for read/write operations
	 */
	boolean isNioDirectTransfer();

}
