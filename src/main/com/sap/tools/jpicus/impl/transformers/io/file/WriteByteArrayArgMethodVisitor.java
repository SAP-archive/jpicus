package com.sap.tools.jpicus.impl.transformers.io.file;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;

import org.objectweb.asm.MethodVisitor;


public class WriteByteArrayArgMethodVisitor extends AbstractFileIOMethodVisitor {
	
	public WriteByteArrayArgMethodVisitor(MethodVisitor next, boolean read) {
		super(next, read);
	}

	@Override
	protected void calculateByteCount() {
		/*
		 * The write method that takes only a byte array as an argument
		 * always writes the whole array contents so we just have to 
		 * get the length of the array
		 */
		mv.visitVarInsn(ALOAD, 1);
//		mv.visitFieldInsn(GETFIELD, "[B", "length", "I");
		mv.visitInsn(ARRAYLENGTH);
	}

}
