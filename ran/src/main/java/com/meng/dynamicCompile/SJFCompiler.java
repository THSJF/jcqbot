package com.meng.dynamicCompile;
import javax.tools.*;
import java.util.*;
import java.io.*;
import com.meng.modules.*;

public class SJFCompiler {
	public SJFCompiler() {

	}

	public boolean start(String className, String content) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final List<ByteArrayJavaClass> classFileObjects = new ArrayList<>();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		JavaFileManager fileManager=compiler.getStandardFileManager(diagnostics, null, null);
		fileManager = new ForwardingJavaFileManager<JavaFileManager>(fileManager){
			public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling)throws IOException {
				ByteArrayJavaClass fileObject = new ByteArrayJavaClass(className);
				classFileObjects.add(fileObject);
				return fileObject;
			}
		};
		JavaFileObject source = buildSource(className, content);
		JavaCompiler.CompilationTask task=compiler.getTask(null, fileManager, diagnostics, null, null, Arrays.asList(source));
		boolean result= task.call();
		for (Diagnostic<? extends JavaFileObject> d:diagnostics.getDiagnostics()) {
			System.out.println(d.getKind() + ":" + d.getMessage(null));
		}
		fileManager.close();
		if (!result) {
			System.out.println("compile failed");
			return false;
		}
		Map<String,byte[]> byteCodeMap=new HashMap<>();
		for (ByteArrayJavaClass cl:classFileObjects) {
			byteCodeMap.put(cl.getName().substring(1), cl.getBytes());
		}
		ClassLoader loader=new MapClassLoader(byteCodeMap);
		Object o = loader.loadClass(className).newInstance();
		ModuleManager.instance.loadModules(o);
		return true;
	}

	private static JavaFileObject buildSource(String name, String content) {
		StringBuilderJavaSource source=new StringBuilderJavaSource(name);
		source.append(content);
		return source;
	}
}
