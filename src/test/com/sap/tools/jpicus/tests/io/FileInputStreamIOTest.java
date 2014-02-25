package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;


public class FileInputStreamIOTest {
	
	File fileNoArg;
	File fileByteArray;
	File fileByteArrayIntInt;
	
	@Before
	public void prepare() throws Exception {

		fileNoArg = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 20000), this.getClass().getName());
		fileByteArray = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 25000000), this.getClass().getName());
		fileByteArrayIntInt = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 25000000), this.getClass().getName());
		
	}
	
	@Test
	public void testReadNoArg() throws Exception {
		
		// TODO java se 5
		if(!Agent.getInstrumentation().isNativeMethodPrefixSupported()){
			System.err.println("testReadNoArg() - not applicable");
			return;
		}
		FileInputStream fis = new FileInputStream(fileNoArg);
		
		int totalBytesRead = 0;
		int count = 0;
		
		while(true){
			
			final int singleByte = fis.read();
			
			
			if (singleByte == -1){
				break;
			}
			
			count++;
			totalBytesRead ++;
			
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileNoArg.getCanonicalPath(), totalBytesRead, 1, 1,count, CounterType.BYTES_READ);	
			}
					
		}
	
		Assert.assertEquals(totalBytesRead, fileNoArg.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileNoArg.getCanonicalPath(), totalBytesRead, 1, 1, count, CounterType.BYTES_READ);
		
		fis.close();
		
	}
	
	@Test
	public void testReadByteArray() throws Exception {
		
		byte [] buf = new byte [1024];
		
		FileInputStream fis = new FileInputStream(fileByteArray);
		
		int totalBytesRead = 0;
		int minBytesRead = buf.length;
		int maxBytesRead = 0;
		int count = 0;
		while(true){
			
			final int bytesRead = fis.read(buf);
			
			if (bytesRead == -1){
				break;
			}
			
			if(bytesRead < minBytesRead){
				minBytesRead = bytesRead;
			}
			
			if (bytesRead > maxBytesRead){
				maxBytesRead = bytesRead;
			}
			count++;
			totalBytesRead += bytesRead;
			
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
													   totalBytesRead,
													   maxBytesRead,
													   minBytesRead,
													   count,
													   CounterType.BYTES_READ);	
			}
					
		}
	
		Assert.assertEquals(totalBytesRead, fileByteArray.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
											   totalBytesRead,
											   buf.length,
											   minBytesRead,
											   count,											   
											   CounterType.BYTES_READ);
		
		fis.close();
		
	}
	
	//  test case for read(byte[], int, int)
	@Test
	public void testReadByteArrayIntInt() throws Exception {
	  
	  byte [] buf = new byte [1024];
	  
	  FileInputStream fis = new FileInputStream(fileByteArrayIntInt);
	  
	  int totalBytesRead = 0;
      int minBytesRead = buf.length;
      int maxBytesRead = 0;
      int count = 0;
      
      while (true) {
        
        final int bytesRead = fis.read(buf, 0, buf.length);
        
        if (bytesRead == -1){
            break;
        }
        
        if(bytesRead < minBytesRead){
            minBytesRead = bytesRead;
        }
        
        if (bytesRead > maxBytesRead){
            maxBytesRead = bytesRead;
        }
        
        count++;
        totalBytesRead += bytesRead;
        
		// this check generates a lot of overhead because a snapshot is created in each iteration
        if(!TestUtil.isBoostPerformanceEnabled()){
        	TestUtil.assertFileIOCountSingleHandle(fileByteArrayIntInt.getCanonicalPath(),
                    							   totalBytesRead,
                    							   maxBytesRead,
                    							   minBytesRead,
                    							   count,
                    							   CounterType.BYTES_READ);
	
        }
      }
      
      Assert.assertEquals(totalBytesRead, fileByteArrayIntInt.length());
      
      TestUtil.assertFileIOCountSingleHandle(fileByteArrayIntInt.getCanonicalPath(),
    		  								 totalBytesRead,
    		  								 buf.length,
    		  								 minBytesRead,
    		  								 count,
    		  								 CounterType.BYTES_READ);
      
      fis.close();
	}
	
	@After
	public void cleanUp(){
		
		assertTrue(fileNoArg.delete());
		assertTrue(fileByteArray.delete());
		assertTrue(fileByteArrayIntInt.delete());
		
	}

}
