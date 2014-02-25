package com.sap.tools.jpicus.impl.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;


public class Deserializer<T> {
		
	
	public T deserialize(byte [] data) throws IOException {
		
		return deserialize(new ByteArrayInputStream(data));
		
		
	}
	
	@SuppressWarnings("unchecked")
	public T deserialize(InputStream in) throws IOException {
	
		ObjectInputStream ois = new ObjectInputStream(in);
		T result;
		try {
			result = (T)ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e.getMessage()); // TODO make it more comprehensive for Java SE 6
		}
		return result;
	}

}
