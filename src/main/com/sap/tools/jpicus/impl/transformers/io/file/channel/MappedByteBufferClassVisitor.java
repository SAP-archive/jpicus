package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MappedByteBufferClassVisitor extends ClassAdapter {

	public MappedByteBufferClassVisitor(ClassVisitor next) {
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
		
		// only interested in public methods
		if( !( (ACC_PUBLIC & access) == 1 ) ){
			return mv;
		}
		
		// TODO implement
		
		
		// for all others don't instrument
		return mv;
	}

	
}
