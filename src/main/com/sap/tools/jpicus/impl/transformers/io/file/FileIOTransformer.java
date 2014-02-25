 package com.sap.tools.jpicus.impl.transformers.io.file;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;

import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;



public class FileIOTransformer extends AbstractTransformer {

	private Set<String> classNames;
	
	public FileIOTransformer(){
		
		this.classNames = new HashSet<String>(4);
		this.classNames.add("java/io/FileInputStream");
		this.classNames.add("java/io/FileOutputStream");
		this.classNames.add("java/io/RandomAccessFile");
		
	}
	
	@Override
	protected ClassVisitor getClassVisitor(ClassVisitor next) {
		
		return new FileIOClassVisitor(next);
		
	}


	@Override
	protected Set<String> getClassNames() {
		
		return this.classNames;
	}

} 
