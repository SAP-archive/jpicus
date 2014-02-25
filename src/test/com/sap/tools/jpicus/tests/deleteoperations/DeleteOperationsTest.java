package com.sap.tools.jpicus.tests.deleteoperations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;

public class DeleteOperationsTest {

	private File file;
	String canonicalPath;
	
	
	private void prepare() throws Exception{
		
		file = TestUtil.createFile(1000, this.getClass().getName());
		canonicalPath = file.getCanonicalPath();
	
	}

	
	@Test
	public void testSuccessfulDelete() throws Exception {
		
		prepare();
		
		assertTrue(file.delete());
		
		TestUtil.assertDeleteOperations(canonicalPath,1,0 );
		
		
	}
	
	@Test
	public void testDeleteOfNonExistingFile() throws Exception {
		
		File nonExisting = TestUtil.getNonExistingFile();
		assertFalse(nonExisting.delete());
		TestUtil.assertDeleteOperations(nonExisting.getCanonicalPath(), 0, 1);
	}

	@Test
	public void testUnsuccessfulDelete() throws Exception {
		
		prepare();
	
		RandomAccessFile fis = new RandomAccessFile(file, "rw");
		
		if(TestUtil.runningOnWindows()){
			assertFalse(file.delete());	
		} else {
			assertTrue(file.delete());
		}
		
		
		fis.close();
		
		if(TestUtil.runningOnWindows()){
			assertTrue(file.delete());	
		} else {
			assertFalse(file.delete());
		}
		TestUtil.assertDeleteOperations(canonicalPath, 1, 1);
		
		TestUtil.assertAssociatedHandles(canonicalPath,2);
		// TODO how to test unsuccessful delete on NIX?
		// maybe launch an external process that locks the file?
		
		
	}
	
	
	@Test
	public void testSingleDeleteOfEmptyFile() throws Exception {
		
		File file = TestUtil.createEmptyFile(this.getClass().getName());
		String canonical = file.getCanonicalPath();
		
		assertTrue( file.delete() );
				
		TestUtil.assertDeleteOperations(canonical, 1, 0);
	}
	
	
	@Test
	public void testMultipleDeleteOfTheSameFile() throws Exception {
		
		String canonical = TestUtil.getRandomFile(this.getClass().getName());
		int randomCount = (int) TestUtil.getRandomLongCount(0, 200);
		
		for(int i = 0 ; i < randomCount; i++){
			File file = new File(canonical);
			file.createNewFile();
			assertTrue( file.delete() );
				
		}
		
		TestUtil.assertDeleteOperations(canonical, randomCount, 0);
	}

	@Test
	public void testMultipleSuccessfulDeletesOfDifferentFiles() throws Exception {
		
		int randomCount = (int) TestUtil.getRandomLongCount(0, 200);
		Set<String> canonicals = new HashSet<String>(randomCount);
		
		for(int i = 0; i< randomCount; i++ ){ 
			
			File file = TestUtil.createEmptyFile(this.getClass().getName());
			canonicals.add(file.getCanonicalPath());
			assertTrue("Couldn't delete: " + file.getCanonicalPath() ,file.delete());
		}
		
		for(String canonical: canonicals){
			TestUtil.assertDeleteOperations(canonical, 1, 0);
		}
		
		
	}

	// TODO: test multiple unsuccessful deletes of the same file
	@Test
	public void testMultipleUnsuccessfulDeleteOfTheSameFile() throws Exception {
	  
	  String canonical = TestUtil.getRandomFile(this.getClass().getName());
      int randomCount = (int) TestUtil.getRandomLongCount(0, 200);
          
      File file = new File(canonical);
      file.createNewFile();
      RandomAccessFile fis = new RandomAccessFile(file, "rw");
      
      if(TestUtil.runningOnWindows()){
        for(int i = 0 ; i < randomCount; i++){
            assertFalse( file.delete() );            
        }
        
        TestUtil.assertDeleteOperations(canonical, 0, randomCount);
      } else {
        assertTrue(file.delete());
      }      
      
      fis.close();
      
      if(TestUtil.runningOnWindows()){
          assertTrue(file.delete());  
          TestUtil.assertDeleteOperations(canonical, 1, randomCount);   
      } else {
          assertFalse(file.delete());
          TestUtil.assertDeleteOperations(canonical, 1, 1);   
      }   
	}
	
	// TODO: test  unsuccessful deletes of the multiple file
	@Test
	public void testMultipleUnsuccessfulDeletesOfDifferentFiles() throws Exception {
      
      int randomCount = (int) TestUtil.getRandomLongCount(0, 200);
      Set<String> canonicals = new HashSet<String>(randomCount);
      
      for(int i = 0; i< randomCount; i++ ){ 
          
          File file = TestUtil.createEmptyFile(this.getClass().getName());
          canonicals.add(file.getCanonicalPath());
          RandomAccessFile fis = new RandomAccessFile(file, "rw");
          
          if(TestUtil.runningOnWindows()){
              assertFalse(file.delete()); 
          } else {
              assertTrue(file.delete());
          }
          
          fis.close();          
          
          if(TestUtil.runningOnWindows()){
              assertTrue(file.delete());  
          } else {
              assertFalse(file.delete());
          }
      }
      
      for(String canonical: canonicals){
          TestUtil.assertDeleteOperations(canonical, 1, 1);
      }            
  }
	
}