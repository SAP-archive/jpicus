package com.sap.tools.jpicus.tests.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;

public class FileChannelForceNIOTest {

	private static final int BUFFER_SIZE = 8192;
	
	private File file;
	
	@Before
	public void prepare() throws Exception {
		file = TestUtil.createFile((int)TestUtil.getRandomLongCount(BUFFER_SIZE, 25000000), getClassName());
	}
	
	@Test
	public void testForce() throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		FileChannel fch = raf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		buffer.clear();
		
		// update file channel and force changes onto disk without synchiing metadata for proper bytes count
		fch.write(buffer);
		fch.force(false);
		
		TestUtil.assertFileIOCount(file.getCanonicalPath(), BUFFER_SIZE, CounterType.BYTES_WRITE);
		
		fch.close();
	}
	
	@After
	public void cleanUp() {
		Assert.assertTrue(file.delete());
	}

	public static String getClassName() {
		return FileChannelReadNIOTest.class.getName();
	}
}
