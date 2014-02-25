package com.sap.tools.jpicus;

import static org.junit.Assert.*;
import org.junit.Test;

import com.sap.tools.jpicus.impl.serializable.Options;

public class OptionsTest {

	private String sep = ";";
	
	@Test
	public void test_01_DefaultOptions(){
		
		assertFalse(Options.getInstance().getKeepClosedHandles());
		assertFalse(Options.getInstance().getTrackSuccessfulDelete());
		assertFalse(Options.getInstance().getTrackFailedDelete());
		assertFalse(Options.getInstance().getTrackIO());
		assertFalse(Options.getInstance().isCalibrationEnabled());
		assertFalse(Options.getInstance().dumpStateOnExit());
		assertFalse(Options.getInstance().getCollectDeleteOfNonExisting());
		assertFalse(Options.getInstance().getDisableThreadDetails());
		assertEquals(0, Options.getInstance().getStackTraceLimit());
		assertEquals(21500, Options.getInstance().getPort());
		
	}
	
	
	@Test
	public void test_02_ParseWithNull(){ // same as default
	
		Options.parse(null);

		assertFalse(Options.getInstance().getKeepClosedHandles());
		assertFalse(Options.getInstance().getTrackSuccessfulDelete());
		assertFalse(Options.getInstance().getTrackFailedDelete());
		assertFalse(Options.getInstance().getTrackIO());
		assertFalse(Options.getInstance().isCalibrationEnabled());
		assertFalse(Options.getInstance().dumpStateOnExit());
		assertFalse(Options.getInstance().getCollectDeleteOfNonExisting());
		assertFalse(Options.getInstance().getDisableThreadDetails());
		assertEquals(0, Options.getInstance().getStackTraceLimit());
		assertEquals(21500, Options.getInstance().getPort());
	}
	
	public void test_03_ParseAll(){
		
		String args = 
			"keepClosedHandles" + sep +
			"trackSuccessfulDelete" + sep +
			"trackFailedDelete" + sep +
			"trackIO"  + sep +
			"calibrate"  + sep +
			"dumpStateOnExit"  + sep +
			"collectDeleteOfNonExisting"  + sep +
			"disableThreadDetails"  + sep +
			"stackTraceLimit=20"  + sep +
			"port=8080";
		
		Options.parse(args);
	

		assertTrue(Options.getInstance().getKeepClosedHandles());
		assertTrue(Options.getInstance().getTrackSuccessfulDelete());
		assertTrue(Options.getInstance().getTrackFailedDelete());
		assertTrue(Options.getInstance().getTrackIO());
		assertTrue(Options.getInstance().isCalibrationEnabled());
		assertTrue(Options.getInstance().dumpStateOnExit());
		assertTrue(Options.getInstance().getCollectDeleteOfNonExisting());
		assertTrue(Options.getInstance().getDisableThreadDetails());
		assertEquals(20, Options.getInstance().getStackTraceLimit());
		assertEquals(8080, Options.getInstance().getPort());	
		
	}
	
	public void test_04_ParseDeleteOperations(){
		
		String args = 
		
			"trackSuccessfulDelete" + sep +
			"trackFailedDelete";
		
		Options.parse(args);
	

		assertFalse(Options.getInstance().getKeepClosedHandles());
		assertTrue(Options.getInstance().getTrackSuccessfulDelete());
		assertTrue(Options.getInstance().getTrackFailedDelete());
		assertFalse(Options.getInstance().getTrackIO());
		assertFalse(Options.getInstance().isCalibrationEnabled());
		assertFalse(Options.getInstance().dumpStateOnExit());
		assertFalse(Options.getInstance().getCollectDeleteOfNonExisting());
		assertFalse(Options.getInstance().getDisableThreadDetails());
		assertEquals(0, Options.getInstance().getStackTraceLimit());
		assertEquals(21500, Options.getInstance().getPort());	
		
	}
}
