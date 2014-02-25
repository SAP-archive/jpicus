
package com.sap.tools.jpicus.tests.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarFile;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.tests.TestUtil;


/**
 *@author Shenol Yousouf
 */
public class JarFileIOTest {

  int length = 10000000;
  File jarFile;
  File jarFile2;
  
  @Before
  public void prepare() throws Exception {

      jarFile = TestUtil.createJarFile(length, this.getClass().getName());
      jarFile2 = TestUtil.createJarFile(length, this.getClass().getName());
      
  }
  
  @Test
  public void testReadSingleEntry() throws Exception {
      
      byte [] buf = new byte [1024];
      
      JarFile jar = new JarFile(jarFile);
      InputStream is = jar.getInputStream(jar.getEntry(TestUtil.DEFAULT_ZIP_ENTRY));
      
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
          count++;
          totalBytesRead += bytesRead;    
          
          if(!TestUtil.isBoostPerformanceEnabled()){
        	  TestUtil.assertFileIOCountSingleHandle(jarFile.getCanonicalPath(), totalBytesRead, maxBytesRead, minBytesRead, count,CounterType.BYTES_READ);     
          }
      }
  
      Assert.assertEquals(totalBytesRead, length);
      
      
      TestUtil.assertFileIOCountSingleHandle(jarFile.getCanonicalPath(), totalBytesRead, buf.length, minBytesRead, count, CounterType.BYTES_READ);
            
      is.close();
      jar.close();
  }
  
  @Test
  public void testReadSingleEntry2() throws Exception {
      
      byte [] buf = new byte [1024];
      
      JarFile jar = new JarFile(jarFile2);
      InputStream is = jar.getInputStream(jar.getEntry(TestUtil.DEFAULT_ZIP_ENTRY));
      
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
          count++;
          totalBytesRead += bytesRead;    
          if(!TestUtil.isBoostPerformanceEnabled()){
        	  TestUtil.assertFileIOCountSingleHandle(jarFile2.getCanonicalPath(), totalBytesRead, maxBytesRead, minBytesRead, count, CounterType.BYTES_READ);     
          }
      }
  
      Assert.assertEquals(totalBytesRead, length);
      
      TestUtil.assertFileIOCountSingleHandle(jarFile2.getCanonicalPath(), totalBytesRead, buf.length, minBytesRead,count, CounterType.BYTES_READ);
      
      is.close();
      jar.close();
  }
  
  
  // TODO: test with more entries
  // TODO: test entries inside folders
  
  @After
  public void cleanUp(){
      
      assertTrue(jarFile.delete());
      assertTrue(jarFile2.delete());
      
  }

}
