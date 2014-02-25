package com.sap.tools.jpicus.impl.serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.sap.tools.jpicus.client.Counter;
import com.sap.tools.jpicus.client.CounterType;
import com.sap.tools.jpicus.client.HandleDescriptor;
import com.sap.tools.jpicus.client.ThreadInfo;

/**
 *  This class is used as a data holder for the file handle descriptors
 *  it contains information about the lifecycle of a file handle, such as
 *  which thread opened it closed it and when did these happen.
 *  The descriptor keeps a reference to the actual handle until closed
 *  
 * @author pavel
 *
 */
public class HandleDescriptorImpl implements HandleDescriptor {

	
	private static AtomicLong autoIncrement = new AtomicLong(0);
	private static final long serialVersionUID = 1L;
	private final long id;
	
	private Map<CounterType, CounterImpl> counters = new HashMap<CounterType, CounterImpl>(6);
	private long ioStartTime;

	private double minThroughput = Double.MAX_VALUE;
	private double maxThroughput;
	private boolean io;
	private boolean nio;
	private boolean nioDirect;
	private boolean nioMMap;
	private boolean directTransfer;
	
	/*
	 * Keeping a reference to the handle until it is closed will
	 * prevent it from garbage collection in case somebody just
	 * opens handles and leaves them to the garbage collector.
	 * This way the problem won't escape and the root cause 
	 * will be discovered. Another useful feature of this is
	 * to close the handle from the outside. 
	 */
	private transient Object handle;
	private String handleClass;
	private ThreadInfo openedBy;
	private ThreadInfo closedBy;

	
	// TODO consider multiple threads performing I/O on the same handle
	// maybe it is a better idea to have io start time as a thread local
	// on the other hand with non blocking I/O we might have one thread
	// initiate the I/O and another thread process the event of I/O complete
	public long getIOStartTime() {
		return ioStartTime;
	}

	/**
	 * At the beginning of each IO operation with this handle capture the current
	 * time so that the time spent for the operation can be calculated
	 * @param startTime
	 */
	public void setIOStart(long startTime) {
		this.ioStartTime = startTime;
	}
	
	/**
	 * Creates an instance of this object describing the file being opened.
	 * Must be called from the thread that opened the file because the constructor
	 * collects thread context information like stack trace and thread name.
	 * @param canonicalPath
	 * @param handle
	 */
	public HandleDescriptorImpl(Object handle) {

		this.id = autoIncrement.incrementAndGet();
		this.openedBy =  new ThreadInfoImpl(5);
		
		this.handle = handle;
		this.handleClass = handle.getClass().getName();

	}
	
	public void close(){

		this.closedBy = new ThreadInfoImpl(5);
		
		// once the handle is closed we shall release the reference
		// so that the object can be garbage collected
		this.handle = null;
	}
			

	public Object getHandle() {

		return this.handle;
	}
	
	public String getHandleClass() {
		return handleClass;
	}
	
	
	@Override
	public int hashCode() {
		return (int)this.id;
	}
	
	@Override
	public String toString() {
		
		String sep = System.getProperty("line.separator");
		
		StringBuilder buf = new StringBuilder(1000);
		
		buf.append(this.handleClass).
			append(" opened by thread ").append(openedBy).append(sep);
		
		if(this.closedBy != null){
			buf.append("closed by thread ").append(closedBy);
		} else {
			buf.append("still open");
		}
		buf.append(sep);
		buf.append("Max throughput: ").append(maxThroughput);
		buf.append(sep);
		buf.append("Min throughput: ").append(minThroughput);
		buf.append(sep);
		buf.append("Counters:").append(sep)
		   .append(this.counters);
		buf.append(sep);
		return  buf.toString();
	}

	
	public ThreadInfo getClosingThreadInfo() {
		
		return this.closedBy;
	}

	
	public ThreadInfo getOpeningThreadInfo() {
		
		return this.openedBy;
	}

	
	public Counter getCounter(CounterType type) {
		
		return this.counters.get(type);
	}
	
	public CounterImpl getCounter(CounterType type, boolean create){
		
		CounterImpl counter = this.counters.get(type);
		if(counter != null ){
			return counter;
		}
		
		if(create){
			counter = new CounterImpl();
			this.counters.put(type, counter);
		}
		return counter;
	}
	
	public void updateThroughput(double throughput){
		
		if(throughput < minThroughput){
			minThroughput = throughput;
		}
		if(throughput >  maxThroughput){
			maxThroughput = throughput;
		}
		
	}

	
	public double getMaxThroughput() {
		
		return this.maxThroughput;
	}

	
	public double getMinThroughput() {
		
		return this.minThroughput;
	}

	
	public long getId() {

		return this.id;
	}

	
	public boolean isIO() {

		return io;
	}

	
	public boolean isNIO() {

		return nio;
	}

	
	public boolean isNIODirect() {

		return nioDirect;
	}

	
	public boolean isNIOMemoryMapped() {

		return nioMMap;
	}
	
	
	public boolean isNioDirectTransfer(){
		return directTransfer;
	}

	public void setIo(boolean io) {
		this.io = io;
	}

	public void setNio(boolean nio) {
		this.nio = nio;
	}

	public void setNioDirect(boolean nioDirect) {
		this.nioDirect = nioDirect;
	}

	public void setNioMMap(boolean nioMMap) {
		this.nioMMap = nioMMap;
	}
	
	public void setNioDirectTransfer(boolean directTransfer){
		this.directTransfer = directTransfer;
	}

}
