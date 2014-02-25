
package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;


/**
 *@author Shenol Yousouf
 */
public class BufferedFileOutputStreamIOTest {

  //File fileByte;
  File fileByteArray;
  File fileByteArrayIntInt;
  
  @Before
  public void prepare() throws Exception {

      //fileByte = TestUtil.createEmptyFile(this.getClass().getName());
      fileByteArray = TestUtil.createEmptyFile( this.getClass().getName());
      fileByteArrayIntInt = TestUtil.createEmptyFile(this.getClass().getName());
      
  }

  @After
  public void cleanUp(){
  	
  	//assertTrue(fileByte.delete());
  	assertTrue(fileByteArray.delete());
  	assertTrue(fileByteArrayIntInt.delete());
  	
  }

  @Test
  public void testWriteByteArray() throws Exception {
  	
  	byte [] buf = new byte [1024];
  	final int count = (int) TestUtil.getRandomLongCount(1, 1000);
  	
  	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileByteArray));
  	
  	int totalBytesWritten = 0;
  	
  	for (int i = 0; i < count; i++) {
  		
  		bos.write(buf);
  		
  		totalBytesWritten += buf.length;
  		// TODO: NPE - no counter stored
  		if(!TestUtil.isBoostPerformanceEnabled()){
  			TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
  												   totalBytesWritten,
  												   buf.length,
  												   buf.length,
  												   count,
  												   CounterType.BYTES_WRITE);
  		}
  		
  	}
  
  	bos.close(); // it is necessary to close it here otherwise the file size does not match
  	
  	Assert.assertEquals(totalBytesWritten, fileByteArray.length());
  	  	
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
      final int count = (int) TestUtil.getRandomLongCount(1, 1000);
      
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileByteArrayIntInt));
      
      int totalBytesWritten = 0;
      
      for (int i = 0; i < count; i++) {
          
          bos.write(buf, 0, buf.length);
          
          totalBytesWritten += buf.length;
          
          // TODO: NPE - no counter stored
    		if(!TestUtil.isBoostPerformanceEnabled()){
      			TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
      												   totalBytesWritten,
      												   buf.length,
      												   buf.length,
      												   count,
      												   CounterType.BYTES_WRITE);
      		}

      
      }
  
      bos.close();  // it is necessary to close it here otherwise the file size does not match
      Assert.assertEquals(totalBytesWritten, fileByteArrayIntInt.length());
      
		TestUtil.assertFileIOCountSingleHandle(fileByteArray.getCanonicalPath(),
											   totalBytesWritten,
											   buf.length,
											   buf.length,
											   count,
											   CounterType.BYTES_WRITE);

  
  }
  
  
}
