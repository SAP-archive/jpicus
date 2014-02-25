package com.sap.tools.jpicus;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.sap.tools.jpicus.impl.util.Util;

public class UtilTest {

	@Test
	public void testAddSuperClassesWithoutA(){
		
		Set<Class<?>> classes = new HashSet<Class<?>>(4);
		classes.add(D.class);
		Util.addSuperClasses(A.class, classes );
		
		Assert.assertEquals("Size", 3, classes.size());
		
		Assert.assertTrue("D", classes.contains(D.class));
		Assert.assertTrue("C", classes.contains(C.class));
		Assert.assertTrue("B", classes.contains(B.class));
	}
	
	@Test
	public void testAddSuperClassesWithoutB(){
		
		Set<Class<?>> classes = new HashSet<Class<?>>(4);
		classes.add(D.class);
		Util.addSuperClasses(B.class, classes );
		
		Assert.assertEquals("Size", 2, classes.size());
		
		Assert.assertTrue("D", classes.contains(D.class));
		Assert.assertTrue("C", classes.contains(C.class));
	}
	
	@Test
	public void testAddSuperClassesDOnly(){
		
		Set<Class<?>> classes = new HashSet<Class<?>>(4);
		classes.add(D.class);
		Util.addSuperClasses(C.class, classes );
		
		Assert.assertEquals("Size", 1, classes.size());
		
		Assert.assertTrue("D", classes.contains(D.class));
	}
	
}

@Ignore
class A {}

@Ignore
class B extends A {}

@Ignore
class C extends B {}

@Ignore
class D extends C {}
