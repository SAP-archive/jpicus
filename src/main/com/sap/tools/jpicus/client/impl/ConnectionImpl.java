package com.sap.tools.jpicus.client.impl;

import java.io.IOException;
import java.rmi.RemoteException;

import com.sap.tools.jpicus.client.AgentManager;
import com.sap.tools.jpicus.client.AgentOptions;
import com.sap.tools.jpicus.client.Connection;
import com.sap.tools.jpicus.client.Snapshot;
import com.sap.tools.jpicus.client.ThreadInfo;
import com.sap.tools.jpicus.impl.util.Deserializer;

public class ConnectionImpl implements Connection {

	private String host;
	private int port;
	private AgentOptions options;
	
	private AgentManager agentManager;
	public ConnectionImpl(String host, int port, AgentManager remoteService) {
		this.host = host;
		this.port = port;
		this.agentManager = remoteService;
	}

	
	public Snapshot getSnapshot() throws RemoteException {
	
		
		byte [] snapshotBytes = agentManager.createSnapshot();
		
		if(System.getProperty("com.sap.tools.jpicus.client.verbose")!= null){
			System.out.println("Snapshot size: " + snapshotBytes.length);
		}
		
		try {
			Snapshot snapshot = new Deserializer<Snapshot>().deserialize(snapshotBytes);
			return snapshot;
		} catch (IOException e) {
			
			throw new RemoteException("Deserialization failed", e);
		}
	}

	
	public void close() {
		// TODO Auto-generated method stub
		
	}

	
	public String getHost() {
		return this.host;
	}

	
	public int getPort() {
		return this.port;
	}


	
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		
		if(! (obj instanceof Connection)){
			return false;
		}
		
		Connection other = (Connection)obj;
		
		if(other.getPort() != this.port){
			return false;
		}
		
		if(!other.getHost().equals(this.host)){
			return false;
		}
		
		return true;

	}

	
	public long currentTimeMillis() throws RemoteException{
	
		return agentManager.currentTimeMillis();
	}
	
	
	public AgentOptions getOptions() throws RemoteException {
		
		if(this.options == null){
			this.options = agentManager.getOptions();	
		}
		return this.options;
	}

	
	public void closeHandle(long id) throws RemoteException {
		agentManager.closeHandle(id);
		
	}

	
	public ThreadInfo getThreadInfo(long id) throws RemoteException {
		return agentManager.getThreadInfo(id);
	}
	
	
}
