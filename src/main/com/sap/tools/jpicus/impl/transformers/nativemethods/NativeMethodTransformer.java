package com.sap.tools.jpicus.impl.transformers.nativemethods;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.sap.tools.jpicus.impl.serializable.Options;

public class NativeMethodTransformer implements ClassFileTransformer {

	
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		if(Options.getInstance().isVerbose()){
			System.out.println("NativeTransformer: " + className);	
		}
		
		return null;
	}

}
