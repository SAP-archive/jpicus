package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;

import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;

public class FileChannelIOTransformer extends AbstractTransformer {

	private Set<String> classNames;
	
	/**
	 * 
	 * @param fileChannelImplClasses the FileChannel implementation class names.
	 * These are not known in advance since FileChannel is an abstract
	 * class that is implemented by the JVM vendor.
	 */
	public FileChannelIOTransformer(Set<Class<?>> fileChannelImplClasses){
		
		this.classNames = new HashSet<String>(fileChannelImplClasses.size());
		
		for(Class<?> cls:fileChannelImplClasses){
			this.classNames.add(cls.getName().replace('.', '/'));
		}
	}
	
	@Override
	protected Set<String> getClassNames() {

		return classNames;
	}

	@Override
	protected ClassVisitor getClassVisitor(ClassVisitor next) {
		return new FileChannelIOClassVisitor(next);
	}

}
