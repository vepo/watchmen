package io.vepo.watchmen.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class InterceptingClassTransformer implements ClassFileTransformer {

	public void init() {

	}

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		System.out.println();
		System.out.println("Processing class " + className);

		String normalizedClassName = className.replaceAll("/", ".");

		ClassReader classReader = null;
		try {
			classReader = new ClassReader(normalizedClassName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		ClassNode classNode = new ClassNode();
		classReader.accept(classNode, ClassReader.SKIP_DEBUG);

		List<MethodNode> allMethods = classNode.methods;
		for (MethodNode methodNode : allMethods) {
			System.out.println(methodNode.name);
		}
		return classfileBuffer;
	}

	public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		// invoke the legacy transform method
		return transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
	}

}
