package com.sap.tools.jpicus.tests.nio;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;

public class FileChannelWriteNIOTest {
	
	private static final int BUFFER_SIZE = 8192;

	private static File fileBB;
	private static File fileBBPos;
	private static File fileBBArray;
	private static File fileBBArrayIntInt;
	
	@BeforeClass
	public static void prepare() throws Exception {

		fileBB = TestUtil.createEmptyFile(getClassName());
		fileBBPos = TestUtil.createEmptyFile(getClassName());
		fileBBArray = TestUtil.createEmptyFile(getClassName());
		fileBBArrayIntInt = TestUtil.createEmptyFile(getClassName());
	}

	@AfterClass
	public static void cleanUp() throws Exception{
		
		assertTrue(fileBB.delete());
		assertTrue(fileBBPos.delete());
		TestUtil.assertDeleteWithMoreInfo(fileBBArray);
		TestUtil.assertDeleteWithMoreInfo(fileBBArrayIntInt);
	}
	
	@Test
	public void testWriteByteBuffer() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBB, "rw");
		FileChannel fch = raf.getChannel();
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		
		final int iterations = (int) TestUtil.getRandomLongCount(1, 10000);
		long total = 0;
		long min = Long.MAX_VALUE;
	    long max = 0;
		int count = 0;
		
		for(int i = 0; i < iterations; i++) {
			
			buffer.clear();
			
			int bytesWritten = fch.write(buffer);
			
			if(bytesWritten < min){
	            min = bytesWritten;
	        }
	        
	        if (bytesWritten > max){
	            max = bytesWritten;
	        }
			
			total += bytesWritten;
			count ++;

			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileBB.getCanonicalPath(),
	                    total, max, min, count, CounterType.BYTES_WRITE);	
			}
		}
		
		fch.close();
		
		Assert.assertEquals(total, fileBB.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileBB.getCanonicalPath(),
                total, max, min, count, CounterType.BYTES_WRITE);	
	}
	
	@Test
	public void testWriteByteBufferAndPosition() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBBPos, "rw");
		FileChannel fch = raf.getChannel();
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		
		final int iterations = (int) TestUtil.getRandomLongCount(1, 10000);
		int total = 0;
		long min = Long.MAX_VALUE;
	    long max = 0;
		int count = 0;
		
		for(int i = 0; i < iterations; i++) {
			
			buffer.clear();
			
			int bytesWritten = fch.write(buffer, total);
			
			if(bytesWritten < min){
	            min = bytesWritten;
	        }
	        
	        if (bytesWritten > max){
	            max = bytesWritten;
	        }
			
			total += bytesWritten;
			count++;
			
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileBBPos.getCanonicalPath(),
	                    total,max,min, count, CounterType.BYTES_WRITE);	
			}
		}
		
		fch.close();
		
		Assert.assertEquals(total, fileBBPos.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileBBPos.getCanonicalPath(),
                total,max,min, count, CounterType.BYTES_WRITE);	
	}
	
	@Test
	public void testWriteByteBufferArray() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBBArray, "rw");
		FileChannel fch = raf.getChannel();

		final short bufferCount = (short) TestUtil.getRandomLongCount(1, 100); 
		ByteBuffer[] buffers = new ByteBuffer[bufferCount];
		
		for(int i = 0; i < bufferCount; i++) {
			buffers[i] = ByteBuffer.allocateDirect(BUFFER_SIZE);
		}
		
		final int iterations = (int) TestUtil.getRandomLongCount(1, 500);
		
		long total = 0;
		long min = Long.MAX_VALUE;
	    long max = 0;
		int count = 0;
		
		for(int i = 0; i < iterations; i++) {
			
			for(ByteBuffer buffer : buffers) {
				buffer.clear();
			}
			
			long bytesWritten = fch.write(buffers);
			
			if(bytesWritten < min){
	            min = bytesWritten;
	        }
	        
	        if (bytesWritten > max){
	            max = bytesWritten;
	        }
			
			total += bytesWritten;
			count++;
			
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileBBArray.getCanonicalPath(),
	                    total,max,min, count,CounterType.BYTES_WRITE);	
			}
		}
		
		fch.close();
		
		Assert.assertEquals(total, fileBBArray.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileBBArray.getCanonicalPath(),
                total,max,min, count,CounterType.BYTES_WRITE);		
	}
	
	@Test
	public void testWriteByteBufferArrayIntInt() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBBArrayIntInt, "rw");
		FileChannel fch = raf.getChannel();

		final short bufferCount = (short) TestUtil.getRandomLongCount(1, 100); 
		ByteBuffer[] buffers = new ByteBuffer[bufferCount];

		for(int i = 0; i < bufferCount; i++) {
			buffers[i] = ByteBuffer.allocateDirect(BUFFER_SIZE);
		}
		
		final int iterations = (int) TestUtil.getRandomLongCount(1, 500);
		
		long total = 0;
		long min = Long.MAX_VALUE;
	    long max = 0;
	
		int count = 0;
		
		for(int i = 0; i < iterations; i++) {
			
			for(ByteBuffer buffer : buffers) {
				buffer.clear();
			}
			
			final long bytesWritten = fch.write(buffers, 0, buffers.length);
	
			if(bytesWritten < min){
	            min = bytesWritten;
	        }
	        
	        if (bytesWritten > max){
	            max = bytesWritten;
	        }
	
			
			total += bytesWritten;
			count++;
			
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileBBArrayIntInt.getCanonicalPath(),
	                    total, max, min, count,CounterType.BYTES_WRITE);	
			}
		}
		
		fch.close();
		
		Assert.assertEquals(total, fileBBArrayIntInt.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileBBArrayIntInt.getCanonicalPath(),
                total, max, min, count,CounterType.BYTES_WRITE);	
	}

	public static String getClassName() {
		return FileChannelReadNIOTest.class.getName();
	}
}
