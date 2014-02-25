package com.sap.tools.jpicus.impl.transformers.io.file;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sap.tools.jpicus.Agent;
import com.sap.tools.jpicus.impl.transformers.InsertAroundMethodVisitor;

public class AssociateFileChannelMethodVisitor extends InsertAroundMethodVisitor {

	public AssociateFileChannelMethodVisitor(MethodVisitor next) {
		super(next);
	}

	int [] opcodes = new int []{Opcodes.ARETURN};
	
	@Override
	protected void insertAfter() {

		// result is already on the stack - first argument
		mv.visitVarInsn(ALOAD, 0); // this  - second argument
		
		// FileChannel associateFileChannel(FileChannel channel, Object handle)
		mv.visitMethodInsn(INVOKESTATIC,
						   Agent.AGENT_CLASS,
						   "associateFileChannel",
						   "(Ljava/nio/channels/FileChannel;Ljava/lang/Object;)Ljava/nio/channels/FileChannel;");

		// after the method call the result shall be on top of the stack so
		// that it can be returned to the caller instead of the real stream
		
	}
	
	@Override
	protected int [] getReturnInstructions() {
		
		return opcodes;
	}
	
}
