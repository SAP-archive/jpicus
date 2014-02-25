
package com.sap.tools.jpicus.tests.filehandles;

import java.io.File;
import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;


/**
 *@author Shenol Yousouf
 */
public class JarFileTest {
  
  private File file;
  String canonicalPath;
  
  
  
  @Before
  public void prepare() throws Exception {
      
      file = TestUtil.createJarFile(100, this.getClass().getName());
      canonicalPath = file.getCanonicalPath();
  
  }

  
  @Test
  public void testConstructor_File() throws Exception {
      
      JarFile jar = null;
      
      TestUtil.assertClosed(canonicalPath);
      
      try{
              
          jar = new JarFile(this.file);           
          TestUtil.assertOpened(canonicalPath, jar);
              
      } finally {
          if(jar != null){
              jar.close();    
          }
                  
      }
          
      TestUtil.assertClosed(canonicalPath);   
              
  }
  
  @Test
  public void testConstructor_String() throws Exception {
      
      JarFile jar = null;
      
      TestUtil.assertClosed(canonicalPath);
      
      try{
              
          jar = new JarFile(this.canonicalPath);          
          TestUtil.assertOpened(canonicalPath, jar);
              
      } finally {
          if(jar != null){
              jar.close();    
          }
                  
      }
          
      TestUtil.assertClosed(canonicalPath);   
              
  }
  
      

}
