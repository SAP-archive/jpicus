package com.sap.tools.jpicus.impl.serializable;

import org.junit.Test;

import junit.framework.Assert;

public class ThreadInfoImplTest {
	
	@Test
	public void testConstructor(){
		
		Options.parse("stackTraceLimit=0");
		StackTraceElement [] trace = Thread.currentThread().getStackTrace();
		
		ThreadInfoImpl info = new ThreadInfoImpl(0);
		Assert.assertEquals(trace.length, info.getStackTrace().length - 1);
		
		
	}
	
	@Test
	public void testConstructorWithNullContextClassLoader(){
		
		StackTraceElement [] trace = Thread.currentThread().getStackTrace();
		
		Thread.currentThread().setContextClassLoader(null);
		ThreadInfoImpl info = new ThreadInfoImpl(0);
		Assert.assertEquals(trace.length, info.getStackTrace().length - 1);
		
	}

}
