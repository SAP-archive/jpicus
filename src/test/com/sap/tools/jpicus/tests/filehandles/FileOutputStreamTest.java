package com.sap.tools.jpicus.tests.filehandles;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;

public class FileOutputStreamTest {
	
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
		FileOutputStream fs = null;
		try{
			
			fs = new FileOutputStream(file);			
			TestUtil.assertOpened(canonicalPath, fs);
			
		} finally {
			if(fs != null){fs.close();}	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}

	
	@Test
	public void testConstructor_File_append() throws Exception {
		
		
		TestUtil.assertClosed(canonicalPath);
		FileOutputStream fs = null;
		try{
			
			fs = new FileOutputStream(file, true);			
			TestUtil.assertOpened(canonicalPath, fs);
			
		} finally {
			if(fs != null){fs.close();}	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}


	@Test
	public void testConstructor_File_dont_append() throws Exception {
		
		
		TestUtil.assertClosed(canonicalPath);
		FileOutputStream fs = null;
		try{
			
			fs = new FileOutputStream(file, false);			
			TestUtil.assertOpened(canonicalPath, fs);
			
		} finally {
			if(fs != null){fs.close();}	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}

	
	
	@Test
	public void testConstructor_String() throws Exception {
	
		TestUtil.assertClosed(canonicalPath);
		FileOutputStream fs = null;
		try{
			
			fs = new FileOutputStream(canonicalPath);			
			TestUtil.assertOpened(canonicalPath, fs);
			
		} finally {
			if(fs != null){fs.close();}	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}
	
	@Test
	public void testConstructor_String_append() throws Exception {
	
		TestUtil.assertClosed(canonicalPath);
		FileOutputStream fs = null;
		try{
			
			fs = new FileOutputStream(canonicalPath, true);			
			TestUtil.assertOpened(canonicalPath, fs);
			
		} finally {
			if(fs != null){fs.close();}	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}
	
	@Test
	public void testConstructor_String_dont_append() throws Exception {
	
		TestUtil.assertClosed(canonicalPath);
		FileOutputStream fs = null;
		try{
			
			fs = new FileOutputStream(canonicalPath, false);			
			TestUtil.assertOpened(canonicalPath, fs);
			
		} finally {
			if(fs != null){fs.close();}	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}
	
	@Test
	public void testConstructor_FD() throws Exception {
	
		TestUtil.assertClosed(canonicalPath);
		FileOutputStream fs = null;
		try{
			
			fs = new FileOutputStream(FileDescriptor.out);			
			TestUtil.assertClosed(canonicalPath);
			
		} finally {
			if(fs != null){fs.close();}	
		}
		
		TestUtil.assertClosed(canonicalPath);
		
	}
	
//	@After
//	public void cleanUp(){
//		TestUtil.printAllHandles();
//	}

}
