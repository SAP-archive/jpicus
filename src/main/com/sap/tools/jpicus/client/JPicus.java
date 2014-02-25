package com.sap.tools.jpicus.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.Naming;

import com.sap.tools.jpicus.client.impl.ConnectionImpl;
import com.sap.tools.jpicus.impl.util.Deserializer;

public class JPicus {

	
	public static Connection connect(String host, int port) throws ConnectionException {
            
		// TODO consider using new InitialContext and associate it with the connection so that it can be closed
		AgentManager remoteService;
		try {
			remoteService = (AgentManager) Naming.lookup("//" + host + ":" + port + "/" + AgentManager.class.getName());
		} catch (Exception e) {
			throw new ConnectionException("Connection failed", e);
		} 
        return new ConnectionImpl(host, port, remoteService);
		
		
	}
	
	public static Connection connect(String host) throws Exception {
		return connect (host, 21500);
	}
	
	/**
	 * Load a previously stored state. The method closes the stream
	 * after it has finished reading it. 
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Snapshot load(InputStream in) throws IOException {
		
		return new Deserializer<Snapshot>().deserialize(in);
	}
	
	
	/**
	 * Save the state of this snapshot to a file so that it
	 * can be loaded back into memory later on. The method closes the stream
	 * after it has finished reading it.
	 * @param file
	 * @throws IOException
	 */
	public static void save(Snapshot snapshot, OutputStream out) throws IOException {

		ObjectOutputStream oos = new ObjectOutputStream(out);
		try {
			oos.writeObject(snapshot);
	
		} finally {
			oos.close();
		}
				
	}

	
}
