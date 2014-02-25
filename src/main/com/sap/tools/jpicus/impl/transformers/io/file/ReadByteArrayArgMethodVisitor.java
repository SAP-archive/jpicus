package com.sap.tools.jpicus.impl.transformers.io.file;

import static org.objectweb.asm.Opcodes.DUP;

import org.objectweb.asm.MethodVisitor;


public class ReadByteArrayArgMethodVisitor extends AbstractFileIOMethodVisitor {
	
	public ReadByteArrayArgMethodVisitor(MethodVisitor next, boolean read) {
		super(next, read);
	}

	@Override
	protected void calculateByteCount() {
		/*
		 * The read method that takes byte array as an 
		 * argument always returns the number of bytes read
		 */
		mv.visitInsn(DUP);
	}

}
