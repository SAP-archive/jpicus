package com.sap.tools.jpicus.impl.serializable;

import java.util.Date;

import com.sap.tools.jpicus.client.Counter;

public class CounterImpl implements Counter {

	private static final long serialVersionUID = 1L;
	
	private double max;
	
	/* initialize it to some really big value so that
	 *  when the buffer is flushed for the first time 
	 *  the real buffer size is saved
	 */
	private double min = Double.MAX_VALUE;
	private double total;
	
	private long count;
	
	private long firstCountMillis;
	private long lastCountMillis;
	
	/**
	 * Use this method to increase the counter total with the given quantity
	 * and update all other internal counter parameters such such as min, max
	 * count etc
	 * @param quantity the quantity to be counted
	 */
	public void count(double quantity) {
		
		this.lastCountMillis = System.currentTimeMillis();
			
		if(this.firstCountMillis == 0){
			this.firstCountMillis = this.lastCountMillis; 
		}
		
		
		this.total += quantity;
		this.count ++;

		if(quantity > this.max){
			this.max = quantity;
		}

		if(quantity < this.min ){
			this.min = quantity;
		}
		
	}


	
	public long getCount() {

		return this.count;
	}


	
	public long getFirstMillis() {
		
		return this.firstCountMillis;
	}


	
	public long getLastMillis() {
		
		return this.lastCountMillis;
	}


	
	public double getMax() {

		return this.max;
	}


	
	public double getMin() {
		
		return this.min;
	}


	
	public double getTotal() {
	
		return this.total;
	}
	
	
	public String toString() {
		
		String sep = System.getProperty("line.separator");
		
		StringBuilder buf = new StringBuilder(200);
		buf.append("total:").append(this.total).append(sep);
		buf.append("count:").append(this.count).append(sep);
		buf.append("min:").append(this.min).append(sep);
		buf.append("max:").append(this.max).append(sep);
		buf.append("first at:").append(new Date(this.firstCountMillis)).append(sep);
		buf.append("last at:").append(new Date(this.lastCountMillis)).append(sep);
		
		return buf.toString();
	}


	
	public double getAverage() {

		return total / count;
	}


}
