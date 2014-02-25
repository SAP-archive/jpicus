package com.sap.tools.jpicus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sap.tools.jpicus.client.CalibrationData;
import com.sap.tools.jpicus.impl.serializable.CalibrationDataImpl;

public class Calibrator {

	
	/**
	 * 
	 * @param file
	 * @param iterations
	 * @param bufSize
	 * @return throughput in MB/s
	 */
	private static double getReadThroughput(final File file, final int bufSize) {
					
		byte []  buf = new byte [bufSize];
		
		long start = System.currentTimeMillis();
		FileInputStream fis = null;
		int total = 0;
		try {
	
			fis = new FileInputStream(file);
			while(true){	
				int bytesRead = fis.read(buf);
				if(bytesRead == -1){
					break;
				} else {
					total += bytesRead;
				}
			}
			
		} catch (IOException e){
			
			e.printStackTrace();
			
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		start = System.currentTimeMillis() - start;		
		double throughput = (total / (double)start ) / 1000; // in MB/s
		return throughput;
	}
	
	/**
	 * 
	 * @param file
	 * @param iterations
	 * @param bufSize
	 * @return throughput in MB/s
	 */
	private static double getWriteThroughput(final File file, final int iterations, final int bufSize) {
					
		byte []  buf = new byte [bufSize];
		
		long start = System.currentTimeMillis();
		FileOutputStream fos = null;
		try {
	
			fos = new FileOutputStream(file);
			for(int i =0; i<iterations; i++){	
				fos.write(buf);
				fos.flush();
			}
			
		} catch (IOException e){
			
			e.printStackTrace();
			
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		start = System.currentTimeMillis() - start;		
		double throughput = (iterations * bufSize / (double)start ) / 1000; // in MB/s
		return throughput;
	}

	
	public static CalibrationData calibrate() {

		final int [] bufSizes = new int [] {2048, 4096, 8192, 16384, 32768, 65536};
		List<Double> readThroughputs = new ArrayList<Double>(bufSizes.length);
		List<Double> writeThroughputs = new ArrayList<Double>(bufSizes.length);
		File file = new File(Agent.CALIBRATION_FILE);		
		
		for(int i = 0; i < bufSizes.length; i++){

			int bufSize = bufSizes[i];
	
			file.delete();
//			long free = file.getFreeSpace();
			
			long free = 0;// TODO java SE 5
			
			int fileSize = 150 * 1000 * 1000; // 150MB
			if(fileSize > free){
				System.err.println("Not enough space to calibrate. Required " + fileSize + " available " + free);
				return null;
			}

			final int iterations =  fileSize / bufSize ;

			
			double writeThroughput = getWriteThroughput(file, iterations, bufSize);
			writeThroughputs.add(i, writeThroughput);
			
			// the file shall stay from the write calibration
			double readThroughput = getReadThroughput(file, bufSize);
			readThroughputs.add(i, readThroughput);
			file.delete();
		}

		int maxReadIndex = getMaxIndex(readThroughputs);
		int maxWriteIndex = getMaxIndex(writeThroughputs);
		
		String writeDescription = "The maximum write throughput is " + writeThroughputs.get(maxWriteIndex) +
								  " MB/s at buffer size " + bufSizes[maxWriteIndex] + " bytes";
		
		String readDescription = "The maximum read throughput is " + readThroughputs.get(maxReadIndex) +
		  " MB/s at buffer size " + bufSizes[maxReadIndex] + " bytes";


		
		return new CalibrationDataImpl(readDescription + writeDescription);
	}

	private static int getMaxIndex(List<Double> list) {
		
		double max = 0;
		int index = 0;
		for(int i = 0 ; i < list.size(); i++){
			double d = list.get(i);
			if(d > max){
				max = d;
				index = i;
			}
		}
		return index;
	}

}
