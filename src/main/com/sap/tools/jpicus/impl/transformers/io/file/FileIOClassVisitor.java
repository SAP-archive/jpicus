package com.sap.tools.jpicus.impl.transformers.io.file;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;


public class FileIOClassVisitor extends ClassAdapter {

//	public static final String IO_TIMER = "com_sap_tools_jpicus_io_read_timer"; 
	
	public FileIOClassVisitor(ClassVisitor next) {
		super(next);
	}

	@Override
	public MethodVisitor visitMethod(int access,
									 String name,
									 String descriptor,
									 String signature,
									 String[] exceptions) {
		
		
		MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
		if(mv == null){
			return null;
		}
		
		// only interested in public methods
		if( !( (ACC_PUBLIC & access) == 1 ) ){
			return mv;
		}
		
		if(	name.equals("read") ){
		
			// no arg read() method
			if(descriptor.equals("()I")){

				return new SingleByteMethodVisitor(mv,true);
			} else {
				return new ReadByteArrayArgMethodVisitor(mv,true);
			}
	
		} 
		
		if(name.equals("write")){
			// single arg write() method
			if(descriptor.equals("(I)V")){

				return new SingleByteMethodVisitor(mv,false);
			} 
			if(descriptor.equals("([B)V")) {
				return new WriteByteArrayArgMethodVisitor(mv,false);
			}
			if(descriptor.equals("([BII)V")){
				return new WriteByteArrayIntIntArgMethodVisitor(mv,false);
			}
		}
		
		if(name.equals("getChannel")){
			
			if(descriptor.equals("()Ljava/nio/channels/FileChannel;")){

				return new AssociateFileChannelMethodVisitor(mv);
			} 
		}
		
		// for all others don't instrument
		return mv;
	}
	
	
//	@Override
//	public void visitEnd() {
//		/*
//		 * Insert an additional field in order to preserve the current time at the
//		 *  beginning of the I/O method call
//		 */
//		FieldVisitor fv = cv.visitField(ACC_PRIVATE,
//					  					IO_TIMER,
//					  					"J",
//					  					null, // signature
//					  					null // initial value
//					  					);
//		if(fv != null){
//			fv.visitEnd();
//		}
//	
//		cv.visitEnd();
//	}
	
}
