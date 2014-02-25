package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;

public class FileChannelDirectTransferMethodVisitor extends InsertAroundMethodVisitor {

	private boolean read;
	int [] opcodes = new int [] {Opcodes.LRETURN};
	
	public FileChannelDirectTransferMethodVisitor(MethodVisitor mv, boolean read) {
		
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
			methodName = "fileChannelInputDirectTransfer";
		} else {
			methodName = "fileChannelOutputDirectTransfer";
		}
		
//		mv.visitInsn(DUP); // the original result (int)
		mv.visitVarInsn(ALOAD, 0); // this
		
		// void fileChannelInputDirectTransfer/fileChannelOutputDirectTransfer (long byteCount, FileChannel channel)
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, methodName, "(JLjava/nio/channels/FileChannel;)J");
		
	}
	
	@Override
	protected int[] getReturnInstructions() {
		
		return opcodes;
	}
	
	
	
}
