package com.sap.tools.jpicus.impl.transformers.filehandles;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;


public class CloseMethodVisitor extends InsertAroundMethodVisitor {

	int [] opcodes = new int []{Opcodes.RETURN};
	
	public CloseMethodVisitor(MethodVisitor next) {
		super(next);
	}


	@Override
	protected void insertAfter() {

		/*
		 * Insert a callback to the agent
		 * ALOAD 0
		 * INVOKESTATIC com/sap/tools/jpicus/Agent.fileOpened(Ljava/lang/Object;Ljava/lang/Object;)V
		 * 
		 */
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, "fileClosed", "(Ljava/lang/Object;)V");

	}


	@Override
	protected int [] getReturnInstructions() {

		return opcodes;
	}
	
}
