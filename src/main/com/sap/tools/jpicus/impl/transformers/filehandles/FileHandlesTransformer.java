package com.sap.tools.jpicus.impl.transformers.filehandles;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;

import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;



public class FileHandlesTransformer extends AbstractTransformer {

	private Set<String> classNames;
	
	public FileHandlesTransformer(Set<Class<?>> fileChannelImplClasses){
		
		this.classNames = new HashSet<String>(4);
		this.classNames.add("java/io/FileInputStream");
		this.classNames.add("java/io/FileOutputStream");
		this.classNames.add("java/io/RandomAccessFile");
		this.classNames.add("java/util/zip/ZipFile");
//		for(Class<?> cls:fileChannelImplClasses){
//			this.classNames.add(cls.getName().replace('.','/'));	
//		}
	}
	
	@Override
	protected ClassVisitor getClassVisitor(ClassVisitor next) {
		
		return new FileHandlesClassVisitor(next);
		
	}


	@Override
	protected Set<String> getClassNames() {
		
		return this.classNames;
	}

	
	
	
} 
