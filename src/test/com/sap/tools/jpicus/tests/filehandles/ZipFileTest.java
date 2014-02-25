package com.sap.tools.jpicus.tests.filehandles;

import java.io.File;
import java.util.zip.ZipFile;

import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;

public class ZipFileTest {
	
	private File file;
	String canonicalPath;
	
	
	
	@Before
	public void prepare() throws Exception {
		
		file = TestUtil.createZipFile(100, this.getClass().getName());
		canonicalPath = file.getCanonicalPath();
	
	}

	
	@Test
	public void testConstructor_File() throws Exception {
		
		ZipFile zip = null;
		
		TestUtil.assertClosed(canonicalPath);
		
		try{
				
			zip = new ZipFile(this.file);			
			TestUtil.assertOpened(canonicalPath, zip);
				
		} finally {
			if(zip != null){
				zip.close();	
			}
					
		}
			
		TestUtil.assertClosed(canonicalPath);	
				
	}
	
	@Test
	public void testConstructor_String() throws Exception {
		
		ZipFile zip = null;
		
		TestUtil.assertClosed(canonicalPath);
		
		try{
				
			zip = new ZipFile(this.canonicalPath);			
			TestUtil.assertOpened(canonicalPath, zip);
				
		} finally {
			if(zip != null){
				zip.close();	
			}
					
		}
			
		TestUtil.assertClosed(canonicalPath);	
				
	}
	
		

}
