package com.sap.tools.jpicus.impl.transformers.io.file;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;


public abstract class AbstractFileIOMethodVisitor extends InsertAroundMethodVisitor {

	int [] opcodes = new int [] {Opcodes.IRETURN, Opcodes.RETURN};
	
	private boolean read;
	
	public AbstractFileIOMethodVisitor(MethodVisitor next, boolean read) {
		
		super(next);
		this.read = read;
	}

	
	@Override
	protected void insertBefore() {
		// notify the agent that an I/O opreation is starting
		mv.visitVarInsn(ALOAD, 0); // this
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, "startIO", "(Ljava/lang/Object;)V");

	}

	
	@Override
	protected void insertAfter() {

		String methodName = null;
		if(read){
			methodName = "fileInput";
		} else {
			methodName = "fileOutput";
		}
		
		calculateByteCount();
		mv.visitVarInsn(ALOAD, 0); // this
		
		// fileInput/fileOutput(int byteCount, Object handle)
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, methodName, "(ILjava/lang/Object;)V");

	}
	
//	@Override
//	protected void insertAfter() {
//
//		calculateByteCount();
//		
//		// Calculate the time spent
//		mv.visitFieldInsn(GETFIELD, owner, FileIOClassVisitor.IO_TIMER, "J");
//		mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J");
//		mv.visitInsn(LSUB); // subtract and leave it on the stack as first argument
//
//		mv.visitVarInsn(ALOAD, 0); // this
//		
//		String methodName = null;
//		if(read){
//			methodName = "fileInput";
//		} else {
//			methodName = "fileOutput";
//		}
//		// fileInput/fileOutput(long time, int byteCount, Object handle)
//		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, methodName, "(IJLjava/lang/Object;)V");
//
//	}

	/**
	 * Calculate the byteCount and leave it on the stack as second argument
	 */
	protected abstract void calculateByteCount();

	@Override
	protected int [] getReturnInstructions() {
		
		return opcodes;
	}
	
	
}
