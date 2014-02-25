package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import java.nio.channels.FileChannel;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.AbstractTransfomerTest;
import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;
import com.sap.tools.jpicus.impl.util.Util;


public class FileChannelIOTransformerTest extends AbstractTransfomerTest {
	
	String []  STRINGS_PRESENT = new String [] {Agent.AGENT_CLASS,
												"fileChannelInput",
												"startIO",
												"fileChannelOutput",
												"fileChannelInputScatter",
												"fileChannelOutputGather",
												"fileChannelInputDirectTransfer",
												"fileChannelOutputDirectTransfer",
												"associateMappedByteBuffer"};
	
	String []  STRINGS_NOT_PRESENT = new String []{ "deleteOperation", "fileOpened", "fileClosed", "fileOutput" };

	private Set<Class<?>> classes;
	
	private String className;
	
	@Before
	public void prepare() throws Exception {
		classes = Util.getNIOPrivateImplementationClasses().get(FileChannel.class);
//		System.out.println("Classes: " + classes);
		Assert.assertEquals("Classes count", 1, classes.size());
		className = classes.iterator().next().getName().replace('.', '/');
//		System.out.println("Class name: " + className);
		
	}
	
	@Override
	protected String getClassName() {
		return this.className;
	
	}

	@Override
	protected String[] getStringsNotPresent() {
		return STRINGS_NOT_PRESENT;
	
	}

	@Override
	protected String[] getStringsPresent() {
		return STRINGS_PRESENT;
	}

	@Override
	protected AbstractTransformer getTransformer() {

		return new FileChannelIOTransformer(classes);
	}

	@Test
	public void test() throws Exception {
		testTransform();
	}
	
}
