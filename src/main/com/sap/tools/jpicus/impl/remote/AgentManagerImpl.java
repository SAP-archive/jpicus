package com.sap.tools.jpicus.impl.remote;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.RemoteException;

import com.sap.tools.jpicus.OperationsState;
import com.sap.tools.jpicus.client.AgentManager;
import com.sap.tools.jpicus.client.AgentOptions;
import com.sap.tools.jpicus.client.CalibrationData;
import com.sap.tools.jpicus.client.ThreadInfo;
import com.sap.tools.jpicus.impl.serializable.Options;
import com.sap.tools.jpicus.impl.serializable.ThreadInfoImpl;

public class AgentManagerImpl implements AgentManager {

	private OperationsState state;
	private CalibrationData calibrationData;
	
	
	public AgentManagerImpl(OperationsState state) {

		this.state = state;
	
	}
	
	public long currentTimeMillis() {
		
		return System.currentTimeMillis();
	}

	
	
	public AgentOptions getOptions() throws RemoteException {
		return Options.getInstance();
	}
	


	public CalibrationData getCalibrationData() throws RemoteException {
		
		return calibrationData;
	}



	public byte[] createSnapshot() {
				
		try {
			return state.creteSnapshot();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}



	public void closeHandle(long id) throws RemoteException {
		state.closeHandle(id);
		
	}



	public void calibrate() {
		// TODO Auto-generated method stub
		
	}

	public void setCalibrationData(CalibrationData calibrationData) {
		this.calibrationData = calibrationData;
	}


	public ThreadInfo getThreadInfo(long id) {
		
		
		// the standard java java.lang.management.ThreadInfo can't be used directly
		// because it is not serializable
		java.lang.management.ThreadInfo info = ManagementFactory.getThreadMXBean().getThreadInfo(id);
	
		return new ThreadInfoImpl(info.getThreadName(),
										   	   info.getStackTrace(),
										   	   info.getThreadId(),
										   	   info.getThreadState().toString()
										   );
		
	}


}
