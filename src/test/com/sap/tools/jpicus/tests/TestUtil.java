package com.sap.tools.jpicus.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Ignore;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.client.Counter;
import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.client.DeleteOperation;
import com.sap.tools.jpicus.client.HandleDescriptor;
import com.sap.tools.jpicus.client.Snapshot;
import com.sap.tools.jpicus.impl.serializable.HandleDescriptorImpl;
import com.sap.tools.jpicus.impl.util.Deserializer;
import com.sap.tools.jpicus.impl.util.OSType;
import com.sap.tools.jpicus.impl.util.Util;

@Ignore
public class TestUtil {

	private static boolean boostPerformance = false;
	private static boolean boostPerformanceCalculated = false;
	public static final String DEFAULT_ZIP_ENTRY = "default.txt";
	private static final String BOOST_PERFORMANCE = "com.sap.tools.jpicus.test.boostperformance";

	public static byte [] getBytes(String className) throws IOException {

		ClassLoader loader = TestUtil.class.getClass().getClassLoader();
		InputStream in;



		if(loader == null){
			System.out.println("Get system resource as stream: " + className);
			in = ClassLoader.getSystemResourceAsStream(className);
		} else {
			System.out.println("Get resource as stream: " + className);
			in = loader.getResourceAsStream(className);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte [] buf = new byte [100];
		while( true ){
			int len = in.read(buf);
			if(len < 0){
				break;
			}
			out.write(buf,0,len);
		}

		in.close();

		return out.toByteArray();

	}

	public static char [] toCharArray(byte [] classBytes){

		ClassReader reader = new ClassReader(classBytes);

		CharArrayWriter buf = new CharArrayWriter();
		TraceClassVisitor tracer = new TraceClassVisitor( new PrintWriter(buf) );

		reader.accept(tracer,0);

		return buf.toCharArray();
	}

	/**
	 * Check the class validity. The method will throw a runtime exception if the
	 * class is not valid. The method will also trace the class in a human readable
	 * format to the standard system out. If there is an error in the class the
	 * tracing will cease at the erronous point
	 * @param classBytes
	 */
	public static void checkClassValidity(byte[] classBytes) {

		System.out.println("Checking validity of: " + classBytes.length);
		ClassReader reader = new ClassReader(classBytes);

		CheckClassAdapter checker = new CheckClassAdapter(new ClassWriter(0));
		TraceClassVisitor tracer = new TraceClassVisitor( checker, new PrintWriter(System.out) );

		reader.accept(tracer,0);

	}

	public static File createEmptyFile(String testName) throws IOException {

		File file;
		do {
			file  = new File(getTestDir(), getRandomFileName(testName));
		} while (file.exists());

		file.createNewFile();

		if(!file.exists()){
			fail("The file " + file + " does not exist");
		}

		if(!file.isFile()){
			fail("The file " + file + " is a directory");
		}

		if(!file.canRead()){
			fail("The file " + file + " can't be read");
		}


		return file;

	}

	public static void createEmptyFiles(File testDir, String testName, int fileCount) throws IOException {

		if (fileCount <= 0) {
			fail("expected positive file count argument; the actual provided count is " + fileCount);
		}

		for(int i=0; i < fileCount; i++) {
			File file;

			// generate a file name which does not exists yet
			do {
				file  = new File(testDir, getRandomFileName(testName));
			} while (file.exists());

			file.createNewFile();
			if (!file.exists()) {
				fail("The file " + file.getAbsolutePath() + " does not exist");
			}
		}
	}

	/**
	 * Clean folder's contents in depth. Note that the folder itself is not removed.
	 * @param folder folder whose contents are to be removed
	 * @return total count of listed files/folders
	 */
	public static int cleanFolderContents(File folder) {

		int result  = 0;

		if (!folder.exists() || !folder.isDirectory()) {
			fail(folder.getAbsolutePath() + " either does not exist or is not a directory.");
		}

		File[] folderContents = folder.listFiles();
		result += folderContents.length;
		for(File file : folderContents) {
			if (file.isDirectory()) {
				result += cleanFolderContents(file);
			}
			if (!file.delete()) {
					fail("File " + file.getAbsolutePath() + " cannot be deleted.");
			}
		}

		return result;
	}

	public static File getTestDir(){

		File currentDir = new File(System.getProperty("user.dir") );
		if(!currentDir.exists()){
			currentDir = new File(".");
		}

		String sep = File.separator;
		File testDir = new File(currentDir, "dist" + sep + "testresults" + sep + "test_temp_dir");
		testDir.mkdirs();

		return testDir;

	}

	public static Set<? extends HandleDescriptor> getOpenHandles(String canonicalPath) throws Exception {

		Set<? extends HandleDescriptor> result = getHandles(canonicalPath);
		if(result == null || result.size() == 0){
			return result;
		}
		// remove the ones that are closed
		Iterator<? extends HandleDescriptor> iterator = result.iterator();
		while(iterator.hasNext()){
			HandleDescriptor descriptor = iterator.next();
			if(descriptor.getClosingThreadInfo() != null){
				iterator.remove();
			}
		}
		return result;


	}

	public static Set<HandleDescriptor> getHandles(String canonicalPath) throws Exception {

		Map<String, Set<HandleDescriptor>> handles = getAllHandles();
		Set<HandleDescriptor> result = handles.get(canonicalPath);

		return result;

	}

	public static Map<String, Set<HandleDescriptor>> getAllHandles() throws Exception {

		if(System.getProperty("jpicus_functional_tests") != null){
			Snapshot snapshot = new Deserializer<Snapshot>().deserialize(Agent.getAgentManager().createSnapshot());
			return snapshot.getHandles();
		} else {
			return Agent.getOperationsState().getHandles();
		}


	}

	public static void printAllHandles() throws Exception  {

		Map<String, Set<HandleDescriptor>> handles = getAllHandles();

		String sep = System.getProperty("line.separator");
		StringBuilder buf = new StringBuilder(10000);
		buf.append("All Handles: ").append(sep);

		for(String key: handles.keySet()){
			buf.append(key).append(sep);
			Set<? extends HandleDescriptor> descriptors = handles.get(key);
			for(HandleDescriptor d: descriptors){
				buf.append(d).append(sep);
			}
		}
		System.out.println(buf);


	}


	public static void assertClosed(String canonicalPath) throws Exception {

		Set<? extends HandleDescriptor> openHandles = TestUtil.getOpenHandles(canonicalPath);
		if(openHandles != null && openHandles.size() != 0){
			fail("The file shall not be opened: " + openHandles );
		}

	}

	public static void assertOpened(String canonicalPath, Object handle) throws Exception {

		Set<? extends HandleDescriptor> openHandles = TestUtil.getOpenHandles(canonicalPath);

		assertNotNull(openHandles);
		assertEquals(1, openHandles.size());

		HandleDescriptorImpl descriptor = (HandleDescriptorImpl)openHandles.iterator().next();

		Object capturedHandle = descriptor.getHandle();
		if(System.getProperty("jpicus_functional_tests") != null){

			assertNull("The handle shall be null but it is: " + capturedHandle, capturedHandle);

		} else {

			if( capturedHandle != handle){

				fail("The handle exists but it is not the same");
			}
		}


	}

	/**
	 * Assert that for this file <tt>canonicalPath</tt> the Agent has counted
	 * <tt>fileCount</tt> files for no more than <tt> timeMeasurement</tt> ns.
	 */
	public static void assertListOperations(String canonicalPath, int fileCount, long timeMeasurement) {
		// TODO: implementation
	}

	/**
	 * Assert that the Agent has counted <tt>bytesTransferred</tt> bytes transferred between file channels
	 * from file <tt>canonicalPathRead</tt> to file <tt>canonicalPathWrite</tt> during file copy
	 * for no more than <tt> timeMeasurement</tt> ns.
	 */
	public static void assertFileChannelTransfer(final String canonicalPathRead,
												 final String canonicalPathWrite,
												 final long total,
												 final long time )throws Exception {

		Set<HandleDescriptor> readHandles = getHandles(canonicalPathRead);
		assertEquals("Read handles size", 1, readHandles.size());

		Set<HandleDescriptor> writeHandles = getHandles(canonicalPathWrite);
		assertEquals("Write handles size", 1, writeHandles.size());

		HandleDescriptor readDescriptor = readHandles.iterator().next();
		assertNotNull("Read handle descriptor", readDescriptor);


		HandleDescriptor writeDescriptor = writeHandles.iterator().next();
		assertNotNull("Write handle descriptor", writeDescriptor);

		System.out.println("Read descriptor: " + readDescriptor);
		System.out.println("Write descriptor: " + writeDescriptor);

		Counter readByteCounter = readDescriptor.getCounter(CounterType.BYTES_READ);
		assertNotNull("Read byte counter", readByteCounter);

		Counter readTimeCounter = readDescriptor.getCounter(CounterType.TIME_READ);
		assertNotNull("Read time counter", readTimeCounter);


		Counter writeByteCounter = writeDescriptor.getCounter(CounterType.BYTES_WRITE);
		assertNotNull("Write byte counter", writeByteCounter);

		Counter writeTimeCounter = writeDescriptor.getCounter(CounterType.TIME_WRITE);
		assertNotNull("Write time counter", writeTimeCounter);



		System.out.println("Read byte counter: " + readByteCounter );
		System.out.println("Read time counter: " + readTimeCounter );

		System.out.println("Write byte counter: " + writeByteCounter );
		System.out.println("Write time counter: " + writeTimeCounter );

		assertEquals("Read total", total, readByteCounter.getTotal(),0);
		assertEquals("Write total", total, writeByteCounter.getTotal(),0);

		assertEquals("Read max", total, readByteCounter.getMax(),0);
		assertEquals("Write max", total, writeByteCounter.getMax(),0);

		assertEquals("Read min", total, readByteCounter.getMin(),0);
		assertEquals("Write min", total, writeByteCounter.getMin(),0);

		assertEquals("Read count",1, readByteCounter.getCount());
		assertEquals("Write count",1, writeByteCounter.getCount());

		assertTrue("Time for reading is greater than expected", readTimeCounter.getTotal() < time);
		assertTrue("Time for writing is greater than expected", writeTimeCounter.getTotal() < time);

	}

	/**
	 * Assert that <tt>bytesCount</tt> bytes were written to disk when
	 * method FileChannel/MappedByteBuffer.force() was called
	 * for file <tt>canonicalPath</tt>.
	 */
	public static void assertFileIOCount(String canonicalPath, long bytesCount, final CounterType counterType) {
		// TODO: add time count ? (may not be necessary because only changes are forced to the file system)
		// TODO: implementation
	}

	public static String getRandomFile(String testName) throws Exception {

		File parent = getTestDir();
		File result = new File(parent, getRandomFileName(testName) );
		return result.getCanonicalPath();
	}

	public static String getRandomFileName(String testName) {
		return testName + System.currentTimeMillis() + "_" + Math.random();
	}

	/**
	 * Creates a file with the given size (as returned by File.length) and unspecified content
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public static File createFile(final int size, String testName) throws Exception {

		File file = new File(getRandomFile(testName));
		byte [] buf = new byte [8192];
		FileOutputStream out = new FileOutputStream(file);
//		System.out.println("createFile: " + size);

		int remaining = size;
		while(true){
			if(remaining <= buf.length){
//				System.out.println("write: " + remaining);
				out.write(buf,0,remaining);
				break;
			}

//			System.out.println("write: " + buf.length);
			out.write(buf,0,buf.length);
			remaining -= buf.length;
		}

		out.close();

		if(!file.exists()){
			fail("The file " + file + " does not exist");
		}

		if(!file.isFile()){
			fail("The file " + file + " is a directory");
		}

		if(!file.canRead()){
			fail("The file " + file + " can't be read");
		}

		if(! (file.length() == size)){
			fail("The expected file size " + size + " doesn't match the real size " + file.length());
		}

//		String oldCanonical = file.getCanonicalPath();

	    File result = new File(getRandomFile(testName));
//	    String newCanonical = result.getCanonicalPath();

//	    System.out.println("Going to rename " + oldCanonical + " to " + newCanonical);

	    boolean success = file.renameTo(result);

	    if (!success) {
	        fail("Failed to rename the file " + file + " to " + result);
	    } else {
//	    	System.out.println("Successfully renamed " + oldCanonical + " to " + newCanonical);
	    }

		return result;

	}

	/**
	 * Creates a zip file with the given uncompressed size (as returned by File.length) and unspecified content
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public static File createZipFile(int uncompressedSize, String testName) throws Exception {

		File file = createFile(uncompressedSize, testName);
		File zipFile = new File(getRandomFile(testName));

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		out.putNextEntry(new ZipEntry(DEFAULT_ZIP_ENTRY));

		FileInputStream fis = new FileInputStream(file);
		byte [] buf = new byte [8192];
		while(true){
			int bytesRead = fis.read(buf);
			if(bytesRead == -1){
				break;
			}
			out.write(buf, 0, bytesRead);
		}
		out.closeEntry();
		out.close();
		fis.close();

		File result = new File(getRandomFile(testName));
		boolean success = zipFile.renameTo(result);

	    if (!success) {
	        fail("Failed to rename the file " + file + " to " + result);
	    }

		return result;
	}

	/**
     * Creates a jar file with the given uncompressed size (as returned by File.length) and unspecified content
     * @param size size of the generated jar file size
     * @return generated jar file
     * @throws Exception
     */
	public static File createJarFile(int uncompressedSize, String testName) throws Exception {

	  File file = createFile(uncompressedSize, testName);
	  File jarFile = new File(getRandomFile(testName));

	  JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile));
      out.putNextEntry(new JarEntry(DEFAULT_ZIP_ENTRY));

      FileInputStream fis = new FileInputStream(file);
      byte [] buf = new byte [8192];
      while(true){
          int bytesRead = fis.read(buf);
          if(bytesRead == -1){
              break;
          }
          out.write(buf, 0, bytesRead);
      }
      out.closeEntry();
      out.close();
      fis.close();

      File result = new File(getRandomFile(testName));
      boolean success = jarFile.renameTo(result);

      if (!success) {
          fail("Failed to rename the file " + file + " to " + result);
      }

      return result;
	}

	public static void assertDeleteOperations(String canonicalPath, int successfulCount, int failedCount) throws Exception {

		Map<String, Set<DeleteOperation>> operations = null;

		if(System.getProperty("jpicus_functional_tests") != null){
			Snapshot snapshot = new Deserializer<Snapshot>().deserialize(Agent.getAgentManager().createSnapshot());
			operations = snapshot.getDeleteOperations();
		} else {
			operations = Agent.getOperationsState().getDeleteOperations();
		}

		Set<? extends DeleteOperation> operationsSet = operations.get(canonicalPath);

		if(operationsSet == null){
			throw new RuntimeException ("Operations set for canonical: " + canonicalPath + " is null");
		}

		int success = 0;
		int failure = 0;

		for(DeleteOperation operation:operationsSet){

			if(operation.isSuccessful()){
				success++;
			} else {
				failure++;
			}
		}
		assertEquals("Successful operations count does not match: " , successfulCount, success );
		assertEquals("Failed operations count does not match: "  , failedCount, failure );

	}

	/**
	 * Ensure that there is just one handle for the given canonical path and its
	 * metrics for the given counter type correspond to the values passed
	 * @param canonicalPath
	 * @param total
	 * @param max
	 * @param min
	 * @param counterType
	 * @throws Exception
	 */
	public static void assertFileIOCountSingleHandle(final String canonicalPath,
										 final double total,
										 final double max,
										 final double min,
										 final long count,
										 final CounterType counterType) throws Exception {

		Set<HandleDescriptor> handles = getHandles(canonicalPath);

		assertEquals("Handles size", 1, handles.size());

		HandleDescriptor descriptor = handles.iterator().next();

		assertNotNull("Handle descriptor", descriptor);

		Counter counter = descriptor.getCounter(counterType);
		assertNotNull("Counter", counter);


		System.out.println(counter);

		assertEquals("total", total, counter.getTotal(),0);
		assertEquals("max", max, counter.getMax(),0);
		assertEquals("min", min, counter.getMin(),0);
		assertEquals("count", count, counter.getCount());
	}

	/**
	 * Inspect all the handles for this canonical for the given counter type and calculate the
	 * accumulated measurements. Then compare them to the method arguments.
	 * @param canonicalPath
	 * @param total
	 * @param max
	 * @param min
	 * @param counterType
	 * @throws Exception
	 */
	public static void assertFileIOCountMultipleHandles(final String canonicalPath,
										 final double total,
										 final double max,
										 final double min,
										 final long count,
										 final CounterType counterType) throws Exception {

		Set<HandleDescriptor> handles = getHandles(canonicalPath);

		double realTotal = 0;
		double realMax = 0;
		double realMin = Double.MAX_VALUE;
		long realCount = 0;

		for(HandleDescriptor descriptor : handles){
			Counter counter = descriptor.getCounter(counterType);
			if(counter == null){
				continue;
			}
			realTotal += counter.getTotal();

			if(counter.getMax() > realMax){
				realMax = counter.getMax();
			}

			if(counter.getMin() < realMin){
				realMin = counter.getMin();
			}

			realCount += counter.getCount();

		}

		assertEquals("total", total, realTotal,0);
		assertEquals("max", max, realMax,0);
		assertEquals("min", min, realMin,0);
		assertEquals("count", count, realCount);
	}

	public static long getRandomLongCount(long from, long to) {
		return (long)( (to - from)*Math.random() + from );

	}

	public static double getRandomDoubleCount(long from, long to) {
		return  (to - from)*Math.random() + from ;

	}

	public static boolean runningOnWindows() {

		if(OSType.WINDOWS == Util.getOSType()){
			return true;
		}
		return false;
	}

	public static File getNonExistingFile() {

		while(true){
			File file = new File(getRandomFileName("non_existing"));
			if(!file.exists()){
				return file;
			}
		}

	}

	/**
	 * Used in order to boost trade performance for details in order to
	 * improve the dev turnaround time.
	 * @return
	 */
	public static boolean isBoostPerformanceEnabled() {


		if(!boostPerformanceCalculated){

			String prop = System.getProperty(BOOST_PERFORMANCE);
			System.err.println("Boost performance: " + prop);
			if( prop != null && prop.equalsIgnoreCase("true") ){
				boostPerformance = true;
		    	  System.out.println("Performance boost is enabled");
			} else {
		    	  System.out.println("Performance boost is disabled");
			}
			boostPerformanceCalculated = true;
		}


		return boostPerformance;
	}

	// TODO more detailed testing
	public static void assertAssociatedHandles(String canonicalPath, int i) throws Exception {

		Map<String, Set<DeleteOperation>> operations = null;

		if(System.getProperty("jpicus_functional_tests") != null){
			Snapshot snapshot = new Deserializer<Snapshot>().deserialize(Agent.getAgentManager().createSnapshot());
			operations = snapshot.getDeleteOperations();
		} else {
			operations = Agent.getOperationsState().getDeleteOperations();
		}

		Set<? extends DeleteOperation> operationsSet = operations.get(canonicalPath);

		if(operationsSet == null){
			throw new RuntimeException ("Operations set for canonical: " + canonicalPath + " is null");
		}

		int handleCount = 0;
		for(DeleteOperation operation:operationsSet){

			Set<? extends HandleDescriptor> associatedHandles = operation.getAssociatedHandleDescriptors();
			System.out.println(operation);
			if(associatedHandles == null){
				continue;
			}
			handleCount += associatedHandles.size();

		}
		assertEquals("The number of handles associated with this cannonical doesn't match: ",i, handleCount);

	}

	public static void assertDeleteWithMoreInfo(File file) throws Exception {

		if (!file.delete()) {
			String canonicalPath = file.getCanonicalPath();
			Set<? extends HandleDescriptor> handleDescriptors = getOpenHandles(canonicalPath);
			if (handleDescriptors != null && handleDescriptors.size() > 0) {
				for(HandleDescriptor descriptor : handleDescriptors) {
					System.out.println(descriptor);
					StackTraceElement[] stackTrace = descriptor.getOpeningThreadInfo().getStackTrace();
					for(StackTraceElement element : stackTrace) {
						System.out.println(element.toString());
					}
				}
			}

			fail("File " + canonicalPath + " cannot be deleted.");
		}
	}

}
