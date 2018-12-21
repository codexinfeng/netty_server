package com.netty.server.compiler.weaver;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import com.netty.server.compiler.invoke.ObjectInvoker;
import com.netty.server.core.ReflectionUtils;
import com.netty.server.exception.CreateProxyException;

public class ByteCodeClassTransformer extends AbstractClassTransformer
		implements Opcodes {
	private static final AtomicLong CLASS_NUMBER = new AtomicLong(0);
	private static final String CLASSNAME_PREFIX = "ASMPROXY_";
	private static final String HANDLER_NAME = "__handler";
	private static final Type INVOKER_TYPE = Type.getType(ObjectInvoker.class);

	@Override
	public Class<?> transform(ClassLoader classLoader, Class<?>... proxyClasses) {
		Class<?> superclass = ReflectionUtils.getParentClass(proxyClasses);
		String proxyName = CLASSNAME_PREFIX + CLASS_NUMBER.incrementAndGet();
		Method[] implementationMethods = super
				.findImplementationMethods(proxyClasses);
		Class<?>[] interfaces = ReflectionUtils.filterInterfaces(proxyClasses);
		String classFileName = proxyName.replace('.', '/');

		try {
			byte[] proxyBytes = generate(superclass, classFileName,
					implementationMethods, interfaces);

			return loadClass(classLoader, proxyName, proxyBytes);
		} catch (final Exception e) {
			throw new CreateProxyException(e);
		}
	}

	private byte[] generate(Class<?> classToProxy, String proxyName,
			Method[] methods, Class<?>... interfaces)
			throws CreateProxyException {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		Type proxyType = Type.getObjectType(proxyName);
		String[] interfaceNames = new String[interfaces.length];
		for (int i = 0; i < interfaceNames.length; i++) {
			interfaceNames[i] = Type.getType(interfaces[i]).getInternalName();
		}
		Type superType = Type.getType(classToProxy);
		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, proxyType.getInternalName(),
				null, superType.getInternalName(), interfaceNames);
		cw.visitField(ACC_FINAL + ACC_PRIVATE, HANDLER_NAME,
				INVOKER_TYPE.getDescriptor(), null, null).visitEnd();
		initialize(cw, proxyType, superType);
		for (final Method method : methods) {
			transformMethod(cw, method, proxyType, HANDLER_NAME);
		}
		return cw.toByteArray();

	}

	// cglib��ʵ�ֶ��ֽ���Ŀ���,ƫ��ײ��asm��jvm���ֽ��������Ⱦ֯����ǿ
	// ��ȡ����ǩ��ͨ������ȡ��,���뷽ʽ���,������մ�����ASM��ջ������API����
	private void transformMethod(ClassWriter cw, Method method, Type proxyType,
			String handlerName) throws CreateProxyException {
		int access = (ACC_PUBLIC | ACC_PROTECTED) & method.getModifiers();
		org.objectweb.asm.commons.Method m = org.objectweb.asm.commons.Method
				.getMethod(method);
		GeneratorAdapter adapter = new GeneratorAdapter(access, m, null,
				getTypes(method.getExceptionTypes()), cw);
		// ����ǩ����ջ
		adapter.push(Type.getType(method.getDeclaringClass()));
		adapter.push(method.getName());
		adapter.push(Type.getArgumentTypes(method).length);
		// ����Class����
		Type classType = Type.getType(Class.class);
		adapter.newArray(classType);
		// ��ȡ���������б�
		for (int i = 0; i < Type.getArgumentTypes(method).length; i++) {
			// �ӷ�����ջ����һ�ݲ�������
			adapter.dup();
			// �Ѳ���������ջ
			adapter.push(i);
			adapter.push(Type.getArgumentTypes(method)[i]);
			adapter.arrayStore(classType);
		}
		// ����getDeclaredMethod����
		adapter.invokeVirtual(
				classType,
				org.objectweb.asm.commons.Method
						.getMethod("java.lang.reflect.Method getDeclaredMethod(String,Class[])"));
		adapter.loadThis();
		adapter.getField(proxyType, handlerName, INVOKER_TYPE);
		// ƫ�ƶ�ջָ��
		adapter.swap();
		adapter.loadThis();
		// ƫ�ƶ�ջָ��
		adapter.swap();
		// ��ȡ�����Ĳ���ֵ�б�
		adapter.push(Type.getArgumentTypes(method).length);
		Type objectType = Type.getType(Object.class);
		adapter.newArray(objectType);
		for (int i = 0; i < Type.getArgumentTypes(method).length; i++) {
			// �ӷ�����ջ����һ�ݲ�������
			adapter.dup();
			adapter.push(i);
			adapter.loadArg(i);
			adapter.valueOf(Type.getArgumentTypes(method)[i]);
			adapter.arrayStore(objectType);
		}
		// ���÷���
		adapter.invokeInterface(
				INVOKER_TYPE,
				org.objectweb.asm.commons.Method
						.getMethod("Object invoke(Object,java.lang.reflect.Method,Object[])"));
		// �����ķ���ֵ����
		adapter.unbox(Type.getReturnType(method));
		adapter.returnValue();
		adapter.endMethod();

	}

	private Type[] getTypes(Class<?>... src) {
		Type[] result = new Type[src.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Type.getType(src[i]);
		}
		return result;
	}

	// �ֽ����е�init�����������г�ʼ��
	private void initialize(ClassWriter cw, Type proxyType, Type superType) {
		GeneratorAdapter adapter = new GeneratorAdapter(ACC_PUBLIC,
				new org.objectweb.asm.commons.Method("<init>", Type.VOID_TYPE,
						new Type[] { INVOKER_TYPE }), null, null, cw);
		adapter.loadThis();
		adapter.invokeConstructor(superType,
				org.objectweb.asm.commons.Method.getMethod("void <init> ()"));
		adapter.loadThis();
		adapter.loadArg(0);
		adapter.putField(proxyType, HANDLER_NAME, INVOKER_TYPE);
		adapter.returnValue();
		adapter.endMethod();
	}

	private Class<?> loadClass(ClassLoader loader, String className, byte[] b) {
		try {
			Method method = ClassLoader.class.getDeclaredMethod("defineClass",
					String.class, byte[].class, int.class, int.class);
			boolean accessible = method.isAccessible();
			if (!accessible) {
				method.setAccessible(true);
			}
			try {
				return (Class<?>) method.invoke(loader, className, b,
						Integer.valueOf(0), Integer.valueOf(b.length));
			} finally {
				if (!accessible) {
					method.setAccessible(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
