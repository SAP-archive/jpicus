package com.sap.tools.jpicus.tests.nanotime;

import java.io.File;
import java.io.RandomAccessFile;

import junit.framework.Assert;

import org.junit.Test;

import com.sap.tools.jpicus.tests.TestUtil;


public class NanoTimeMeasurementTest {

//	int [] bufSizes = new int [] {};
	
	@Test
	public void test() throws Exception {
	
		for(int bufSize = 100; bufSize < 1000000; bufSize*=10){
			
			int iterations = 100000000 / bufSize;
			test(iterations,bufSize);
			
		}
		
		
	}
	
	private void test(final int iterations, final int bufSize) throws Exception {
		
		File target = TestUtil.createEmptyFile(this.getClass().getName());
		RandomAccessFile raf = new RandomAccessFile(target,"rw");

		long ns = 0;
		byte [] buf = new byte [bufSize];
		
		long start = System.currentTimeMillis();
		
		for(int i=0; i < iterations; i++){
			
			long mark = System.nanoTime();
			
			raf.seek(0);
			raf.write(buf);
						
			ns += (System.nanoTime() - mark);
	
		}
		
		long basedOnMs = System.currentTimeMillis() - start;
		long basedOnNs = ns/1000000;
		
		
		Assert.assertTrue("ns result shall not be greater than the ms result", basedOnMs >= basedOnNs);
		
		System.out.println("buf: " + bufSize);
		System.out.println("iterations: " + iterations);
		System.out.println("ms: " + basedOnMs);
		System.out.println("ns: " + basedOnNs);
		
		double deviation = 1 - (double)basedOnNs/basedOnMs;
		System.out.println(deviation);
		
		Assert.assertTrue("ns result deviation shall be within 1%", deviation < 0.3);
		
		
		raf.close();
		Assert.assertTrue(target.delete());

		
	}
	
		
}
