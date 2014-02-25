package com.sap.tools.jpicus.tests.filehandles;

import java.io.File;
import java.io.RandomAccessFile;

import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;

public class RandomAccessFileTest {
	
	private final static String [] MODES = {"r", "rw", "rws" , "rwd"};
	
	private File file;
	String canonicalPath;
	
	
	
	@Before
	public void prepare() throws Exception{
		
		file = TestUtil.createEmptyFile(this.getClass().getName());
		canonicalPath = file.getCanonicalPath();
	
	}

	
	@Test
	public void testConstructor_File() throws Exception {
		
		for(String mode:MODES){
			System.out.println("Testing with mode: " + mode);
			TestUtil.assertClosed(canonicalPath);
			RandomAccessFile rf = null;
			try{
				
				rf = new RandomAccessFile(file, mode);			
				TestUtil.assertOpened(canonicalPath, rf);
				
			} finally {
				if(rf != null){
					rf.close();	
				}
					
			}
			
			TestUtil.assertClosed(canonicalPath);
			System.out.println("Testing OK: " + mode);
		}
		
				
	}

	@Test
	public void testConstructor_String() throws Exception {
		
		for(String mode:MODES){
			System.out.println("Testing with mode: " + mode);
			TestUtil.assertClosed(canonicalPath);
			RandomAccessFile rf = null;
			try{
				
				rf = new RandomAccessFile(canonicalPath, mode);			
				TestUtil.assertOpened(canonicalPath, rf);
				
			} finally {
				if(rf != null){
					rf.close();	
				}	
			}
			
			TestUtil.assertClosed(canonicalPath);
			System.out.println("Testing OK: " + mode);
		}
		
				
	}
	
//	@After
//	public void cleanUp(){
//		TestUtil.printAllHandles();
//	}

}
