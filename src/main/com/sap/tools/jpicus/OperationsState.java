package com.sap.tools.jpicus;

import static com.sap.tools.jpicus.impl.util.Util.SEP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.client.DeleteOperation;
import com.sap.tools.jpicus.client.HandleDescriptor;
import com.sap.tools.jpicus.client.ListOperation;
import com.sap.tools.jpicus.client.Snapshot;
import com.sap.tools.jpicus.impl.serializable.ArraySet;
import com.sap.tools.jpicus.impl.serializable.CounterImpl;
import com.sap.tools.jpicus.impl.serializable.DeleteOperationImpl;
import com.sap.tools.jpicus.impl.serializable.HandleDescriptorImpl;
import com.sap.tools.jpicus.impl.serializable.ListOperationImpl;
import com.sap.tools.jpicus.impl.serializable.Options;
import com.sap.tools.jpicus.impl.serializable.SnapshotImpl;
import com.sap.tools.jpicus.impl.util.IOType;
import com.sap.tools.jpicus.impl.util.Util;

/**
 * This class represents the current state of the agent with respect to all the operations that took place,
 * such as opening, closing, reading, writing and deleting. It shall be possible to dump this state upon agent
 * shutdown or upon request.
 *
 * Remote client have read only access to the state
 *
 * @author pavel
 *
 * TODO refactor this class so that it is fully unit testable and create comprehensive tests
 *
 */
public class OperationsState implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * This mutex is used in order to protect the structure of the maps/sets from
	 * modification during the serialization of the whole state (i.e. creation of a snapshot)
	 */
	private transient Object mutex = new Object();

	private int peakHandleCount;
	private long peakHandleCountTime;

	private volatile AtomicInteger currentHandleCount = new AtomicInteger();

	// handles (client visible)
	private final Map<String, Set<HandleDescriptor>> canonical2Handles = new HashMap<String, Set<HandleDescriptor>>();

	// internal
	private transient final Map<Object, HandleDescriptorImpl> handle2descriptor = new ConcurrentHashMap<Object, HandleDescriptorImpl>();
	private transient final Map<Object, String> handle2Canonical = new ConcurrentHashMap<Object, String>();

	private transient final Map<FileChannel, Object> fileChannel2Handle = new ConcurrentHashMap<FileChannel, Object>();
	private transient final ConcurrentHashMap<Object, Set<FileChannel>> handle2FileChannels =  new ConcurrentHashMap<Object, Set<FileChannel>>();

	private transient final ConcurrentHashMap<MappedByteBuffer, Object> buffer2Handle = new ConcurrentHashMap<MappedByteBuffer, Object>();
	private transient final ConcurrentHashMap<Object, Set<MappedByteBuffer>> handle2MappedBuffers = new ConcurrentHashMap<Object, Set<MappedByteBuffer>>();

//	private transient ThreadLocal<Boolean> ioStarted = new ThreadLocal<Boolean>();

	// File.delete() operations (client visible)
	private final Map<String, Set<DeleteOperation>> canonical2DeleteOperations = new HashMap<String, Set<DeleteOperation>>();

	// File.list() operations
	private final ConcurrentHashMap<String, Set<ListOperation>> listOperations = new ConcurrentHashMap<String, Set<ListOperation>>();


	public void fileOpened(Object pathObject, Object handle) {

		if(handle2descriptor.containsKey(handle)){
			// this could happen because we instrument all public constructors
			// and sometimes they call each other, that is why we could receive
			// more than one callback for the same handle. We shall always use the
			// first callback and ignore the next ones for the same handle
			return;
		}

		HandleDescriptorImpl descriptor = new HandleDescriptorImpl(handle);
		String path = Util.getCannonical(pathObject);


		handle2descriptor.put(handle, descriptor);
		handle2Canonical.put(handle, path);
		int current = currentHandleCount.incrementAndGet();

		synchronized(mutex){

			Set<HandleDescriptor> handleDescriptors = canonical2Handles.get(path);

			if(handleDescriptors == null){ // first handle for this canonical

				handleDescriptors = new ArraySet<HandleDescriptor>(2);
				canonical2Handles.put(path, handleDescriptors);
			}

			handleDescriptors.add(descriptor);

			if(peakHandleCount < current){
				peakHandleCount = current;
				peakHandleCountTime = System.currentTimeMillis();
			}


		}

	}

	public void fileClosed(Object handle) {


		HandleDescriptorImpl descriptor = handle2descriptor.get(handle);
		if(descriptor == null){
			// this might happen if the handle has been opened before the agent was initialized
			return;
		}

		descriptor.close();
		currentHandleCount.decrementAndGet();

		if( !Options.getInstance().getKeepClosedHandles() ){

			String canonical = handle2Canonical.get(handle);
			synchronized(mutex){

				Set<HandleDescriptor> descriptors = canonical2Handles.get(canonical);
				descriptors.remove(descriptor);
				if(descriptors.size() == 0){
					canonical2Handles.remove(canonical);
				}
			}
		}

		handle2Canonical.remove(handle);
		handle2descriptor.remove(handle);

		Set<FileChannel> channels = handle2FileChannels.get(handle);
		if(channels != null){
			synchronized(channels){
				for(FileChannel channel:channels){
					fileChannel2Handle.remove(channel);
				}
			}
		}
		handle2FileChannels.remove(handle);


		Set<MappedByteBuffer> buffers = handle2MappedBuffers.get(handle);
		if(buffers != null){
			synchronized(buffers){
				for(MappedByteBuffer buffer:buffers){
					buffer2Handle.remove(buffer);
				}
			}
		}

		handle2MappedBuffers.remove(handle);


	}



	/**
	 * The entry point for all file I/O
	 * @param handle
	 * @param byteCount
	 * @param read
	 * @param type
	 */
	public synchronized void fileIO(Object handle,
									long byteCount,
									boolean read,
									IOType type){

		final long timestamp = System.nanoTime();

		HandleDescriptorImpl descriptor = getDescriptor(handle);
		if(descriptor == null){
			return;
		}

		long timeSpent = timestamp - descriptor.getIOStartTime();;

		switch (type) {

		case IO:
			descriptor.setIo(true);
			break;

		case NIO:
			descriptor.setNio(true);
			break;

		case DIRECT_BUFFER:
			descriptor.setNioDirect(true);
			break;

		case MEMORY_MAPPED:
			descriptor.setNioMMap(true);
			break;

		case DIRECT_TRANSFER:
			descriptor.setIo(true);
			break;

		default:
			throw new IllegalArgumentException("Unsupported IO type: " + type);
		}

		count(descriptor, byteCount, timeSpent, read);

	}


	/**
	 *
	 * @param handle could be either the real handle or a file channel
	 * @return
	 */
	private HandleDescriptorImpl getDescriptor(final Object handle) {

		HandleDescriptorImpl descriptor = handle2descriptor.get(handle);

		if(descriptor != null){
			return descriptor;
		}

		// assuming that "handle" is not the real handle but a file channel
		Object fileChannelHandle = fileChannel2Handle.get(handle);
		if(fileChannelHandle != null){
			return handle2descriptor.get(fileChannelHandle);
		}

		// assuming that "handle" is not the real handle but a mapped byte buffer
		Object mappedByteBufferHandle = buffer2Handle.get(handle);
		if(mappedByteBufferHandle != null){
			return handle2descriptor.get(mappedByteBufferHandle);
		}


		// the handle might have been opened before agent initialization
		return null;


	}

	public void associateMappedByteBuffer(MappedByteBuffer buf, FileChannel channel){
		Object handle = fileChannel2Handle.get(channel);
		if(handle == null){
			return;
		}

		buffer2Handle.put(buf,handle );
		Set<MappedByteBuffer> buffers = handle2MappedBuffers.get(handle);
		if(buffers == null){
			buffers = new HashSet<MappedByteBuffer>();
			Set<MappedByteBuffer> alreadyThere = handle2MappedBuffers.putIfAbsent(handle, buffers);
			if(alreadyThere != null){
				buffers = alreadyThere;
			}
		}
		synchronized(buffers){
			buffers.add(buf);
		}

	}

	/**
	 *
	 * @param descriptor
	 * @param byteCount
	 * @param timeSpent in nanoseconds
	 * @param read
	 */
	private void count(HandleDescriptorImpl descriptor,
					   long byteCount,
					   long timeSpent,
					   boolean read){

		synchronized(descriptor){
			// bytes
			CounterType byteCounterType = read ? CounterType.BYTES_READ : CounterType.BYTES_WRITE;
			CounterImpl bytesCounter = descriptor.getCounter(byteCounterType, true);
			bytesCounter.count(byteCount);

			// time spent
			CounterType timeCounterType = read ? CounterType.TIME_READ : CounterType.TIME_WRITE;
			CounterImpl timeCounter = descriptor.getCounter(timeCounterType, true);
			timeCounter.count(timeSpent);

			if(timeSpent == 0){
				// don't pollute the throughput counter with fake measurements with throughput = infinity
				return;
			}

			// throughput
			double throughput = byteCount / (double)timeSpent;
			descriptor.updateThroughput(throughput);

		}

	}



	public void deleteOperation(String canonical, boolean success) {

//		if(canonical.contains("jpicusRandomTestFile")){
//			System.out.println("Delete: " + canonical + " : " + success);
//		}

		File file = new File(canonical);
		if(!success && !Options.getInstance().getCollectDeleteOfNonExisting() && !file.exists() ){
			return;
		}

		String description = null;

		if(!success) {
			// collect additional context for failed operations
			StringBuilder buf = new StringBuilder();
			if(!file.exists()){

				buf.append("File doesn't exist").append(SEP);
			} else {
				if(!file.isFile()){
					buf.append("Directory.").append(SEP);
				}

				if(!file.canWrite()){
					buf.append("Can't write.").append(SEP);
				}

				buf.append("Last modified: ").append( new Date(file.lastModified()) );
			}
			description = buf.toString();

		}

		DeleteOperationImpl op = new DeleteOperationImpl(success, description);
		synchronized(mutex){
				// capture the current handle descriptors associated with this canonical

				Set<HandleDescriptor> descriptors = canonical2Handles.get(canonical);
				if(descriptors != null){
					Set<HandleDescriptor> set = new HashSet<HandleDescriptor>(descriptors);
					op.setDescriptors(set);
				}
//				TODO
//				// if not identified within the JVM
//				// try to identify any external processes that might keep the file open
//				if(descriptors == null ){
//					String externalOwners = Util.getExternalOwners(canonical);
//					if(externalOwners != null){
//						descriptors = new HashSet<HandleDescriptorImpl>(1);
//						descriptors.add(new HandleDescriptorImpl(externalOwners));
//					}
//				}

			// add this operation to the current set of operations associated with this canonical
			Set<DeleteOperation> operations = canonical2DeleteOperations.get(canonical);
			if(operations == null){
				operations = new ArraySet<DeleteOperation>(2);
				canonical2DeleteOperations.put(canonical, operations);
			}

			operations.add(op);

		}

	}


	public void listOperation(String canonical, int size, long time ){

		ListOperationImpl operation = new ListOperationImpl(size, time);
		Set<ListOperation> ops = this.listOperations.get(canonical);
		if(ops == null){
			ops = new ArraySet<ListOperation>(2);
			ops = this.listOperations.putIfAbsent(canonical, ops);
		}
		ops.add(operation);

	}

	public int getPeakHandleCount() {
		return peakHandleCount;
	}

	public long getPeakHandleCountTime() {
		return peakHandleCountTime;
	}

	public int getCurrentHandleCount() {
		return currentHandleCount.get();
	}

	public Map<String, Set<HandleDescriptor>> getHandles() {
		return canonical2Handles;
	}

	public Map<String, Set<DeleteOperation>> getDeleteOperations(){
		return canonical2DeleteOperations;
	}

	public void startIO(Object handle) {

		HandleDescriptorImpl descriptor = getDescriptor(handle);

		// the descriptor can be null if the handle has been opened before
		// the agent was initialized
		if(descriptor != null){
			descriptor.setIOStart(System.nanoTime());
//			ioStarted.set(true);
		}

	}

	public byte [] creteSnapshot() throws IOException {

		Snapshot snapshot = new SnapshotImpl(System.currentTimeMillis() , this );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);

		// lock globally while serializing the state
		synchronized (mutex) {
			out.writeObject(snapshot);
		}
		// TODO replace ByteArrayOutputStream with more memory efficient object
		return baos.toByteArray();
	}

	public void closeHandle(long id) {

		Iterator<Object> iterator = handle2descriptor.keySet().iterator();

		while(iterator.hasNext()){
			Object handle = iterator.next();
			HandleDescriptorImpl descriptor = handle2descriptor.get(handle);
			if(descriptor.getId() == id){

				try {
					Method closeMethod = handle.getClass().getDeclaredMethod("close", new Class []{});
					closeMethod.setAccessible(true);
					closeMethod.invoke(handle, null, new Object [] {});
				} catch (Exception e) {

					e.printStackTrace();
				}
				break;
			}
		}

	}

	public Map<String, Set<ListOperation>> getListIoperations() {

		return this.listOperations;
	}

	/**
	 * This method shall establish the necessary mapping between a file channel and
	 * its originating handle and vice versa so that the operations state can
	 * be updated efficiently with IO measurements and when the handle/or channel are
	 * closed.
	 * @param channel
	 * @param handle
	 */
	public void associateFileChannel(FileChannel channel, Object handle) {

		fileChannel2Handle.put(channel, handle);
		Set<FileChannel> channels = handle2FileChannels.get(handle);
		if(channels == null){
			channels = new HashSet<FileChannel>();
			Set<FileChannel> alreadyThere = handle2FileChannels.putIfAbsent(handle, channels);
			if(alreadyThere != null){
				channels = alreadyThere;
			}
		}
		synchronized(channels){
			channels.add(channel);
		}

	}

	public void fileChannelClosed(FileChannel channel) {

		Object handle = fileChannel2Handle.get(channel);
		if(handle == null){
			return;
		}

		fileClosed(handle);
	}

}
