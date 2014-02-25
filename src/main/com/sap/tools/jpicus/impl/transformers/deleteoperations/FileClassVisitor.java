package com.sap.tools.jpicus.impl.transformers.deleteoperations;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class FileClassVisitor  extends ClassAdapter {

	public FileClassVisitor(ClassVisitor next) {
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
		
		// the public delete method
		if(	name.equals("delete") &&
			(ACC_PUBLIC & access) == 1){
			
			return new DeleteFileMethodVisitor(mv);
		} 
		
		// for all others don't instrument
		return mv;
	}
}
