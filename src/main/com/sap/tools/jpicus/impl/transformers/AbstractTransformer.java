package com.sap.tools.jpicus.impl.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;


/**
 * Defines the common class file transformer behaviour.
 * There shall be one transformer per class name registered in the
 * instrumentation. The name is passed in the constructor.
 *  
 * @author pavel
 *
 */
public abstract class AbstractTransformer implements ClassFileTransformer {

	
	
	public byte[] transform(ClassLoader loader,
							String name,
							Class<?> clazz,
							ProtectionDomain arg3,
							byte[] bytes) throws IllegalClassFormatException {
		

		if( ! this.getClassNames().contains(name) ){
			return null;
		}

//		if(name.contains("FileChannel")){
//			System.out.println("About to transform: " + name);
//		}
		
		
		ClassReader reader = new ClassReader(bytes);
		
		// compute everything automatically
		// trades instrumentation performance for simplicity and runtime performance
		// e.g. the stack size won't be increased if not necessary
		ClassWriter writer = new ClassWriter(reader,ClassWriter.COMPUTE_FRAMES);
				
		// TODO investigate how we can make use of JSRInlinerAdapter in order to
		// prevent the nasty "JSR/RET are not supported with computeFrames option"
		// runtime exception
		/**
		 * java.lang.RuntimeException: JSR/RET are not supported with computeFrames option
	at org.objectweb.asm.Frame.a(Unknown Source)
	at org.objectweb.asm.MethodWriter.visitJumpInsn(Unknown Source)
	at org.objectweb.asm.MethodAdapter.visitJumpInsn(Unknown Source)
	at org.objectweb.asm.ClassReader.accept(Unknown Source)
	at org.objectweb.asm.ClassReader.accept(Unknown Source)
	at com.sap.tools.jpicus.impl.transformers.AbstractTransformer.transform(AbstractTransformer.java:57)
	at com.sap.tools.jpicus.impl.transformers.AbstractTransfomerTest.testTransform(AbstractTransfomerTest.java:24)
	at com.sap.tools.jpicus.impl.transformers.io.file.channel.FileChannelIOTransformerTest.test(FileChannelIOTransformerTest.java:59)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:616)
	at org.junit.internal.runners.TestMethodRunner.executeMethodBody(TestMethodRunner.java:99)
	at org.junit.internal.runners.TestMethodRunner.runUnprotected(TestMethodRunner.java:81)
	at org.junit.internal.runners.BeforeAndAfterRunner.runProtected(BeforeAndAfterRunner.java:34)
	at org.junit.internal.runners.TestMethodRunner.runMethod(TestMethodRunner.java:75)
	at org.junit.internal.runners.TestMethodRunner.run(TestMethodRunner.java:45)
	at org.junit.internal.runners.TestClassMethodsRunner.invokeTestMethod(TestClassMethodsRunner.java:66)
	at org.junit.internal.runners.TestClassMethodsRunner.run(TestClassMethodsRunner.java:35)
	at org.junit.internal.runners.TestClassRunner$1.runUnprotected(TestClassRunner.java:42)
	at org.junit.internal.runners.BeforeAndAfterRunner.runProtected(BeforeAndAfterRunner.java:34)
	at org.junit.internal.runners.TestClassRunner.run(TestClassRunner.java:52)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:45)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:460)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:673)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:386)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)
		 * 
		 */
		
		ClassVisitor classVisitor = getClassVisitor(writer);
		
		reader.accept(classVisitor, 0);
		
		byte [] result = writer.toByteArray();
		
//		if(this.getClass().getName().equals("com.sap.tools.jpicus.impl.transformers.io.file.FileIOTransformer") && name.equals("java/io/FileOutputStream")){
//
//			System.out.println("Transformation of: " + name + " by transformer " + this.getClass().getName());
//			System.out.println("Original bytes:" + bytes.length);
//			System.out.println("Resulting bytes:" + result.length);
//			trace(name, bytes);
//			trace(name, result);
//		
//		}
		return result;
		
	}
	
//	private void trace(String name, byte[] bytes) {
//
//		ClassReader tracingReader = new ClassReader(bytes);	
//		TraceClassVisitor tracer;
//		try {
//			String home = System.getProperty("user.home");
//			String fileName = home + File.separator + name.replace("/", ".") + bytes.length + "_" + System.currentTimeMillis() + ".txt";
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			
//			tracer = new TraceClassVisitor( new PrintWriter(bos));
//			tracingReader.accept(tracer,0);
//			RandomAccessFile out = new RandomAccessFile(fileName, "rwd");
//			out.write(bos.toByteArray());
//			out.close();
//			System.out.println("Class file written to: " + fileName );
//		} catch (Exception e) {
//
//			e.printStackTrace();
//		}
//		
//	}

	/**
	 * 
	 * @return classNames the names of the classes to which this
	 * transformer is applicable
	 */
	protected abstract Set<String> getClassNames();

	/**
	 * Override this method in order to add your own transformation behavior
	 * 
	 * @param next the next visitor that this one has to delegate to.
	 * @return a class visitor that will be given the class bytes first
	 */
	protected abstract ClassVisitor getClassVisitor(ClassVisitor next);

	
}