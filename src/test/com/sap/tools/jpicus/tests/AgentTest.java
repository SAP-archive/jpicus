package com.sap.tools.jpicus.tests;

import static org.junit.Assert.assertTrue;

import java.lang.instrument.Instrumentation;

import org.junit.Test;

import com.sap.tools.jpicus.Agent;

public class AgentTest {

	
	@Test
	public void testRedefineClassSupported(){

		Instrumentation inst = Agent.getInstrumentation();
		
		assertTrue(inst.isRedefineClassesSupported());
		
	}
	
	@Test
	public void testRetransformClassSupported(){
		
		Instrumentation inst = Agent.getInstrumentation();
		
		// TODO java se 5 exclude this test from the Java SE 5 suite
		assertTrue(inst.isRetransformClassesSupported());
		assertTrue(inst.isRedefineClassesSupported());
		assertTrue(inst.isNativeMethodPrefixSupported());
	}
	

}
