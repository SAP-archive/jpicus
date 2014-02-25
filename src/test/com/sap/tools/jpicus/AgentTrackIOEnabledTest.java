package com.sap.tools.jpicus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.impl.serializable.Options;
import com.sap.tools.jpicus.tests.TestUtil;

public class AgentTrackIOEnabledTest {
	
	String path;
	
	@Before
	public void prepare() throws Exception {
	
		path = TestUtil.createEmptyFile(this.getClass().getName()).getCanonicalPath();
		Options.parse("trackIO");
		
		Agent.initManagers();
	}
	
	@Test
	public void testFileInput() throws Exception {
		
		TestUtil.assertClosed(path);
		
		Object handle = new Object();
		
		Agent.fileOpened(path, handle);
		
		TestUtil.assertOpened(path, handle);
		
		Agent.startIO(handle);
		
		Thread.sleep(100);
		
		final int total = 1000; 
		Agent.fileInput(total, handle);
		
		TestUtil.assertFileIOCountSingleHandle(path, total, total, total, 1, CounterType.BYTES_READ);
		
		Agent.fileClosed(handle);
		
		TestUtil.assertClosed(path);
	}
	
	@After
	public void cleanUp(){
		Assert.assertTrue("File can't be deleted", new File(path).delete());
	}

}
