package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.LRETURN;

import org.objectweb.asm.MethodVisitor;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;

public class FileChannelMultipleBuffersMethodVisitor extends InsertAroundMethodVisitor {

	private boolean read;
	int [] opcodes = new int [] {LRETURN};
	
	public FileChannelMultipleBuffersMethodVisitor(MethodVisitor mv, boolean read) {
		
		super(mv);
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
			methodName = "fileChannelInputScatter";
		} else {
			methodName = "fileChannelOutputGather";
		}
		
		// TODO warning the result of the method is not int but long
		// these probably occupy two slots on the stack instead of one
		//mv.visitInsn(DUP); // the original result
		mv.visitVarInsn(ALOAD, 0); // this
		
		// void fileChannelInputScatter/fileChannelOutputGather(long byteCount, FileChannel channel)
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, methodName, "(JLjava/nio/channels/FileChannel;)J");
		
	}
	
	@Override
	protected int[] getReturnInstructions() {
		
		return opcodes;
	}
	
	
	
}
