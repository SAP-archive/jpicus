package com.sap.tools.jpicus.client;

import java.util.Map;
import java.util.Set;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String host = "localhost";
		System.out.println("Args: " + args.length);
		
		if(args.length > 0 && args[0] != null  ){
			host = args[0];
		}
		
		Connection con  = JPicus.connect(host);
		Snapshot s = con.getSnapshot();
		Map<String, Set<HandleDescriptor>> handles = s.getHandles();
		System.out.println("-----------Handles--------------------------------");
		System.out.println(handles);
		System.out.println("--------------------------------------------------");
		
		Map<String, Set<DeleteOperation>> deleteOperations = s.getDeleteOperations();
		System.out.println("--------------Delete operations-------------------");
		System.out.println(deleteOperations);
		System.out.println("--------------------------------------------------");			
		
	}
	
}
