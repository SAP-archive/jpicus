package com.sap.tools.jpicus.client;

import java.rmi.RemoteException;


public interface Connection {
	
	String getHost();
	
	int getPort();
	
	/**
	 * Create a snapshot of the current agent state
	 * @return
	 * @throws RemoteException
	 */
	Snapshot getSnapshot() throws RemoteException;
	
	/**
	 * 
	 * @return the current time in milliseconds taken from the target host
	 */
	long currentTimeMillis() throws RemoteException;
	
	AgentOptions getOptions() throws RemoteException;
	
	/**
	 * Try to release any resources that this connection might be holding
	 */
	void close();
	
	void closeHandle(long id) throws RemoteException;
	
	/**
	 * Could be used to see if a thread that opened a file is still running.
	 * @return a thread info of a thread with the specified ID or null if the thread is not
	 * alive anymore. 
	 * NOTE: thread IDs might be reused so this method is not guaranteed to return 100% accurate results
	 * but it should be accurate to a very large degree (e.g. until you create some millions of threads,
	 * so that the IDs have to be reused.
	 * NOTE: If there is thread pooling in place a thread might be still alive but might have
	 * finished its task so that the file will probably remain open. Some heuristics could be used
	 *  in order to compare the names and stack traces of the thread at the point in time when it opened the file 
	 *  and the current shapshot to figure out if there is a chance for this file to be closed.
	 *  This could be used as a hint to the user in an environment with many threads.
	 */
	ThreadInfo getThreadInfo(long threadId) throws RemoteException;
	

}
