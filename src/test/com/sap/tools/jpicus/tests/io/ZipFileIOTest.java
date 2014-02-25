package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;

public class ZipFileIOTest {

	int length = 10000000;
	File zipFile;
	File zipFile2;
	
	@Before
	public void prepare() throws Exception {

		zipFile = TestUtil.createZipFile(length, this.getClass().getName());
		zipFile2 = TestUtil.createZipFile(length, this.getClass().getName());
		
	}
	
	@Test
	public void testReadSingleEntry() throws Exception {
		
		byte [] buf = new byte [1024];
		
		ZipFile zip = new ZipFile(zipFile);
		InputStream is = zip.getInputStream(zip.getEntry(TestUtil.DEFAULT_ZIP_ENTRY));
		
		int totalBytesRead = 0;
		int minBytesRead = buf.length;
		int maxBytesRead = 0;
		int count = 0;
		while(true){
			
			final int bytesRead = is.read(buf);
			
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
			count++;
			
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(zipFile.getCanonicalPath(), totalBytesRead, maxBytesRead, minBytesRead, count,CounterType.BYTES_READ);	
			}
					
		}
	
		Assert.assertEquals(totalBytesRead, length);
		
		TestUtil.assertFileIOCountSingleHandle(zipFile.getCanonicalPath(), totalBytesRead, buf.length, minBytesRead,count, CounterType.BYTES_READ);
		
		is.close();
		zip.close();
	}
	
	@Test
    public void testReadSingleEntry2() throws Exception {
        
        byte [] buf = new byte [1024];
        
        ZipFile zip = new ZipFile(zipFile2);
        InputStream is = zip.getInputStream(zip.getEntry(TestUtil.DEFAULT_ZIP_ENTRY));
        
        int totalBytesRead = 0;
        int minBytesRead = buf.length;
        int maxBytesRead = 0;
        int count = 0;
        
        while(true){
            
            final int bytesRead = is.read(buf, 0, buf.length);
            
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
            count ++;
			// this check generates a lot of overhead because a snapshot is created in each iteration
			if(!TestUtil.isBoostPerformanceEnabled()){
				TestUtil.assertFileIOCountSingleHandle(zipFile2.getCanonicalPath(), totalBytesRead, maxBytesRead, minBytesRead, count,CounterType.BYTES_READ);	
			}
                 
        }
    
        Assert.assertEquals(totalBytesRead, length);
        
        TestUtil.assertFileIOCountSingleHandle(zipFile2.getCanonicalPath(), totalBytesRead, buf.length, minBytesRead,count, CounterType.BYTES_READ);
        
        is.close();
        zip.close();
    }
	
	
	@After
	public void cleanUp(){
		
		assertTrue(zipFile.delete());
		assertTrue(zipFile2.delete());
		
	}

}
