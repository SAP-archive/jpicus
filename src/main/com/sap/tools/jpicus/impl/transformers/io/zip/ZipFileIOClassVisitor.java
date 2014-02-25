package com.sap.tools.jpicus.impl.transformers.io.zip;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ZipFileIOClassVisitor extends ClassAdapter {

	public ZipFileIOClassVisitor(ClassVisitor next) {
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
		
		if(	name.equals("getInputStream") ){
		
			// InputStream getInputStream(java.uitl.zip.ZipEntry)
			if(descriptor.equals("(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;")){

				return new WrapInputStreamMethodVisitor(mv);
			}
		} 
		
		// for all others don't instrument
		return mv;
	}

	
	
}
