package com.sap.tools.jpicus.impl.transformers.io.zip;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;

import com.sap.tools.jpicus.impl.transformers.AbstractTransformer;

public class ZipFileIOTransofrmer extends AbstractTransformer {

	
	private Set<String> classNames;
	
	public ZipFileIOTransofrmer() {
		this.classNames = new HashSet<String>(1);
		this.classNames.add("java/util/zip/ZipFile");
	}
	
	@Override
	protected Set<String> getClassNames() {
		return this.classNames;
	}

	@Override
	protected ClassVisitor getClassVisitor(ClassVisitor next) {
		return new ZipFileIOClassVisitor(next);
	}

}
