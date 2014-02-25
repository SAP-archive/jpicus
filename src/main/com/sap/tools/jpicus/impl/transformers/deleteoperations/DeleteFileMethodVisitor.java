package com.sap.tools.jpicus.impl.transformers.deleteoperations;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;

public class DeleteFileMethodVisitor extends InsertAroundMethodVisitor {

	int [] opcodes = new int []{Opcodes.IRETURN};
	
	public DeleteFileMethodVisitor(MethodVisitor next) {
		super(next);
	}


	@Override
	protected void insertAfter() {

		// duplicate the return value on the stack, push this on the stack and invoke
		mv.visitInsn(DUP);  // first argument
		mv.visitVarInsn(ALOAD, 0); // second argument
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, "deleteOperation", "(ZLjava/io/File;)V");

	}


	@Override
	protected int [] getReturnInstructions() {
		
		return opcodes;
	}

}
