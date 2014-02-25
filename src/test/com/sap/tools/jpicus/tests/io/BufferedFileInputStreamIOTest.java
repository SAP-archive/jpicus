
package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;


/**
 *@author Shenol Yousouf
 */
public class BufferedFileInputStreamIOTest {
  
  //File fileNoArg;
  File fileByteArray;
  File fileByteArrayIntInt;
  
  @Before
  public void prepare() throws Exception {

     //fileNoArg = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 20000), this.getClass().getName());
      fileByteArray = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 25000000), this.getClass().getName());
      fileByteArrayIntInt = TestUtil.createFile((int)TestUtil.getRandomLongCount(10, 25000000), this.getClass().getName());
      
  }
  
  

  @After
  public void cleanUp(){
  	
  	//assertTrue(fileNoArg.delete());
    assertTrue(fileByteArray.delete());
  	assertTrue(fileByteArrayIntInt.delete());
  	
  }
  
  @Test
  public void testReadByteArray() throws Exception {
    
    byte [] buf = new byte [1024];
    
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileByteArray));
    
    int totalBytesRead = 0;
    int minBytesRead = buf.length;
    int maxBytesRead = 0;
    int count = 0;
    
    while (true) {
      
      final int bytesRead = bis.read(buf);
      
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
   			   buf.length,
   			   minBytesRead,
   			   count,
   			   CounterType.BYTES_READ);
  
      }
    }
    
    bis.close();
    Assert.assertEquals(totalBytesRead, fileByteArray.length());
    
    TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
                        				   totalBytesRead,
                        				   buf.length,
                        				   minBytesRead,
                        				   count,
                        				   CounterType.BYTES_READ);
    
  }  

  //  test case for read(byte[], int, int)
  @Test
  public void testReadByteArrayIntInt() throws Exception {
    
    byte [] buf = new byte [1024];
    
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileByteArrayIntInt));
    
    int totalBytesRead = 0;
    int minBytesRead = buf.length;
    int maxBytesRead = 0;
    int count = 0;
    
    while (true) {
      
      final int bytesRead = bis.read(buf, 0, buf.length);
      
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
   			   buf.length,
   			   minBytesRead,
   			   count,
   			   CounterType.BYTES_READ);
  
      }

    }
    
    bis.close();
    Assert.assertEquals(totalBytesRead, fileByteArrayIntInt.length());
    
    TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
			   totalBytesRead,
			   buf.length,
			   minBytesRead,
			   count,
			   CounterType.BYTES_READ);
    
    
  }  

}
