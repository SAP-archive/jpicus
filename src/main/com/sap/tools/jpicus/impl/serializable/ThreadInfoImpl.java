package com.sap.tools.jpicus.impl.serializable;

import java.util.Date;

import com.sap.tools.jpicus.client.ThreadInfo;
import com.sap.tools.jpicus.impl.util.Util;

public class ThreadInfoImpl implements ThreadInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private StackTraceElement [] stackTrace;
	private long timestamp;
	private int priority;
	private String context;
	private boolean isDaemon;

	private long id;
	
	public ThreadInfoImpl(String name,
						  StackTraceElement [] stackTrace,
						  long id,
						  String context
						  ){
		this.timestamp = System.currentTimeMillis();
		this.name = name;
		this.stackTrace = stackTrace;
		this.context = context;
		this.id = id;
		
		this.isDaemon = false; //fake
		this.priority = 0; // fake
		
		
		
	}
	
	
	public ThreadInfoImpl(int skipDepth){
		
		this.timestamp = System.currentTimeMillis();
		
		Thread thread = Thread.currentThread();
		this.priority = thread.getPriority();
		this.isDaemon = thread.isDaemon();
		this.id = thread.getId();
		
		if(Options.getInstance().getDisableThreadDetails()){
			
			String threadClassName = thread.getClass().getName();
			if(!threadClassName.equals("java.lang.Thread")){
				this.name = threadClassName;	
			} else {
				this.name = thread.getName();
			}
			
		} else {
			this.name = thread.getName();
			Object contextLoader = thread.getContextClassLoader();
			if(contextLoader != null){
				this.context = contextLoader.toString();	
			} else {
				this.context = "null";
			}
			
		}
		
		StackTraceElement [] trace = thread.getStackTrace();
		
		final int limit = Options.getInstance().getStackTraceLimit();
		int trimmedLength = trace.length - skipDepth;
		if(limit > 0 && trimmedLength > limit ){
			trimmedLength = limit;
		}
		if(trimmedLength != trace.length){
					
			StackTraceElement [] trimmedTrace = new StackTraceElement[trimmedLength];
			System.arraycopy(trace, skipDepth, trimmedTrace, 0, trimmedTrace.length);
			this.stackTrace = trimmedTrace;
		} else {
			this.stackTrace = trace;	
		}
		 
			
	}
	
	
	
	public String getName() {
		
		return this.name;
	}

	
	public StackTraceElement[] getStackTrace() {
		
		return this.stackTrace;
	}

	
	public long getTimestamp() {
		
		return this.timestamp;
	}

	
	public int hashCode() {
		return (int)this.timestamp;
	}
	
	
	public String toString() {
		
		StringBuilder buf = new StringBuilder(name.length() + stackTrace.length*100);
		buf.append(this.name).append(" from " + new Date(this.timestamp));
		Util.stackTraceToString(stackTrace, buf);
		return buf.toString();
	}


	
	public String getContext() {
		return this.context;
	}


	
	public int getPriority() {
		
		return this.priority;
	}


	
	public boolean isDaemon() {
		return this.isDaemon;
	}


	
	public long getId() {
		
		return this.id;
	}

	
}
