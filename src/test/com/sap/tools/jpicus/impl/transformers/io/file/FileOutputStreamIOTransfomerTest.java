package com.sap.tools.jpicus.impl.transformers.io.file;

import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.AbstractTransfomerTest;
import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;
import com.sap.tools.jpicus.impl.transformers.io.file.FileIOTransformer;

public class FileOutputStreamIOTransfomerTest  extends AbstractTransfomerTest {

	String []  STRINGS_PRESENT = new String [] {Agent.AGENT_CLASS, "startIO", "fileOutput", "associateFileChannel" };
	String []  STRINGS_NOT_PRESENT = new String []{ "deleteOperation", "fileInput", "fileOpened", "fileClosed"};
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
		
		return new FileIOTransformer();
	}

}
