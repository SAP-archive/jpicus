package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;

public class FileChannelMappedByteBufferMethodVisitor extends InsertAroundMethodVisitor {

	int [] opcodes = new int [] {Opcodes.ARETURN};
	
	public FileChannelMappedByteBufferMethodVisitor (MethodVisitor mv) {
		
		super(mv);
	}

	@Override
	protected void insertAfter() {
		
		mv.visitInsn(DUP); // the original result (MappedByteBuffer)
		mv.visitVarInsn(ALOAD, 0); // this
		
		// void associateMappedByteBuffer(MappedByteBuffer buf, FileChannel channel)
		mv.visitMethodInsn(INVOKESTATIC, Agent.AGENT_CLASS, "associateMappedByteBuffer", "(Ljava/nio/MappedByteBuffer;Ljava/nio/channels/FileChannel;)V");
		
	}
	
	@Override
	protected int[] getReturnInstructions() {
		
		return opcodes;
	}
	
	
	
}
