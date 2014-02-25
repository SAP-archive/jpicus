package com.sap.tools.jpicus.tests.filehandles;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;

public class FileInputStreamTest {
	
	private File file;
	String canonicalPath;
	
	@Before
	public void prepare() throws Exception{
		
		file = TestUtil.createEmptyFile(this.getClass().getName());
		canonicalPath = file.getCanonicalPath();
	
	}

	
	@Test
	public void testConstructor_File() throws Exception {
		
		
		TestUtil.assertClosed(canonicalPath);
		FileInputStream fis = null;
		try{
			
			fis = new FileInputStream(file);			
			TestUtil.assertOpened(canonicalPath, fis);
			
		} finally {
			fis.close();	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}
	
	@Test
	public void testConstructor_String() throws Exception {
	
		TestUtil.assertClosed(canonicalPath);
		FileInputStream fis = null;
		try{
			
			fis = new FileInputStream(canonicalPath);			
			TestUtil.assertOpened(canonicalPath, fis);
			
		} finally {
			fis.close();	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}
	
	@Test
	public void testConstructor_FD() throws Exception {
	
		TestUtil.assertClosed(canonicalPath);
		FileInputStream fis = null;
		try{
			
			fis = new FileInputStream(FileDescriptor.out);			
			TestUtil.assertClosed(canonicalPath);
			
		} finally {
			fis.close();	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}
	
//	@After
//	public void cleanUp(){
//		TestUtil.printAllHandles();
//	}

}
