package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;

import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;

public class MappedByteBufferTransformer extends AbstractTransformer {

	Set<String> classNames;
	
	public MappedByteBufferTransformer(Set<Class<?>> mappedbyteBufferClasses){

		this.classNames = new HashSet<String>(mappedbyteBufferClasses.size());
		
		for(Class<?> cls:mappedbyteBufferClasses){
			this.classNames.add(cls.getName());
		}
		
	}
	
	@Override
	protected Set<String> getClassNames() {
		return this.classNames;
	}

	@Override
	protected ClassVisitor getClassVisitor(ClassVisitor next) {
		return new MappedByteBufferClassVisitor(next);
	}

}
