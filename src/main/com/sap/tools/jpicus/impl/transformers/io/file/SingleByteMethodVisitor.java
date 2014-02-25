package com.sap.tools.jpicus.impl.transformers.io.file;

import static org.objectweb.asm.Opcodes.LCONST_1;

import org.objectweb.asm.MethodVisitor;


public class SingleByteMethodVisitor extends AbstractFileIOMethodVisitor {

	public SingleByteMethodVisitor(MethodVisitor next, boolean read) {
		
		super(next, read);
	}

	@Override
	protected void calculateByteCount() {
		/*
		 * The no arg read method always reads a single byte
		 */
		mv.visitInsn(LCONST_1); 

	}

}
