package com.sap.tools.jpicus.impl.transformers.io.zip;

import java.io.IOException;
import java.io.InputStream;

import com.sap.tools.jpicus.Agent;

public class IOTracingInputStreamWrapper extends InputStream {

	private InputStream stream;
	private Object handle;
	
	public IOTracingInputStreamWrapper(InputStream wrapped, Object handle){
		this.stream = wrapped;
		this.handle = handle;
	}
	
	
	@Override
	public int available() throws IOException {
	
		return this.stream.available();
	}

	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
	
	@Override
	public boolean equals(Object obj) {

		return stream.equals(obj);
	}
	
	@Override
	public int hashCode() {
		
		return stream.hashCode();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		
		stream.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {

		return stream.markSupported();
	}
	
	@Override
	public int read(byte[] b) throws IOException {

		Agent.startIO(handle);
		int result = stream.read(b);
		Agent.fileInput(result, handle);
		
		return result;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
	
		Agent.startIO(handle);
		int result = stream.read(b, off, len);
		Agent.fileInput(result, handle);
		
		return result;
	
	}
	
	@Override
	public synchronized void reset() throws IOException {
		
		stream.reset();
	}
	
	@Override
	public long skip(long n) throws IOException {
		
		return stream.skip(n);
	}
	
	@Override
	public String toString() {
		
		return this.getClass().getName() + " wrapper for " + stream.toString();
	}
	
	
	
	@Override
	public int read() throws IOException {
		
		Agent.startIO(handle);
		int result = this.stream.read();
		Agent.fileInput(1, handle);
		
		return result;
	}
	
}