package com.sap.tools.jpicus.impl.serializable;

import com.sap.tools.jpicus.client.CalibrationData;

public class CalibrationDataImpl implements CalibrationData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
//	private float throughput;
//	private int optimalBufferSize;

	private String description;

//	public CalibrationDataImpl(float throughput, int optimalBufferSize){
//		this.throughput = throughput;
//		this.optimalBufferSize = optimalBufferSize;
//	}
	
	public CalibrationDataImpl(String description){
		this.description = description;
	}
//	
//	@Override
//	public float getFileThroughput() {
//
//		return throughput;
//	}
//
//	@Override
//	public int getOptimalFileBufferSize() {
//
//		return optimalBufferSize;
//	}


	public String getDescription() {
		
		return this.description;
	}

}
