package com.sap.tools.jpicus.client;

public class ConnectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionException(String message){
		super(message);
		
	}
	
	public ConnectionException(String message, Throwable cause){
		super(message, cause);
	}
	
}
