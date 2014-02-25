package com.sap.tools.jpicus.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

public interface Snapshot extends Serializable {
	
	
	/**
	 * When the snapshot has been created
	 * @return
	 */
	long getTimestamp();
	
	
	/**
	 * 
	 * @return a map with all known file handles. Depending on the
	 * agent options map may contain open as well as closed handles.
	 * The keys of the map are canonical paths to files. The values
	 * are sets with one or more handles associated with this file
	 * i.e. FileInputStream and ZipFile
	 */
	Map<String, Set<HandleDescriptor>> getHandles();

	
	/**
	 * 
	 * @return all the delete operations that took place in the
	 * virtual machine after the agent has been initialized.
	 * 
	 * May return empty result if this option is not enabled
	 */
	Map<String, Set<DeleteOperation>> getDeleteOperations();
	
	
	Map<String, Set<ListOperation>> getListOperations();
		

	/**
	 * 
	 * @return the maximum simultaneous number of open files that has been reached
	 * after the JVM was started
	 * (not counting the files that have been opened before the agent initialization)
	 */
	int getPeakHandleCount();
	
	/**
	 * 
	 * @return a timestamp of the time when the current peak handle count was reached
	 * @throws RemoteException
	 */
	long getPeakHandleTime();
	

}
