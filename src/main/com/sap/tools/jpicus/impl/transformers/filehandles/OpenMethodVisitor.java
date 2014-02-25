package com.sap.tools.jpicus.impl.transformers.filehandles;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;


public class OpenMethodVisitor extends InsertAroundMethodVisitor {

	int [] opcodes = new int []{Opcodes.RETURN};
	
	public OpenMethodVisitor(MethodVisitor next) {
		super(next);
	}


	@Override
	protected void insertAfter() {

		/*
		 * All classes that open files have the file path as their first
		 * argument (and the this reference at slot 0)
		 * Insert a callback to the agent fileOpened(Object path, Object handle)
		 * ALOAD 1
		 * ALOAD 0
		 * INVOKESTATIC com/sap/tools/jpicus/Agent.fileOpened(Ljava/lang/Object;Ljava/lang/Object;)V
		 * 
		 */
		mv.visitVarInsn(ALOAD, 1); // first argument
		mv.visitVarInsn(ALOAD, 0); // second argument
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, "fileOpened", "(Ljava/lang/Object;Ljava/lang/Object;)V");

	}


	@Override
	protected int [] getReturnInstructions() {
		
		return opcodes;
	}
	
	
//	@Override
//	public void visitMaxs(int maxStack, int maxLocals) {
//		
//		/*
//		 * add 2 to the size of the stack in order to 
//		 * accommodate the two additional operands (this and arg0)
//		 * that are needed in order to invoke the registry
//		 */
//		this.mv.visitMaxs(maxStack + 2, maxLocals);
////		super.visitMaxs(maxStack, maxLocals);
//	}
	
}
