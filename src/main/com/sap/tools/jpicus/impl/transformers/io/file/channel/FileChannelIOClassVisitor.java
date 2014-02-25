package com.sap.tools.jpicus.impl.transformers.io.file.channel;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;


public class FileChannelIOClassVisitor extends ClassAdapter {

	public FileChannelIOClassVisitor(ClassVisitor next) {
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
		
		if(name.equals("read") ){
		
			if(descriptor.equals("(Ljava/nio/ByteBuffer;)I") || descriptor.equals("(Ljava/nio/ByteBuffer;J)I")){

				return new JSRInlinerAdapter(new FileChannelSingleBufferMethodVisitor(mv,true),
											 access,
											 name,
											 descriptor,
											 signature,
											 exceptions);
				
			}
			
			// Don't cover gathering and scattering with a single argument because thse are
			// final methods in FileChannel and will most likely delegate to the implementation somehow
			// descriptor.equals("([Ljava/nio/ByteBuffer;)J") || 
			if(descriptor.equals("([Ljava/nio/ByteBuffer;II)J")){
				return new JSRInlinerAdapter(new FileChannelMultipleBuffersMethodVisitor(mv,true),
						 access,
						 name,
						 descriptor,
						 signature,
						 exceptions);
			}
	
		}
		
		if(name.equals("write") ){
			
			if(descriptor.equals("(Ljava/nio/ByteBuffer;)I") || descriptor.equals("(Ljava/nio/ByteBuffer;J)I")){

				return new JSRInlinerAdapter(new FileChannelSingleBufferMethodVisitor(mv,false),
						 access,
						 name,
						 descriptor,
						 signature,
						 exceptions);
			} 
			// descriptor.equals("([Ljava/nio/ByteBuffer;)J") ||
			if( descriptor.equals("([Ljava/nio/ByteBuffer;II)J")){
				
				return new JSRInlinerAdapter( new FileChannelMultipleBuffersMethodVisitor(mv,false),
						 access,
						 name,
						 descriptor,
						 signature,
						 exceptions);
			}
	
		}
		
		// direct transfer
		if(name.equals("transferTo") && descriptor.equals("(JJLjava/nio/channels/WritableByteChannel;)J")){
			
			return new JSRInlinerAdapter(new FileChannelDirectTransferMethodVisitor(mv,true),
					 access,
					 name,
					 descriptor,
					 signature,
					 exceptions);
		}
		
		if(name.equals("transferFrom") && descriptor.equals("(Ljava/nio/channels/ReadableByteChannel;JJ)J")){
			return new JSRInlinerAdapter(new FileChannelDirectTransferMethodVisitor(mv,false),
					 access,
					 name,
					 descriptor,
					 signature,
					 exceptions);
		}
		
		// Memory mapped
		if(name.equals("map") && descriptor.equals("(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;")){
			return new JSRInlinerAdapter(new FileChannelMappedByteBufferMethodVisitor(mv),
					 access,
					 name,
					 descriptor,
					 signature,
					 exceptions);
		}
		
		
		// for all others don't instrument
		return mv;
	}

}
