package com.sap.tools.jpicus.tests.nio;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;

public class FileChannelReadNIOTest {
	
	private static final int BUFFER_SIZE = 8192;

	private static File fileBB;
	private static File fileBBPos;
	private static File fileBBArray;
	private static File fileBBArrayIntInt;
	
	@BeforeClass
	public static void prepare() throws Exception {

		fileBB = TestUtil.createFile((int)TestUtil.getRandomLongCount(BUFFER_SIZE, 25000000), getClassName());
		fileBBPos = TestUtil.createFile((int)TestUtil.getRandomLongCount(BUFFER_SIZE, 25000000), getClassName());
		fileBBArray = TestUtil.createFile((int)TestUtil.getRandomLongCount(BUFFER_SIZE, 25000000), getClassName());
		fileBBArrayIntInt = TestUtil.createFile((int)TestUtil.getRandomLongCount(BUFFER_SIZE, 25000000), getClassName());
	}
	
	public static String getClassName() {
		return FileChannelReadNIOTest.class.getName();
	}
	
	@Test
	public void testReadByteBuffer() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBB, "r");
		FileChannel fch = raf.getChannel();
		ByteBuffer buf = ByteBuffer.allocateDirect(BUFFER_SIZE);
		
		int totalBytesRead = 0;
	    int minBytesRead = buf.capacity();
	    int maxBytesRead = 0;
		int count = 0;
	    
		while(true) {
			
			buf.clear();
			
			int bytesRead = fch.read(buf);
			
			if (bytesRead == -1) {
				break;
			}
			
			if(bytesRead < minBytesRead){
	            minBytesRead = bytesRead;
	        }
	        
	        if (bytesRead > maxBytesRead){
	            maxBytesRead = bytesRead;
	        }
	        
	        totalBytesRead += bytesRead;
	        count++;
	        
	        if(!TestUtil.isBoostPerformanceEnabled()){
	        	TestUtil.assertFileIOCountSingleHandle(fileBB.getCanonicalPath(),
	                    totalBytesRead, maxBytesRead, minBytesRead, count, CounterType.BYTES_READ);
		
	        }
		}
		
		fch.close();
				
		Assert.assertEquals(totalBytesRead, fileBB.length());
	      
	
	    TestUtil.assertFileIOCountSingleHandle(fileBB.getCanonicalPath(),
	                        totalBytesRead, maxBytesRead, minBytesRead,count, CounterType.BYTES_READ);
		
	}
	
	@Test
	public void testReadByteBufferAndPosition() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBBPos, "r");
		FileChannel fch = raf.getChannel();
		ByteBuffer buf = ByteBuffer.allocateDirect(BUFFER_SIZE);
		
		int totalBytesRead = 0;
	    int minBytesRead = buf.capacity();
	    int maxBytesRead = 0;
		int count = 0;
	    
		while(true) {
			
			buf.clear();
			
			int bytesRead = fch.read(buf, totalBytesRead);
			
			if (bytesRead == -1) {
				break;
			}		
			
			if(bytesRead < minBytesRead){
	            minBytesRead = bytesRead;
	        }
	        
	        if (bytesRead > maxBytesRead){
	            maxBytesRead = bytesRead;
	        }
	        
	        totalBytesRead += bytesRead;
	        count++;
	        
	        
	        if(!TestUtil.isBoostPerformanceEnabled()){
	        	TestUtil.assertFileIOCountSingleHandle(fileBBPos.getCanonicalPath(),
	                    totalBytesRead, maxBytesRead, minBytesRead, count, CounterType.BYTES_READ);
		
	        }
		}
		
		fch.close();
				
		Assert.assertEquals(totalBytesRead, fileBBPos.length());
		
	   TestUtil.assertFileIOCountSingleHandle(fileBBPos.getCanonicalPath(),
	                        totalBytesRead, maxBytesRead, minBytesRead, count , CounterType.BYTES_READ);
		
	}
	
	@Test
	public void testReadByteBufferArray() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBBArray, "r");
		FileChannel fch = raf.getChannel();
		
		final short byteBufferCount = (short) TestUtil.getRandomLongCount(1, 255);
		ByteBuffer[] buffers = new ByteBuffer[byteBufferCount];
		
		for(int i=0; i<byteBufferCount; i++) {
			//byteBuffer = ByteBuffer.wrap(new byte[BUFFER_SIZE]);
			buffers[i] = ByteBuffer.allocateDirect(BUFFER_SIZE);
		}
		
		long totalBytesRead = 0;
	    long minBytesRead = Long.MAX_VALUE;
	    long maxBytesRead = 0;
		int count = 0;
	    
		while(true) {
			
			for(ByteBuffer byteBuffer : buffers) {
				byteBuffer.clear();
			}
			
//			System.err.println("Read: " + count);
			long bytesRead = fch.read(buffers);
//			System.err.println("Done, "  + bytesRead + " bytes read");
			
			if (bytesRead == -1) {
				break;
			}		
			
			if(bytesRead < minBytesRead){
	            minBytesRead = bytesRead;
	        }
	        
	        if (bytesRead > maxBytesRead){
	            maxBytesRead = bytesRead;
	        }
	        
	        totalBytesRead += bytesRead;
	        count++;
	        
	        if(!TestUtil.isBoostPerformanceEnabled()){
	        	TestUtil.assertFileIOCountSingleHandle(fileBBArray.getCanonicalPath(),
	                    totalBytesRead, maxBytesRead, minBytesRead, count,CounterType.BYTES_READ);
		
	        }
		}
		
		fch.close();
				
		Assert.assertEquals(totalBytesRead, fileBBArray.length());
		  
	    TestUtil.assertFileIOCountSingleHandle(fileBBArray.getCanonicalPath(),
	                        totalBytesRead, maxBytesRead, minBytesRead, count,CounterType.BYTES_READ);
		
	}	
	
	@Test
	public void testReadByteBufferArrayInt() throws Exception {
		
		RandomAccessFile raf = new RandomAccessFile(fileBBArrayIntInt, "r");
		FileChannel fch = raf.getChannel();
		
		short byteBufferCount = (short) TestUtil.getRandomLongCount(1, 255);
		ByteBuffer[] buffers = new ByteBuffer[byteBufferCount];

		for(int i=0; i<byteBufferCount; i++) {
			buffers[i] = ByteBuffer.allocateDirect(BUFFER_SIZE);
		}
		
		long totalBytesRead = 0;
	    long minBytesRead = Long.MAX_VALUE;
	    long maxBytesRead = 0;
		int count = 0;
	    
		while(true) {
			
			for(ByteBuffer byteBuffer : buffers) {
				byteBuffer.clear();
			}
			
			long bytesRead = fch.read(buffers, 0, buffers.length);
			
			if (bytesRead == -1) {
				break;
			}		
			
			if(bytesRead < minBytesRead){
	            minBytesRead = bytesRead;
	        }
	        
	        if (bytesRead > maxBytesRead){
	            maxBytesRead = bytesRead;
	        }
	        
	        totalBytesRead += bytesRead;
	        count ++;
	        
	        
	        if(!TestUtil.isBoostPerformanceEnabled()){
	        	TestUtil.assertFileIOCountSingleHandle(fileBBArrayIntInt.getCanonicalPath(),
	                    totalBytesRead, maxBytesRead, minBytesRead, count, CounterType.BYTES_READ);
		
	        }
		}
		
		fch.close();
				
		Assert.assertEquals(totalBytesRead, fileBBArrayIntInt.length());
		
	    TestUtil.assertFileIOCountSingleHandle(fileBBArrayIntInt.getCanonicalPath(),
	                        totalBytesRead, maxBytesRead, minBytesRead, count, CounterType.BYTES_READ);
		
	}	
	
	@AfterClass
	public static void cleanUp() throws Exception{
		
		//Thread.sleep(1000);
		assertTrue(fileBB.delete());
		assertTrue(fileBBPos.delete());
		TestUtil.assertDeleteWithMoreInfo(fileBBArray);
		TestUtil.assertDeleteWithMoreInfo(fileBBArrayIntInt);
	}
}
