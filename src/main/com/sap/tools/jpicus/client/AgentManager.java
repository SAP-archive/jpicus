package com.sap.tools.jpicus.client;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Used by remote clients for communication to the agent.
 * @author pavel
 *
 */
public interface AgentManager extends Remote {
	
		
	/**
	 * 
	 * @return a snapshot of the current operations state
	 */
	byte [] createSnapshot() throws RemoteException;
	
	/**
	 * Forcibly close the handle with the given id
	 * @param id
	 */
	void closeHandle(long id) throws RemoteException;
	
	/**
	 * 
	 * @return the current time on the target system. This is
	 * needed because lots of objects returned by the agent
	 * have timestamps and the time at the system being analyzed
	 * might be different than the time on the client system
	 */
	long currentTimeMillis() throws RemoteException;
	
	
	/**
	 * @return the current agent options
	 */
	AgentOptions getOptions() throws RemoteException;

	
	/**
	 * The agent can optionally perform some calibration on startup in order to
	 * find out the current parameters of the underlying file system and hardware.
	 * Only the partition of the user.dir (current working dir) is used during the calibration
	 * 
	 * The calibration might not be very accurate if there are other processes, such as
	 * anti virus software etc, that are using the filesystem as well, but shall still reflect
	 * the current working environment of the process being profiled
	 * 
	 * @return the calibration data or null if calibration is not enabled
	 * @throws RemoteException
	 */
	CalibrationData getCalibrationData() throws RemoteException;

	/**
	 * Initiate calibration
	 */
	void calibrate () throws RemoteException;

	/**
	 * 
	 * @return a thread dump of the target process
	 */
	ThreadInfo getThreadInfo(long id) throws RemoteException;

	
}
