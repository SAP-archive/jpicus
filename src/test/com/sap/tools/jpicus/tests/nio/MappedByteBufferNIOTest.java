package com.sap.tools.jpicus.tests.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;

public class MappedByteBufferNIOTest {

	private File file;
	private static final int BUFFER_SIZE = 8192;
	
	@Before
	public void prepare() throws Exception {
		file = TestUtil.createFile((int)TestUtil.getRandomLongCount(BUFFER_SIZE, 25000000), getClassName());
	}
	
	@Test
	public void testReadMode() throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel fch = raf.getChannel();
		MappedByteBuffer mbb = fch.map(MapMode.READ_ONLY, 0, fch.size());
		
		mbb.load();
		TestUtil.assertFileIOCount(file.getCanonicalPath(), fch.size(), CounterType.BYTES_READ);
		
		mbb.force();
		TestUtil.assertFileIOCount(file.getCanonicalPath(), 0, CounterType.BYTES_WRITE);
		
		fch.close();
		raf.close();
	}
	
	@Test
	public void testWriteMode() throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		FileChannel fch = raf.getChannel();
		MappedByteBuffer mbb = fch.map(MapMode.READ_WRITE, 0, fch.size());
		
		mbb.load();
		TestUtil.assertFileIOCount(file.getCanonicalPath(), fch.size(), CounterType.BYTES_READ);
		
		mbb.put(new byte[BUFFER_SIZE]);
		mbb.force();
		TestUtil.assertFileIOCount(file.getCanonicalPath(), BUFFER_SIZE, CounterType.BYTES_WRITE);
		
		fch.close();
		raf.close();
	}
	
	@Test
	public void testPrivateMode() throws IOException {
		
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		FileChannel fch = raf.getChannel();
		MappedByteBuffer mbb = fch.map(MapMode.PRIVATE, 0, fch.size());
		
		mbb.load();
		TestUtil.assertFileIOCount(file.getCanonicalPath(), fch.size(), CounterType.BYTES_READ);
		
		mbb.put(new byte[BUFFER_SIZE]);
		mbb.force();
		TestUtil.assertFileIOCount(file.getCanonicalPath(), 0, CounterType.BYTES_WRITE);
		
		fch.close();
		raf.close();
	}

	@After
	public void cleanUp() throws Exception {
		TestUtil.assertDeleteWithMoreInfo(file);
	}

	public static String getClassName() {
		return FileChannelReadNIOTest.class.getName();
	}
}
