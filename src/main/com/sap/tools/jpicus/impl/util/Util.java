package com.sap.tools.jpicus.impl.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.serializable.Options;

public class Util {
	
	private static Executor exec = Executors.newSingleThreadExecutor();
	public static final String SEP = System.getProperty("line.separator");

	public static String getCannonical(Object path) {
		
		
		File file = null;
		
		if(path instanceof String){
			file = new File( (String)path);	
		}
		
		if(path instanceof File){
			
			file = (File)path;
		}
		
		if(file != null){
		
			String cannonicalPath;
			
			try {
				cannonicalPath = file.getCanonicalPath();
			} catch (IOException e) {
					
				e.printStackTrace();
				// fall back to the method that does not throw an exception 
				cannonicalPath = file.getAbsolutePath();
			}
			return cannonicalPath;
				
			
		}
		
		if(path instanceof FileDescriptor){
			FileDescriptor fd = (FileDescriptor)path;
			String result = fd.toString();
			return result;
		}
		
		throw new IllegalArgumentException("Unknown path type: " + path);
				
		
		
	}

	
	public static void executeAndWait(final Runnable r){
		final CountDownLatch countDown = new CountDownLatch(1);
		Runnable task = new Runnable(){
			
			public void run() {
				
				try {
					r.run();
				} finally{
					System.err.println("scheduled");
					countDown.countDown();
				}
				
			}
		};
		
		exec.execute(task);
		
		try {
			countDown.await();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			return;
		}
		
	}

	public static StackTraceElement [] cleanStackTrace(
			StackTraceElement[] stackTrace) {
		
		StackTraceElement [] result = new StackTraceElement[stackTrace.length -3];
		System.arraycopy(stackTrace, 3, result, 0, result.length);
		return result;
		
	}

	public static void stackTraceToString(StackTraceElement[] stackTrace, StringBuilder buf) {
		
		String sep = System.getProperty("line.separator");
	
		for (int i = 0; i < stackTrace.length; i++){
			StackTraceElement element = stackTrace[i];
			buf.append(sep + "\tat " + element.toString() );
		}
	
		
		
	}

	public static OSType getOSType(){
				
		String os = System.getProperty("os.name");
		if(os.contains("nux") || os.contains("nix") ){
			
			return OSType.NIX;
		}
		
		if(os.contains("laris")){
			return OSType.SOLARIS;
		}
		
		if(os.contains("indows")){
			return OSType.WINDOWS;
		}
		return OSType.UNKNOWN;
	}

	

	public static String getExternalOwners(String canonical) {
		
		
		// *nix systems normally have lsof
		if( OSType.WINDOWS != getOSType()){
			
//			try {
//				// TODO create synchronous process executor utility
//				Process p = Runtime.getRuntime().exec("lsof | grep " + canonical);
//				return null;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//				
//			}	
		} else {
			// TODO windows
//			try {
//				Process p = Runtime.getRuntime().exec("handle.exe");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
		}
		
		return null;
	}
	
	/**
	 * Iterate over the classes and for each of them
	 * 1.Find the all parents up to the limit
	 * 2.Add them to the result
	 * @param limit
	 * @param classes
	 */
	public static void addSuperClasses(Class<?> limit,	Set<Class<?>> classes) {
	
		Set<Class<?>> parents = new HashSet<Class<?>>(4);
		
		for(Class<?> cls:classes){
			// find all parents of this class until the limiting class is reached
			while(true){
				Class<?> parent = cls.getSuperclass();
				if(parent == null || parent.equals(limit)){
					break;
				}
				parents.add(parent);
				cls = parent;
			}
		}
		if(Options.getInstance().isVerbose()){
			System.out.println("Util.addSuperClasses(). Source= " + classes + ", parents=" + parents );
		}
		
		classes.addAll(parents);
		
	}
	
	/**
	 * Since the particular implementations of FileChannel are not known in
	 * advance, find out which are they
	 * @return
	 * @throws IOException
	 */
	public static Map<Class<?>,Set<Class<?>>> getNIOPrivateImplementationClasses() throws IOException {
		
		Map<Class<?>, Set<Class<?>>> result = new HashMap<Class<?>, Set<Class<?>>>(2);
		result.put(FileChannel.class, new HashSet<Class<?>>(4));
		result.put(MappedByteBuffer.class, new HashSet<Class<?>>(4));
		
		File file = new File(Agent.CALIBRATION_FILE);
		
		if(!file.exists()){
			file.createNewFile();
		}
		
		byte [] buf = new byte [4096];
		
		FileOutputStream fos = new FileOutputStream(file);
		
		result.get(FileChannel.class).add (fos.getChannel().getClass());
		// intentionally commented. There isn't a map mode corresponding to 
		// FileOutputStream
		//		result.get(MappedByteBuffer.class).add(fos.getChannel().map(MapMode.PRIVATE, 0, buf.length).getClass());
		
		fos.write(buf);
		fos.close();
		
		
		FileInputStream fis = new FileInputStream(file);
		result.get(FileChannel.class).add(fis.getChannel().getClass());
		result.get(MappedByteBuffer.class).add(fis.getChannel().map(MapMode.READ_ONLY, 0, buf.length).getClass());
		fis.close();
		
		Map<String,MapMode> mapModes = new HashMap<String, MapMode>(4);
		mapModes.put("r", MapMode.READ_ONLY);
		mapModes.put("rw", MapMode.READ_WRITE);
		mapModes.put("rws", MapMode.PRIVATE);
		mapModes.put("rwd", MapMode.PRIVATE);
		
		for(String mode:mapModes.keySet()){
			RandomAccessFile raf = new RandomAccessFile(file, mode);
			result.get(FileChannel.class).add(raf.getChannel().getClass());
			
			result.get(MappedByteBuffer.class).add(raf.getChannel().map(mapModes.get(mode),0,buf.length).getClass());
			raf.close();
				
		}
		
		file.delete();
		
		addSuperClasses(FileChannel.class, result.get(FileChannel.class));
		addSuperClasses(MappedByteBuffer.class, result.get(MappedByteBuffer.class));
		
		if(Options.getInstance().isVerbose()){
			System.out.println("NIO private implementation classes: " + result);
		}
		
		return result;
	}


	public static byte[] getBytes(Class<?> clazz) throws IOException {
	
		String resourcePath = clazz.getName().replace('.', '/') + ".class"; 

//		System.out.println("Util.getBytes():" + resourcePath);
		ClassLoader loader = clazz.getClassLoader();
		InputStream in;
		
		if(loader == null){
			in = ClassLoader.getSystemResourceAsStream(resourcePath);
		} else {
			in = loader.getResourceAsStream(resourcePath);
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

}
