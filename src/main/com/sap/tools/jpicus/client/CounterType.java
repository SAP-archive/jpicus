package com.sap.tools.jpicus.client;

import java.io.Serializable;

public enum CounterType implements Serializable {

	BYTES_READ, BYTES_WRITE,
	/**
	 * Given in nanoseconds
	 */
	TIME_READ, TIME_WRITE,
	
}
