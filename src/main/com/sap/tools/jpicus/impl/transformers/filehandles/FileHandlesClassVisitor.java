package com.sap.tools.jpicus.impl.transformers.filehandles;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;


import static org.objectweb.asm.Opcodes.*;

public class FileHandlesClassVisitor extends ClassAdapter {

	public FileHandlesClassVisitor(ClassVisitor next) {
		super(next);
	}

	@Override
	public MethodVisitor visitMethod(int access,
									 String name,
									 String descriptor,
									 String signature,
									 String[] exceptions) {
		
		MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
		if(mv == null){
			return null;
		}
		
		// only iterested in public methods
		if( !( (ACC_PUBLIC & access) == 1) ){
			return mv;
		}
		
		// all public constructors e.g. new FileInputStream(File file)
		if(	name.equals("<init>") ){
		
				return new OpenMethodVisitor(mv);					
		} 
		
		// public void close()
		if(name.equals("close") ){
		
			return new CloseMethodVisitor(mv);
			
		}
		
		// for all others don't instrument
		return mv;
	}
}
