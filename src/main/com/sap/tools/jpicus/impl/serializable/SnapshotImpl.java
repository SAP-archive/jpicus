package com.sap.tools.jpicus.impl.serializable;

import java.util.Map;
import java.util.Set;

import com.sap.tools.jpicus.OperationsState;
import com.sap.tools.jpicus.client.DeleteOperation;
import com.sap.tools.jpicus.client.HandleDescriptor;
import com.sap.tools.jpicus.client.ListOperation;
import com.sap.tools.jpicus.client.Snapshot;

public class SnapshotImpl implements Snapshot{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long timestamp;
	private OperationsState operationsState;
	

	public SnapshotImpl(long timestamp, OperationsState state) {
		this.timestamp = timestamp;
		this.operationsState = state;
	}	
	
	
	@SuppressWarnings("unchecked")
	
	public Map<String, Set<HandleDescriptor>> getHandles() {
		
		// TODO learn more about generics :)
		return (Map) operationsState.getHandles();
	
	}


	
	@SuppressWarnings("unchecked")
	
	public Map<String, Set<DeleteOperation>> getDeleteOperations() {
		
		return (Map)operationsState.getDeleteOperations();
	}

	
	
	public int getPeakHandleCount() {
		return operationsState.getPeakHandleCount();
	}
	
	

	
	public long getPeakHandleTime() {
		
		return operationsState.getPeakHandleCountTime();
	}


	
	public long getTimestamp() {
		return this.timestamp;
	}


	
	public Map<String, Set<ListOperation>> getListOperations() {
		
		return operationsState.getListIoperations();
	}
	
	

}
