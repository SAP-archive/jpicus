package com.sap.tools.jpicus.impl.transformers.deleteoperations;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;

import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;



public class DeleteOperationsTransformer extends AbstractTransformer {

	
	
	private Set<String> classNames;
	
	public DeleteOperationsTransformer(){
		
		this.classNames = new HashSet<String>(1);
		this.classNames.add("java/io/File");
		
	}
	
	@Override
	protected ClassVisitor getClassVisitor(ClassVisitor next) {
		
		return new FileClassVisitor(next);
		
	}


	@Override
	protected Set<String> getClassNames() {
		
		return this.classNames;
	}
		
} 
