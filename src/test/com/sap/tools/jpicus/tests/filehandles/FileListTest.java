package com.sap.tools.jpicus.tests.filehandles;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import com.sap.tools.jpicus.tests.TestUtil;

public class FileListTest {

	private static String testFileName = "FileListTest";
	private static File testDir;
	private static String canonicalPath;
	
	private static int fileCount = 2500;
	private static long totalTimeMeasured = 0;
	private static int totalFilesCount = 0;
	
	@BeforeClass
	public static void prepare() throws IOException {
		
		testDir = new File(TestUtil.getTestDir(), TestUtil.getRandomFileName(testFileName));
				
		if (!testDir.mkdir()) {
			fail("Folder " + testDir.getAbsolutePath() + " cannot be created.");
		}
		
		TestUtil.createEmptyFiles(testDir, testFileName, fileCount);
		
		canonicalPath = testDir.getCanonicalPath();
	}
	
	@Test
	public void testList() {
		long startTime = System.nanoTime();
		totalFilesCount += testDir.list().length;
		totalTimeMeasured += System.nanoTime() - startTime;
		
		TestUtil.assertListOperations(canonicalPath, totalFilesCount, totalTimeMeasured);
	}
	
	@Test
	public void testListFiles() {
		
		long startTime = System.nanoTime();
		totalFilesCount += testDir.listFiles().length;
		totalTimeMeasured += System.nanoTime() - startTime;
		
		TestUtil.assertListOperations(canonicalPath, totalFilesCount, totalTimeMeasured);
		
	}
	
	@Test
	public void testListWithFilenameFilter() {
		
		long startTime = System.nanoTime();
		totalFilesCount += testDir.list(new MyFilenameFilter()).length;
		totalTimeMeasured += System.nanoTime() - startTime;
		
		TestUtil.assertListOperations(canonicalPath, totalFilesCount, totalTimeMeasured);
	}
	
	@Test
	public void testListFileWithFileFilter() {
		
		long startTime = System.nanoTime();
		totalFilesCount += testDir.listFiles(new MyFileFilter()).length;
		totalTimeMeasured += System.nanoTime() - startTime;
		
		TestUtil.assertListOperations(canonicalPath, totalFilesCount, totalTimeMeasured);
		
	}

	@Test
	public void  testListFilesWithFilenameFilter() {
		
		long startTime = System.nanoTime();
		totalFilesCount += testDir.listFiles(new MyFilenameFilter()).length;
		totalTimeMeasured += System.nanoTime() - startTime;
		
		TestUtil.assertListOperations(canonicalPath, totalFilesCount, totalTimeMeasured);
		
	}
	
	@AfterClass
	public static void cleanUp() {
		if (testDir.exists()) {
			if (testDir.isDirectory()) {
				TestUtil.cleanFolderContents(testDir);
			}
			if (!testDir.delete()) {
				fail("Test folder " + testDir.getAbsolutePath() + " cannot be deleted.");
			}
		}
	}
	
	@Ignore
	class MyFilenameFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			
			if (name.contains(testFileName)) {
				return true;
			}
			
			return false;
		}
		
	}
	
	@Ignore
	class MyFileFilter implements FileFilter {

		public boolean accept(File pathname) {
			
			if (pathname.getAbsolutePath().contains(testFileName)) {
				return true;
			}
			
			return false;
		}
		
	}
}
