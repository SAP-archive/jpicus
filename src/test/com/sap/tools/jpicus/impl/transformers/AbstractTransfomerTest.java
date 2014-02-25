package com.sap.tools.jpicus.impl.transformers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;

import com.sap.tools.jpicus.tests.TestUtil;

@Ignore
public abstract class AbstractTransfomerTest {

	
	protected void testTransform() throws Exception {
		
		String classPath = getClassName() + ".class";
		byte [] bytes = TestUtil.getBytes(classPath);
		
//		System.out.println("Got bytes for class " + classPath + " length=" + bytes.length);
		
		AbstractTransformer transformer = getTransformer();
		
		byte [] result = transformer.transform(null, getClassName(), Class.forName(getClassName().replace('/', '.' )), null, bytes );		
		
		try {
			TestUtil.checkClassValidity(result);
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		
		char [] asString = TestUtil.toCharArray(result);
		String string = new String(asString);
		
		for(String s: getStringsPresent() ){
			assertTrue("The string " + s + " is not present in the class textual representation", string.contains(s));
		}
		
		for(String s: getStringsNotPresent() ){
			assertFalse("The string " + s + " is present in the class textual representation", string.contains(s));
		}
				
	}
	
	protected abstract AbstractTransformer getTransformer();

	protected abstract String [] getStringsPresent();
	
	protected abstract String [] getStringsNotPresent();
	
	protected abstract String getClassName();

}
