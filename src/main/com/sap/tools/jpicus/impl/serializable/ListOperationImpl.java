package com.sap.tools.jpicus.impl.serializable;

import com.sap.tools.jpicus.client.ListOperation;
import com.sap.tools.jpicus.client.ThreadInfo;

public class ListOperationImpl implements ListOperation {

	
	private ThreadInfo thread;
	private int length;
	private long duration;
	
	public ListOperationImpl(int size, long time){
		
		this.thread = new ThreadInfoImpl(5);
		this.duration = time;
		this.length = size;
		
	}
	
	
	public ThreadInfo getThreadInfo() {

		return thread;
	}

	
	public long getDuration() {

		return duration;
	}

	
	public int getLength() {
		
		return length;
	}

}
