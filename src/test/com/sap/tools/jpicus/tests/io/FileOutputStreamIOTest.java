package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;

public class FileOutputStreamIOTest {
	
	File fileByte;
	File fileByteArray;
	File fileByteArrayIntInt;
	
	@Before
	public void prepare() throws Exception {

	    fileByte = TestUtil.createEmptyFile(this.getClass().getName());
		fileByteArray = TestUtil.createEmptyFile( this.getClass().getName());
		fileByteArrayIntInt = TestUtil.createEmptyFile(this.getClass().getName());
		
	}
	
	// TODO move this method in a separate class so that it can be
	// excluded from the Java SE 5 test suite
	@Test
	public void testWriteByte() throws Exception {
	
		
		if(!Agent.getInstrumentation().isNativeMethodPrefixSupported()){
			System.err.println("testWriteByte() - Not applicable");
			return;
		}
	  
	    final int size = (int) TestUtil.getRandomLongCount(1, 1000);
	    int b = (int) TestUtil.getRandomLongCount(0, Byte.MAX_VALUE);
	  
		FileOutputStream fos = new FileOutputStream(fileByte);
		
		int totalBytesWritten = 0;
		int count = 0;
		
		while (totalBytesWritten < size) {
		  
			fos.write(b);
			totalBytesWritten++;
			count++;
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileByte.getCanonicalPath(), totalBytesWritten, 1, 1, count, CounterType.BYTES_WRITE);	
			}
		   
		}
	
		Assert.assertEquals(totalBytesWritten, fileByte.length());
		
		TestUtil.assertFileIOCountSingleHandle(fileByte.getCanonicalPath(), totalBytesWritten, 1, 1,count, CounterType.BYTES_WRITE);		
		
		fos.close();
		
	}
	
	@Test
	public void testWriteByteArray() throws Exception {
		
		byte [] buf = new byte [1024];
		final int iterations = (int) TestUtil.getRandomLongCount(1, 1000);
		
		FileOutputStream fos = new FileOutputStream(fileByteArray);
		
		int totalBytesWritten = 0;
		int count = 0;
		
		for (int i = 0; i < iterations; i++) {
			
			fos.write(buf);
			
			totalBytesWritten += buf.length;
			count++;
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
	                    							   totalBytesWritten,
	                    							   buf.length,
	                    							   buf.length,
	                    							   count,
	                    							   CounterType.BYTES_WRITE);	
			}
					
		}
	
		Assert.assertEquals(totalBytesWritten, fileByteArray.length());
				
		fos.close();
		
		TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
		                    				   totalBytesWritten,
		                    				   buf.length,
		                    				   buf.length,
		                    				   count,
		                    				   CounterType.BYTES_WRITE);
	}
	
	@Test
    public void testWriteByteArrayIntInt() throws Exception {
        
        byte [] buf = new byte [1024];
        final int iterations = (int) TestUtil.getRandomLongCount(1, 1000);
        
        FileOutputStream fos = new FileOutputStream(fileByteArrayIntInt);
        
        int totalBytesWritten = 0;
        int count = 0;
        
        for (int i = 0; i < iterations; i++) {
            
            fos.write(buf, 0, buf.length);
            
            totalBytesWritten += buf.length;
            count++; 
            
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(fileByteArrayIntInt.getCanonicalPath(),
                        totalBytesWritten, buf.length, buf.length, count, CounterType.BYTES_WRITE);	
			}
                   
        }
    
        Assert.assertEquals(totalBytesWritten, fileByteArrayIntInt.length());
                
        fos.close();
        
        TestUtil.assertFileIOCountSingleHandle(fileByteArrayIntInt.getCanonicalPath(),
                            totalBytesWritten, buf.length, buf.length, count, CounterType.BYTES_WRITE);
    }
	
	@After
	public void cleanUp(){
		
		assertTrue(fileByte.delete());
		assertTrue(fileByteArray.delete());
		assertTrue(fileByteArrayIntInt.delete());
		
	}

}
