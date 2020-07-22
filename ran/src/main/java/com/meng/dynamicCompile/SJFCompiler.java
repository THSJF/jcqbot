package com.meng.dynamicCompile;

import com.meng.*;
import com.meng.modules.*;
import java.io.*;
import java.util.*;
import javax.tools.*;

/**
 * @author 司徒灵羽
 */

public class SJFCompiler {
	
	public SJFCompiler() {

	}

	public boolean start(String className, String content) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		Autoreply.sendMessage(Autoreply.yysGroup, 0, "初始化编译器");
		final List<ByteArrayJavaClass> classFileObjects = new ArrayList<>();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		fileManager = new ForwardingJavaFileManager<JavaFileManager>(fileManager){
			public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling)throws IOException {
				ByteArrayJavaClass fileObject = new ByteArrayJavaClass(className);
				classFileObjects.add(fileObject);
				return fileObject;
			}
		};
		Autoreply.sendMessage(Autoreply.yysGroup, 0, "读取源码");
		JavaFileObject source = buildSource(className, content);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, Arrays.asList(source));
		boolean result = task.call();
		for (Diagnostic<? extends JavaFileObject> d:diagnostics.getDiagnostics()) {
			System.out.println(d.getKind() + ":" + d.getMessage(null));
		}
		fileManager.close();
		if (!result) {
			Autoreply.sendMessage(Autoreply.yysGroup, 0, "编译失败");
			System.out.println("compile failed");
			return false;
		}
		Map<String,byte[]> byteCodeMap = new HashMap<>();
		for (ByteArrayJavaClass cl:classFileObjects) {
			byteCodeMap.put(cl.getName().substring(1), cl.getBytes());
			System.out.println(cl.getName().substring(1));
		}
		ClassLoader loader = new MapClassLoader(byteCodeMap);
		Object o = loader.loadClass(className).newInstance();
		Autoreply.sendMessage(Autoreply.yysGroup, 0, "加载类成功");
		ModuleManager.instance.loadModules(o);
		Autoreply.sendMessage(Autoreply.yysGroup, 0, "载入成功");
		return true;
	}

	private static JavaFileObject buildSource(String name, String content) {
		StringBuilderJavaSource source = new StringBuilderJavaSource(name);
		source.append(content);
		return source;
	}
}
