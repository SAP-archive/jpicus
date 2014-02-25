package com.sap.tools.jpicus.impl.transformers.io.file;

import org.objectweb.asm.MethodVisitor;


import static org.objectweb.asm.Opcodes.*;

public class WriteByteArrayIntIntArgMethodVisitor extends AbstractFileIOMethodVisitor {
	
	public WriteByteArrayIntIntArgMethodVisitor(MethodVisitor next, boolean read) {
		super(next, read);
	}

	@Override
	protected void calculateByteCount() {
		/*
		 * The write method that takes a byte array and two 
		 * int arguments always writes a number of bytes equal to
		 * the last argument 
		 */
		mv.visitVarInsn(ILOAD, 3);

	}

}
