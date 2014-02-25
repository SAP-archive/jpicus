package com.sap.tools.jpicus.client.impl;

import org.junit.Test;

import com.sap.tools.jpicus.impl.serializable.CounterImpl;
import com.sap.tools.jpicus.tests.TestUtil;

import static org.junit.Assert.*;
public class CounterImplTest {

	@Test
	public void testInitialState(){

		CounterImpl counter = new CounterImpl();

		assertEquals("count",counter.getCount(),0);
		assertEquals("firstMillis",counter.getFirstMillis(),0);
		assertEquals("lastMillis",counter.getLastMillis(),0);
		assertEquals("max",counter.getMax(),0,0);
		assertEquals("min",counter.getMin(),Double.MAX_VALUE,0);
		assertEquals("total",counter.getTotal(),0,0);


	}

	@Test
	public void testRandomCountLong() throws Exception {
		long count = TestUtil.getRandomLongCount(1,1000);
		CounterImpl counter = new CounterImpl();

		final long startTime = System.currentTimeMillis();

		Thread.sleep(2);

		long max = 0;
		long min = Long.MAX_VALUE;
		long total = 0;

		for(long i=0; i<count;i++){

			long quantity = TestUtil.getRandomLongCount(0, 10000);
			counter.count(quantity);
			total += quantity;
			if(max < quantity){
				max = quantity;
			}
			if(min > quantity){
				min = quantity;
			}
		}

		Thread.sleep(2);

		final long endTime = System.currentTimeMillis();

		assertEquals("count",counter.getCount(),count);
		assertTrue("firstMillis", counter.getFirstMillis() > startTime);
		assertTrue("lastMillis",counter.getLastMillis() < endTime);
		assertEquals("max",counter.getMax(),max,0);
		assertEquals("min",counter.getMin(),min,0);
		assertEquals("total",counter.getTotal(),total,0);


	}

}
