package com.sap.tools.jpicus.impl.transformers.io.zip;

import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.AbstractTransfomerTest;
import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;

public class ZipFileIOTransfomerTest  extends AbstractTransfomerTest {

	String []  STRINGS_PRESENT = new String [] {Agent.AGENT_CLASS, "wrapInputStream"  };
	String []  STRINGS_NOT_PRESENT = new String []{ "deleteOperation", "fileInput", "fileOutput" };
	String className = "java/util/zip/ZipFile";
	
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
		
		return new ZipFileIOTransofrmer();
	}

}
