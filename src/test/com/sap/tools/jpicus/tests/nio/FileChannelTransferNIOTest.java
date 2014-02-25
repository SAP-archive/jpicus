package com.sap.tools.jpicus.tests.nio;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;

public class FileChannelTransferNIOTest {

	private File fileRead;
	private RandomAccessFile rafRead;
	private FileChannel fchRead;
	
	private File fileWrite;
	private RandomAccessFile rafWrite;
	private FileChannel fchWrite;
	
	@Before
	// Use @Before instead of @BeforeClass because the class properties have to be reinitialized before each test method run 
	public void prepare() throws Exception {
		fileRead = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 25000000), getClassName());
		fileWrite = TestUtil.createEmptyFile(getClassName());
		
		rafRead = new RandomAccessFile(fileRead, "r");
		rafWrite = new RandomAccessFile(fileWrite, "rw");
		
		fchRead = rafRead.getChannel();
		fchWrite = rafWrite.getChannel();
	}
	
	@After
	public void cleanUp() {
		assertTrue(fileRead.delete());
		assertTrue(fileWrite.delete());
	}
	
	@Test
	public void testTransferTo() throws Exception {
		
		final long startTime = System.nanoTime();
		fchRead.transferTo(0, fchRead.size(), fchWrite);
		final long timeMeasurement = System.nanoTime() - startTime;
		
		fchRead.close();
		fchWrite.close();
		
		Assert.assertEquals(fileRead.length(), fileWrite.length());
		
		TestUtil.assertFileChannelTransfer(fileRead.getCanonicalPath(), fileWrite.getCanonicalPath(),
				fileRead.length(), timeMeasurement);
	}
	
	@Test
	@Ignore // This one won't work until the memory mapped instrumentations are in place
	public void testTransferFrom() throws Exception {
		
		long startTime = System.nanoTime();
		fchWrite.transferFrom(fchRead, 0, fchRead.size());
		long timeMeasurement = System.nanoTime() - startTime;
		
		fchRead.close();
		fchWrite.close();
		
		Assert.assertEquals(fileRead.length(), fileWrite.length());
		
		TestUtil.assertFileChannelTransfer(fileRead.getCanonicalPath(), fileWrite.getCanonicalPath(),
				fileRead.length(), timeMeasurement);
	}

	public static String getClassName() {
		return FileChannelReadNIOTest.class.getName();
	}
	
	
}
