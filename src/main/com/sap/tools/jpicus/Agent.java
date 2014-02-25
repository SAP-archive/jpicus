package com.sap.tools.jpicus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import com.sap.tools.jpicus.client.AgentManager;
import com.sap.tools.jpicus.client.CalibrationData;
import com.sap.tools.jpicus.impl.remote.AgentManagerImpl;
import com.sap.tools.jpicus.impl.serializable.Options;
import com.sap.tools.jpicus.impl.transformers.deleteoperations.DeleteOperationsTransformer;
import com.sap.tools.jpicus.impl.transformers.filehandles.FileHandlesTransformer;
import com.sap.tools.jpicus.impl.transformers.io.file.FileIOTransformer;
import com.sap.tools.jpicus.impl.transformers.io.file.channel.FileChannelIOTransformer;
import com.sap.tools.jpicus.impl.transformers.io.file.channel.MappedByteBufferTransformer;
import com.sap.tools.jpicus.impl.transformers.io.zip.IOTracingInputStreamWrapper;
import com.sap.tools.jpicus.impl.transformers.io.zip.ZipFileIOTransofrmer;
import com.sap.tools.jpicus.impl.transformers.nativemethods.NativeMethodTransformer;
import com.sap.tools.jpicus.impl.util.IOType;
import com.sap.tools.jpicus.impl.util.Util;

public class Agent {

	public static final String AGENT_CLASS = "com/sap/tools/jpicus/Agent";

	private static volatile boolean active = true;
//	private static volatile boolean serverStarted = false;
	private static Instrumentation instrumentation;
	
	// remote interface
	private static AgentManagerImpl agentManager;	
//	private static Collection<Remote> exportedObjects = new HashSet<Remote>();
	private static OperationsState operationsState = new OperationsState();
	
	private static boolean isJavaSE5;
	
	public static OperationsState getOperationsState(){
		return operationsState;
	}

	
	public static final String CALIBRATION_FILE = "com.sap.tools.jpicus.calibration.file";
	

	
	public static void premain(String args, Instrumentation inst) throws Exception {
		
		instrumentation = inst;
	
		String javaVersion = System.getProperty("java.version");
//		System.out.println("Java version: " + javaVersion);
		isJavaSE5 = javaVersion.startsWith("1.5");
		
		if(isJavaSE5 && !instrumentation.isRedefineClassesSupported()){
			throw new Exception("Redefine classes is not supported");
		}
		
		if(!isJavaSE5 && !instrumentation.isRetransformClassesSupported()){
			throw new Exception("Retransform classes is not supported");
		}
		
		parseOptions(args);
		initManagers();
		calibrate();
		registerShutdownHook();
		
		preloadAgentClasses();
		applyInstrumentations();
		
		startRMIServer();
		
	}
	
	private static void registerShutdownHook() {
		
		if(Options.getInstance().dumpStateOnExit()){
			
			Runtime.getRuntime().addShutdownHook(new Thread(){
				
				@Override
				public void run() {
					
					active = false;		
					try {
						
						File snapshot = new File("snapshot_" + new Date() + ".jpicus");
						byte [] data = operationsState.creteSnapshot();
						OutputStream os = new FileOutputStream(snapshot);
						os.write(data);
						
						if(Options.getInstance().isVerbose()){
							System.out.println("Snapshot written to: " + snapshot.getCanonicalPath());	
						}
						
					} catch (Exception e){
						e.printStackTrace();
					}
					
				}

				
			});
			if(Options.getInstance().isVerbose()){
				System.out.println("Added shutdown hook");
			}
			
		}
	}
	
//	private static void stopRMIServer() {
//		try {
//			for(Remote exported:exportedObjects){
//				UnicastRemoteObject.unexportObject(exported, true);	
//			}
//			
//		} catch (NoSuchObjectException e) {
//			
//			e.printStackTrace();
//		}
//		exportedObjects.clear();
//		exportedObjects = null;
//		
//	}

	private static void calibrate() {
		
		if(!Options.getInstance().isCalibrationEnabled()){
			return;
		}
	
		CalibrationData calibration = Calibrator.calibrate();
		agentManager.setCalibrationData(calibration);
		if(Options.getInstance().isVerbose()){
			System.out.println("Calibration finished");
		}
		
	}

	public static Instrumentation getInstrumentation(){
		return instrumentation;
	}
	
	private static void parseOptions(String args){
		
		Options.parse(args);
		
	}


	/**
	 * Load all the classes that would be possibly required during a callback from
	 * instrumented code. Failure to do so might lead to nasty and non-debuggable 
	 * problems like:
	 * 
	 * 	<OL>
      		<LI>Class circularity errors in case class loading is initiated during a callback 
      		will try to create another FileInputStream which will lead to a ClassCircularityError
	 		e.g. an event from the FileInputStreamConstructor that leads to class loading
      	
      		<LI>Endless loops. For example if a callback for file IO comes from say a FileOutputStream
      		and this callback is processed in the agent in such a way that it leads to exactly the same
      		type of IO this will cause an endless loop and (curiously) the JVM might crash (instead of
      		throwing a StackOverflowError to the calling thread)
		</OL>
	 *  
	 *  
	 * @throws Exception
	 */
	private static void preloadAgentClasses() throws Exception {
		
		// com.sap.tools.jpicus.impl.serializable
		Class.forName("com.sap.tools.jpicus.impl.serializable.ArraySet");
		Class.forName("com.sap.tools.jpicus.impl.serializable.CalibrationDataImpl");
		Class.forName("com.sap.tools.jpicus.impl.serializable.CounterImpl");
		Class.forName("com.sap.tools.jpicus.impl.serializable.DeleteOperationImpl");
		Class.forName("com.sap.tools.jpicus.impl.serializable.HandleDescriptorImpl");
		Class.forName("com.sap.tools.jpicus.impl.serializable.ListOperationImpl");
		Class.forName("com.sap.tools.jpicus.impl.serializable.SnapshotImpl");
		Class.forName("com.sap.tools.jpicus.impl.serializable.ThreadInfoImpl");
		Class.forName("com.sap.tools.jpicus.client.CounterType");
		
		
		// com.sap.tools.jpicus.impl.util
		Class.forName("com.sap.tools.jpicus.impl.util.Util");
		Class.forName("com.sap.tools.jpicus.impl.util.IOType");
		Class.forName("com.sap.tools.jpicus.impl.util.OSType");
		Class.forName("com.sap.tools.jpicus.impl.util.Deserializer");
		
		Class.forName("java.io.ByteArrayOutputStream");
		Class.forName("java.io.File");
		Class.forName("java.io.IOException");
		Class.forName("java.io.ObjectOutputStream");
		Class.forName("java.lang.reflect.Method");
		Class.forName("java.util.Date");		
		
		if(Options.getInstance().isVerbose()){
			System.out.println("Preloaded agent classes");
		}
	
	}
	
	static void initManagers() throws Exception {
		
		// remotely accessible objects		
		agentManager = new AgentManagerImpl(operationsState );
		if(Options.getInstance().isVerbose()){
			System.out.println("AgentManager initialized");
		}

	}
	
	
	private static void applyInstrumentations() throws Exception {
		
		if(!isJavaSE5 && instrumentation.isNativeMethodPrefixSupported()){
			
			NativeMethodTransformer nmt = new NativeMethodTransformer();
			instrumentation.addTransformer(nmt);
			instrumentation.setNativeMethodPrefix(nmt, "$$$com_sap_tools_jpicus$$$");
			
		} else {
			if(Options.getInstance().isVerbose()){
				System.out.println("Native method prefix is not supported");	
			}
		}
		
		Map<Class<?>, Set<Class<?>>> nioPrivateImplementationClasses = Util.getNIOPrivateImplementationClasses();
		Set<Class<?>> fileChannelImplClasses = nioPrivateImplementationClasses.get(FileChannel.class);
		Set<Class<?>> mappedbyteBufferClasses = nioPrivateImplementationClasses.get(MappedByteBuffer.class);
		
		// add all the transformers according to options
		
		// handles
		addTransformer(new FileHandlesTransformer(null));
		
		
		// delete operations
		if(Options.getInstance().getTrackSuccessfulDelete() ||
				Options.getInstance().getTrackFailedDelete()){
		
			addTransformer(new DeleteOperationsTransformer());
			
		}

		// IO
		if(Options.getInstance().getTrackIO()){
		
			addTransformer(new FileIOTransformer());
			addTransformer(new ZipFileIOTransofrmer());
			addTransformer(new FileChannelIOTransformer(fileChannelImplClasses));
			addTransformer(new MappedByteBufferTransformer(mappedbyteBufferClasses));
		}
		
		if(Options.getInstance().isVerbose()){
			System.out.println("Added the transformers");
		}
		
		
		// java.io
		
		// these classes are most likely already loaded and it's not worth the
		// additional effort checking this
		retransformClasses(File.class);
		retransformClasses(FileInputStream.class);
		retransformClasses(FileOutputStream.class);
		retransformClasses(RandomAccessFile.class);
		retransformClasses(ZipFile.class);
		
		
//		// java.nio private classes
		for(Class<?> cls:fileChannelImplClasses){
			retransformClasses(cls);	
		}
		
		for(Class<?> cls: mappedbyteBufferClasses){
			retransformClasses(cls);
		}
		
		if(Options.getInstance().isVerbose()){
			System.out.println("Applied instrumentations");
		}
		
	}

	private static void retransformClasses(Class<?> clazz) throws Exception{
		
		if(isJavaSE5){
			
			byte [] bytes = Util.getBytes(clazz);
			ClassDefinition definition = new ClassDefinition(clazz, bytes);
			instrumentation.redefineClasses(new ClassDefinition [] {definition});
			
		} else {
			instrumentation.retransformClasses(clazz);
		}
		
	}

	private static void addTransformer(ClassFileTransformer transformer) {
		
		if(isJavaSE5){
			instrumentation.addTransformer(transformer);
		} else{ 
			instrumentation.addTransformer(transformer, true);
		}
		
	}

	private static void startRMIServer() throws Exception {
		
		if(Options.getInstance().dumpStateOnExit()){
			// these two options are conflicting because the RMI server prevents the
			// process from exiting
			return;
		}
		final int port = Options.getInstance().getPort();
		if(Options.getInstance().isVerbose()){
			System.out.println("Starting JPicus RMI server on port: " + port);	
		}
		
		Thread t = new Thread(){
			@Override
			public void run() {

				// start the registry
				try {
					Remote agentStub = UnicastRemoteObject.exportObject(agentManager, port);
//					exportedObjects.add(agentStub);
					Registry rmiRegistry = LocateRegistry.createRegistry(port);
//					Naming.bind("com.sap.tools.jpicus.client.AgentManager", agentManager);
					rmiRegistry.rebind(AgentManager.class.getName(), agentStub);
//					serverStarted = true;
					if(Options.getInstance().isVerbose()){
						System.out.println("JPicus RMI Server started");	
					}
					
				} catch (RemoteException e) {
					
					e.printStackTrace();
				}							
			}
		};
		// start the server in a daemon thread
		t.setDaemon(true);
		t.start();
		t.join();
		
//		Thread cleaner = new Thread(){
//			
//			@Override
//			public void run() {
//			
//				// now make all the threads except the current one
//				// daemons. This will make it possible to preserve the
//				// original shutdown behavior of the aplication
//				// Otherwise it will not exit because of the RMI Reaper
//				// non daemon thread
//				while(true){
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						break;
//					}
//					if(!serverStarted){
//						continue;
//					}
//					Thread [] threads = new Thread[Thread.activeCount() + 300];
//					Thread.enumerate(threads);
//					int nonDaemons = 0;
//					for(Thread t:threads){
//						if(t == null){
//							continue;
//						}
//						if(t == Thread.currentThread()){
//							continue;
//						}
//						
//						if(t.isAlive() && !t.isDaemon()){
//							
//							nonDaemons++;							
//						}
//					}
//					// if there is just a single non-daemon thread it is just the
//					// server left so we have to stop it so that the client can exit
//					if(nonDaemons == 1){
//						stopRMIServer();
//						break;
//					}
//				
//	
//				}
//							}
//		};
//		cleaner.setName("JPicus cleanup thread");
//		cleaner.setDaemon(true);
//		cleaner.start();
			
	}
	
	public static AgentManager getAgentManager(){
		return agentManager;
	}
	
	
	
	// called by the instrumented code / unit tests
	
	/**
	 * a convenience method for the bytecode instrumentation/unit tests
	 * @param cannonicalPath
	 * @param handle
	 */
	public static void fileOpened(Object path, Object handle) {
	
		operationsState.fileOpened( path, handle );
		
	}
	
	/**
	 * Inform the agent that an IO operation has been started with the
	 * given handle (e.g. FileInputStream.read(byte [] buf)
	 * 
	 * TODO this method assumes that handles are used by one thread only.
	 * Check if it is legal to use a handle by many threads at a time
	 * (does not apply to ZipFile because it is handled by wrapping the
	 * entry input streams and does not use this method)
	 * 
	 * @param handle
	 */
	public static void startIO(Object handle){

		operationsState.startIO(handle);
		
	}

	/**
	 * a convenience method for the bytecode instrumentation/unit tests
	 * @param cannonicalPath
	 * @param handle
	 */
	public static void fileClosed(Object handle) {
		
		operationsState.fileClosed(handle);
	}
	
	
	/**
	 * 
	 * This method is called by the instrumented code in order to 
	 * report file input. In order for this method to work correctly
	 * the startIO method must be called right before the operation IO
	 * is attempted. This is used in order to measure the time spent
	 * for the operation
	 * 
	 * @param byteCount
	 * @param handle
	 * @see startIO()
	 */
	public static void fileInput(int byteCount, Object handle){
		
		if(!active){
			return;
		}
		if(byteCount < 1){
			return; // not interested in -1 and 0
		}

		operationsState.fileIO(handle, byteCount, true, IOType.IO);
	}
	
	public static void fileChannelInput(int byteCount, FileChannel channel, ByteBuffer buffer){
		
//		System.err.println("fileChannelInput: " + byteCount + " channel " + channel + " buffer " + buffer);
		if(!active){
			return;
		}
		if(byteCount < 1){
			return; // not interested in -1 and 0
		}
		
		IOType type = getIOType(buffer);
		operationsState.fileIO(channel, byteCount, true, type);
	}
	

	// TODO include the buffers, offset and length parameters
	public static long fileChannelInputScatter(long byteCount, FileChannel channel){
		
		if(!active){
			return byteCount;
		}
		if(byteCount < 1){
			return byteCount; // not interested in -1 and 0
		}
		
		IOType type = IOType.NIO;
		operationsState.fileIO(channel, byteCount, true, type);
		return byteCount;
	}
	
	public static long fileChannelInputDirectTransfer(long byteCount, FileChannel channel){
	
		if(!active){
			return byteCount;
		}
		if(byteCount < 1){
			return byteCount; // not interested in -1 and 0
		}
		
		operationsState.fileIO(channel, byteCount, true, IOType.DIRECT_TRANSFER);
		return byteCount;
	}
	
	public static void fileChannelInputMappedByteBuffer(long byteCount, MappedByteBuffer buffer){
		
		if(!active){
			return;
		}
		if(byteCount < 1){
			return; // not interested in -1 and 0
		}
		
		operationsState.fileIO(buffer, byteCount, true, IOType.MEMORY_MAPPED);
		
	}
	
	private static IOType getIOType(ByteBuffer buffer) {
		
		if(buffer.isDirect()){
			return IOType.DIRECT_BUFFER;
		} else {
			return IOType.NIO;
		}
	}


	/**
	 * This method is called by the instrumented code in order to 
	 * report file output
	 * 
	 * @param byteCount
	 * @param handle
	 */
	public static void fileOutput(int byteCount, Object handle){
		
		if(!active){
			return;
		}
		
		if(byteCount < 1){
			return; // not interested in -1 and 0
		}

		operationsState.fileIO(handle, byteCount, false, IOType.IO);
	}


	public static void fileChannelOutput(int byteCount, FileChannel channel, ByteBuffer buffer){
	
		if(!active){
			return;
		}
		if(byteCount < 1){
			return; // not interested in -1 and 0
		}
		
		IOType type = getIOType(buffer);
		operationsState.fileIO(channel, byteCount, false, type);
		
	}
	
	
	// TODO include the actual buffers, offset and length
	public static long fileChannelOutputGather(long byteCount, FileChannel channel){
		
		if(!active){
			return byteCount;
		}
		if(byteCount < 1){
			return byteCount; // not interested in -1 and 0
		}
		
		IOType type = IOType.NIO;
		operationsState.fileIO(channel, byteCount, false, type);
		return byteCount;
	}
	
	public static long fileChannelOutputDirectTransfer(long byteCount, FileChannel channel){
		
		System.err.println("Direct transferFrom. Bytes: " + byteCount);
		if(!active){
			return byteCount;
		}
		if(byteCount < 1){
			return byteCount; // not interested in -1 and 0
		}
		
		operationsState.fileIO(channel, byteCount, false, IOType.DIRECT_TRANSFER);
		return byteCount;
	}
	
	public static void fileChannelOutputMappedByteBuffer(long byteCount, MappedByteBuffer buffer){
	
		if(!active){
			return;
		}
		if(byteCount < 1){
			return; // not interested in -1 and 0
		}
		
		operationsState.fileIO(buffer, byteCount, false, IOType.MEMORY_MAPPED);
	}
	
	/**
	 * Called from the instrumented code in order to wrap the input stream
	 * originally returned by the handle. Wrapping is cheaper and easier than
	 * instrumentation (because addition of fields is not allowed in the already
	 * loaded classes) but may have some behavioral effect e.g. if somebody is
	 * using instanceof in order to check for a particular implementation
	 *  
	 * @param toBeWrapped the original stream
	 * @param handle the handle the the stream has to be associated with
	 * @return a wrapper of the stream that tracks IO on behalf of the given
	 * handle
	 */
	public static InputStream wrapInputStream(InputStream toBeWrapped, Object handle){
	
		InputStream wrapper = new IOTracingInputStreamWrapper(toBeWrapped,handle);
		return wrapper;
	}
	
	
	
	public static FileChannel associateFileChannel(FileChannel channel, Object handle){

		operationsState.associateFileChannel(channel,handle);
		return channel;
	}
	
	/**
	 * This method shall be called from the instrumented code when the 
	 * corresponding file channel's close method is invoked.
	 * @param channel
	 */
	public static void fileChannelClosed(FileChannel channel){
		operationsState.fileChannelClosed(channel);
	}
	
	public static void associateMappedByteBuffer(MappedByteBuffer buf, FileChannel channel){
		operationsState.associateMappedByteBuffer(buf, channel);
	}
	
	
//	public static void socketInput(Object handle, int byteCount, int time){
//		fileManager.input(handle, byteCount, time);
//	}
//
//	public static void socketOutput(Object handle, int byteCount, int time){
//		fileManager.output(handle, byteCount, time);
//	}
	
	public static void deleteOperation(boolean success, File file){
		
		if(success && !Options.getInstance().getTrackSuccessfulDelete()){
			return;
		}
		
		if(!success && !Options.getInstance().getTrackFailedDelete()){
			return;
		}
		
		String canonical = Util.getCannonical(file);
		
		operationsState.deleteOperation(canonical, success);

	}	
	
}
