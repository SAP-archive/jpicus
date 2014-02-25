package com.sap.tools.jpicus.impl.transformers.io.file;

import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.AbstractTransfomerTest;
import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;
import com.sap.tools.jpicus.impl.transformers.io.file.FileIOTransformer;

public class FileInputStreamIOTransfomerTest extends AbstractTransfomerTest {

	String []  STRINGS_PRESENT = new String [] {Agent.AGENT_CLASS, "startIO", "fileInput", "associateFileChannel" };
	String []  STRINGS_NOT_PRESENT = new String []{ "deleteOperation", "fileOpened", "fileClosed", "fileOutput" };
	String className = "java/io/FileInputStream";
	
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
		
		return new FileIOTransformer();
	}
	
}
