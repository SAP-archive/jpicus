package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;


public class FileChannelSingleBufferMethodVisitor extends InsertAroundMethodVisitor {

	private boolean read;
	int [] opcodes = new int [] {Opcodes.IRETURN};
	
	public FileChannelSingleBufferMethodVisitor(MethodVisitor mv, boolean read) {
		
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
			methodName = "fileChannelInput";
		} else {
			methodName = "fileChannelOutput";
		}
		
		mv.visitInsn(DUP); // the original result (int)
		mv.visitVarInsn(ALOAD, 0); // this
		mv.visitVarInsn(ALOAD, 1); // the first method argument (i.e. ByteBuffer)
		
		// fileChannelInput/fileChannelOutput(long byteCount, FileChannel channel, ByteBuffer buffer)
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, methodName, "(ILjava/nio/channels/FileChannel;Ljava/nio/ByteBuffer;)V");
		
	}
	
	@Override
	protected int[] getReturnInstructions() {
		
		return opcodes;
	}
	
	
	
}
