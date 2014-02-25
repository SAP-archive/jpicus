
package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;


/**
 *@author Shenol Yousouf
 */
public class FileReaderIOTest {

  File fileByteArray;
  File fileByteArrayIntInt;
  
  @Before
  public void prepare() throws Exception {
  
  	fileByteArray = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 25000000), this.getClass().getName());
  	fileByteArrayIntInt = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 25000000), this.getClass().getName());
  	
  }

  @After
  public void cleanUp(){
  	
  	assertTrue(fileByteArray.delete());
  	assertTrue(fileByteArrayIntInt.delete());
  	
  }

  @Test
  public void testReadByteArray() throws Exception {
  	
  	char [] buf = new char [1024];
  	
  	//FileInputStream fr = new FileInputStream(fileByteArray);
  	FileReader fr  = new FileReader(fileByteArray);
  	
  	int totalBytesRead = 0;
  	int minBytesRead = buf.length;
  	int maxBytesRead = 0;
  	int count = 0;
  	
  	while(true){
  		
  		final int bytesRead = fr.read(buf);
  		
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
  		
  		 // TODO: fails with: "total expected:<1024.0> but was:<8192.0>" (memory page size ?)
  		if(!TestUtil.isBoostPerformanceEnabled()){
  		
  			TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
 				   totalBytesRead,
 				   maxBytesRead,
 				   minBytesRead,
 				   count,
 				   CounterType.BYTES_READ);

  		}
  		
  	
  	
  	}
  
  	fr.close();
  	
  	Assert.assertEquals(totalBytesRead, fileByteArray.length());
  	
	TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
										   totalBytesRead,
										   maxBytesRead,
										   minBytesRead,
										   count,
										   CounterType.BYTES_READ);
	
  	
  	
  }
  
  @Test
  public void testReadByteArrayIntInt() throws Exception {
    
    char [] buf = new char [1024];
    
    //FileInputStream fr = new FileInputStream(fileByteArray);
    FileReader fr  = new FileReader(fileByteArray);
    
    int totalBytesRead = 0;
    int minBytesRead = buf.length;
    int maxBytesRead = 0;
    int count = 0;
    
    while(true){
        
        final int bytesRead = fr.read(buf, 0, buf.length);
        
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
        
         // TODO: fails with: "total expected:<1024.0> but was:<8192.0>" (memory page size ?)
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
			   maxBytesRead,
			   minBytesRead,
			   count,
			   CounterType.BYTES_READ);

    
    fr.close();
    
  }
}
