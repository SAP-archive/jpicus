package com.sap.tools.jpicus.impl.transformers;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

public abstract class InsertAroundMethodVisitor extends MethodAdapter {

	public InsertAroundMethodVisitor(MethodVisitor next){
		super(next);
	}
	

	@Override
	public void visitCode() {
		
		mv.visitCode();
		this.insertBefore();
	}
	
	@Override
	public void visitInsn(int opcode) {
		
		int [] opcodes = getReturnInstructions();
		for (int code : opcodes){
			if(opcode == code ){
				insertAfter();
				break;
			}
		}
		
		mv.visitInsn(opcode);
	}

	protected abstract void insertAfter();
	
	protected void insertBefore(){
		
	}
	
	/**
	 * 
	 * @return the opcodes of the instructions that demarcate the exit of the method
	 */
	protected abstract int [] getReturnInstructions();
	
}

