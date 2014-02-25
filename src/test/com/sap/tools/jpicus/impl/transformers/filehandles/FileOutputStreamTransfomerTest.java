package com.sap.tools.jpicus.impl.transformers.filehandles;

import java.util.HashSet;

import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.AbstractTransfomerTest;
import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;

public class FileOutputStreamTransfomerTest  extends AbstractTransfomerTest {

	String []  STRINGS_PRESENT = new String [] {Agent.AGENT_CLASS, "fileOpened", "fileClosed" };
	String []  STRINGS_NOT_PRESENT = new String []{ "deleteOperation", "fileInput" };
	String className = "java/io/FileOutputStream";
	
	@Override
	protected String getClassName() {
		
		return className;
	}

	@Override
	protected String[] getStringsPresent() {
		
		return STRINGS_PRESENT;
	}
	
	@Override
	protected String[] getStringsNotPresent() {
		
		return STRINGS_NOT_PRESENT;
	}
	
	@Test
	public void test() throws Exception {
		testTransform();
	}
	
	@Override
	protected AbstractTransformer getTransformer() {
		
		return new FileHandlesTransformer(new HashSet<Class<?>>(0));
	
	}

}
