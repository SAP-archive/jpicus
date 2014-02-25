package com.sap.tools.jpicus.client;

import java.io.Serializable;

/**
 * 
 * This class represents a counter of some quantity. The quantity
 * could be I/O bytes, time or something else that can be measured
 * with a long primitive type.
 *  
 * For example I/O operations involve flushing a buffer of data in or out
 * of the JVM. One Counter could be associated with a handle
 * in order to collect statistics about the inbound transfer and another
 * one could be associated with the outbound transfer. There could be
 * more Counter objects associated with the handle in order to measure the
 * time spent in I/o.
 * 
 * @author pavel
 *
 */
public interface Counter extends Serializable {

	
	/**
	 * 
	 * @return the total accumulated quantity
	 */
	double getTotal();
	
	/**
	 * 
	 * @return the minimum quantity passed to the count method
	 */
	double getMin();
	
	/**
	 * 
	 * @return the maximum quantity passed to the count method
	 */
	double getMax();
	
	/**
	 * 
	 * @return the average quantity passed to the count method
	 * total / count
	 */
	double getAverage();
	
	
	/**
	 * 
	 * @return how many times the count method was called.
	 * This value could be used together with the total
	 * quantity in order to calculate the average 
	 * quantity (i.e. total/callCount)
	 */
	long getCount();
	
	/**
	 * 
	 * @return a timestamp of the first invocation to count
	 */
	long getFirstMillis();
	
	/**
	 * 
	 * @return a timestamp of the last invocation to count
	 */
	long getLastMillis();
	
}
