package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;

public class RandomAccessFileIOTest {
	
    private final static String [] MODES = {"r", "rw", "rws" , "rwd"};
    private final static String [] WRITE_MODES = {"rw", "rws" , "rwd"};
  
	File fileReadNoArg;
	File fileReadByteArray;
	File fileReadByteArrayIntInt;
	
	File fileWriteByteArray;
	File fileWriteByteArrayIntInt;
	
	@Before
	public void prepare() throws Exception {

		fileReadNoArg = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 20000), this.getClass().getName());
		// bigger file sizes can cause OOM because of Junit logs - each of the files below is tested MODES.length times
		fileReadByteArray = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 1000000), this.getClass().getName());
		fileReadByteArrayIntInt = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 1000000), this.getClass().getName());
		fileWriteByteArray = TestUtil.createEmptyFile( this.getClass().getName());
		fileWriteByteArrayIntInt = TestUtil.createEmptyFile( this.getClass().getName());
	}
	
	@Test
	public void testReadNoArg() throws Exception {
		
		// TODO java se 5
		if(!Agent.getInstrumentation().isNativeMethodPrefixSupported()){
			System.out.println("testReadNoArg() - Not applicable");
			return;
		}
		//FileInputStream fis = new FileInputStream(fileNoArg);
		RandomAccessFile raf = new RandomAccessFile(fileReadNoArg, "r");
		
		int totalBytesRead = 0;

		while(true){
			
			final int singleByte = raf.read();
			
			if (singleByte == -1){
				break;
			}
			
			totalBytesRead ++;
			
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileReadNoArg.getCanonicalPath(), totalBytesRead, 1, 1,totalBytesRead, CounterType.BYTES_READ);	
			}
					
		}
	
		Assert.assertEquals(totalBytesRead, fileReadNoArg.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileReadNoArg.getCanonicalPath(), totalBytesRead, 1, 1,totalBytesRead, CounterType.BYTES_READ);
		
		raf.close();
		
	}
	
	@Test
	public void testReadByteArray() throws Exception {
		
		byte [] buf = new byte [1024];

		// "global" statistics - must stay outside the loops
        int totalBytesRead = 0;
        int minBytesRead = buf.length;
        int maxBytesRead = 0;
		int count = 0;
        
		for(String mode : MODES) {
		  
		    RandomAccessFile raf = new RandomAccessFile(fileReadByteArray, mode);
		
    		int currentSize = 0;

    		while(true){
    			
    			final int bytesRead = raf.read(buf);
    			
    			if (bytesRead == -1){
    				break;
    			}
    			
    			if(bytesRead < minBytesRead){
    				minBytesRead = bytesRead;
    			}
    			
    			if (bytesRead > maxBytesRead){
    				maxBytesRead = bytesRead;
    			}
    			totalBytesRead += bytesRead;
    			currentSize += bytesRead;
    			count++;
    			
    			// this check generates a lot of overhead because a snapshot is created in each iteration
    			if(!TestUtil.isBoostPerformanceEnabled()){
    				TestUtil.assertFileIOCountMultipleHandles(fileReadByteArray.getCanonicalPath(),
		                    totalBytesRead, maxBytesRead, minBytesRead, count,CounterType.BYTES_READ);	
    			}
    					
    		}
    	
    		Assert.assertEquals(currentSize, fileReadByteArray.length());
    		
    		TestUtil.assertFileIOCountMultipleHandles(fileReadByteArray.getCanonicalPath(), totalBytesRead, buf.length, minBytesRead,count, CounterType.BYTES_READ);
    		
    		raf.close();
		}
		
	}
	
	//  test case for read(byte[], int, int)
	@Test
	public void testReadByteArrayIntInt() throws Exception {
	  
	  byte [] buf = new byte [1024];

	  // "global" statistics - must stay outside the loops
      int totalBytesRead = 0;
      int minBytesRead = buf.length;
      int maxBytesRead = 0;
      int count = 0;
      
      for(String mode : MODES) {
    	  
          RandomAccessFile raf = new RandomAccessFile(fileReadByteArrayIntInt, mode);
    	  
    	  int currentSize = 0;
          
          while (true) {
            
            final int bytesRead = raf.read(buf, 0, buf.length);
            
            if (bytesRead == -1){
                break;
            }
            
            if(bytesRead < minBytesRead){
                minBytesRead = bytesRead;
            }
            
            if (bytesRead > maxBytesRead){
                maxBytesRead = bytesRead;
            }
            
            totalBytesRead += bytesRead;
            currentSize += bytesRead;
            count++;
          
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountMultipleHandles(fileReadByteArrayIntInt.getCanonicalPath(),
                        totalBytesRead, maxBytesRead, minBytesRead, count,CounterType.BYTES_READ);	
			}
            
          }
          
          Assert.assertEquals(currentSize, fileReadByteArrayIntInt.length());
          
          TestUtil.assertFileIOCountMultipleHandles(fileReadByteArrayIntInt.getCanonicalPath(),
                              totalBytesRead, buf.length, minBytesRead,count, CounterType.BYTES_READ);
         
          raf.close();
      }
	}
	
	
	@Test
	public void testWriteByteArray() throws Exception {
		
		byte [] buf = new byte [1024];
		final int iterations = (int) TestUtil.getRandomLongCount(1, 1000);
		
		int totalBytesWritten = 0;
		int count = 0;
		for(String mode : WRITE_MODES) {
    		RandomAccessFile fos = new RandomAccessFile(fileWriteByteArray, mode);		
    		
    		int currentSize = 0;
    		
    		for (int i = 0; i < iterations; i++) {
    			
    			fos.write(buf);
    			
    			totalBytesWritten += buf.length;
    			currentSize += buf.length;
    			count++;
    			
    			// this check generates a lot of overhead because a snapshot is created in each iteration
    			if(!TestUtil.isBoostPerformanceEnabled()){
    				TestUtil.assertFileIOCountMultipleHandles(fileWriteByteArray.getCanonicalPath(),
		                    totalBytesWritten, buf.length, buf.length, count, CounterType.BYTES_WRITE);	
    			}
    					
    		}
    	
    		Assert.assertEquals(currentSize, fileWriteByteArray.length());
    				
    		fos.close();
    		
    		TestUtil.assertFileIOCountMultipleHandles(fileWriteByteArray.getCanonicalPath(),
    		                    totalBytesWritten, buf.length, buf.length, count, CounterType.BYTES_WRITE);
		}
	}

	@Test
    public void testWriteByteArrayIntInt() throws Exception {
        
        byte [] buf = new byte [1024];
        final int iterations = (int) TestUtil.getRandomLongCount(1, 1000);
        
        int totalBytesWritten = 0;
        int count = 0;
        
        for(String mode : WRITE_MODES) {
            RandomAccessFile fos = new RandomAccessFile(fileWriteByteArrayIntInt, mode);      
            
            int currentSize = 0;
            
            for (int i = 0; i < iterations; i++) {
                
                fos.write(buf, 0, buf.length);
                
                totalBytesWritten += buf.length;
                count++;
                currentSize += buf.length;
    			// this check generates a lot of overhead because a snapshot is created in each iteration
    			if(!TestUtil.isBoostPerformanceEnabled()){
    				TestUtil.assertFileIOCountMultipleHandles(fileWriteByteArrayIntInt.getCanonicalPath(),
                            totalBytesWritten, buf.length, buf.length,count, CounterType.BYTES_WRITE);	
    			}
                        
            }
        
            Assert.assertEquals(currentSize, fileWriteByteArrayIntInt.length());
                    
            fos.close();
            
            TestUtil.assertFileIOCountMultipleHandles(fileWriteByteArrayIntInt.getCanonicalPath(),
                                totalBytesWritten, buf.length, buf.length,count, CounterType.BYTES_WRITE);
        }
    }
	
	@After
	public void cleanUp(){
		
		assertTrue(fileReadNoArg.delete());
		assertTrue(fileReadByteArray.delete());
		assertTrue(fileReadByteArrayIntInt.delete());

		assertTrue(fileWriteByteArray.delete());
		assertTrue(fileWriteByteArrayIntInt.delete());
	}

}
