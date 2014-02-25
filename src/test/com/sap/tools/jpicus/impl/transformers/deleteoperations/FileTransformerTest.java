package com.sap.tools.jpicus.impl.transformers.deleteoperations;

import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.AbstractTransfomerTest;
import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;

public class FileTransformerTest extends AbstractTransfomerTest {


	String []  STRINGS_PRESENT = new String []{Agent.AGENT_CLASS,"deleteOperation" };
	String []  STRINGS_NOT_PRESENT = new String []{"fileInput", "fileOutput", "fileOpened", "fileClosed" };
	
	String className = "java/io/File";
	
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
		
		return new DeleteOperationsTransformer();
	}
}
