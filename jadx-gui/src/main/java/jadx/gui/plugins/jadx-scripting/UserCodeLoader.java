package jadx.gui.plugins.jadxscripting;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jadx.gui.ui.MainWindow;

// Example code loading from https://gist.github.com/chrisvest/9873843
// TODO: check and see if this can be more efficient
public class UserCodeLoader {
	public String runInputCode(String program, MainWindow mainWindow) throws Exception {
		// Compile the input code
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		JavaFileObject compilationUnit = new StringJavaFileObject("UserCodeClass", program);
		SimpleJavaFileManager fileManager = new SimpleJavaFileManager(compiler.getStandardFileManager(diagnostics, null, null));
		JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, null, null, Arrays.asList(compilationUnit));
		boolean success = compilationTask.call();
		CompiledClassLoader classLoader = new CompiledClassLoader(fileManager.getGeneratedOutputFiles());

		// Check for compilation errors
		if (!success) {
			// Append error message to a string
			// Need to using html tags for JPanel to respect newlines
			String errorMessage = "Java compilation error:\n";
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				errorMessage += diagnostic.getMessage(null) + "\n";
			}
			return errorMessage;
		}

		// Input code must implement method "public static String userCodeMain(MainWindow mainWindow)"
		// Return string will be console output
		// This method can invoke other methods of user code placed inside
		Class<?> userCodeClass = classLoader.loadClass("UserCodeClass");
		Method method = userCodeClass.getMethod("userCodeMain", MainWindow.class);

		return (String) method.invoke(null, mainWindow);
	}

	private static class StringJavaFileObject extends SimpleJavaFileObject {
		private final String code;

		public StringJavaFileObject(String name, String code) {
		  super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
			  Kind.SOURCE);
		  this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		  return code;
		}
	}

	private static class ClassJavaFileObject extends SimpleJavaFileObject {
		private final ByteArrayOutputStream outputStream;
		private final String className;

		protected ClassJavaFileObject(String className, Kind kind) {
		  super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
		  this.className = className;
		  outputStream = new ByteArrayOutputStream();
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
		  return outputStream;
		}

		public byte[] getBytes() {
		  return outputStream.toByteArray();
		}

		public String getClassName() {
		  return className;
		}
	}

	private static class SimpleJavaFileManager extends ForwardingJavaFileManager {
		private final List<ClassJavaFileObject> outputFiles;

		protected SimpleJavaFileManager(JavaFileManager fileManager) {
		  super(fileManager);
		  outputFiles = new ArrayList<ClassJavaFileObject>();
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
		  ClassJavaFileObject file = new ClassJavaFileObject(className, kind);
		  outputFiles.add(file);
		  return file;
		}

		public List<ClassJavaFileObject> getGeneratedOutputFiles() {
		  return outputFiles;
		}
	}

	private static class CompiledClassLoader extends ClassLoader {
		private final List<ClassJavaFileObject> files;

		private CompiledClassLoader(List<ClassJavaFileObject> files) {
		  this.files = files;
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
		  Iterator<ClassJavaFileObject> itr = files.iterator();
		  while (itr.hasNext()) {
			ClassJavaFileObject file = itr.next();
			if (file.getClassName().equals(name)) {
			  itr.remove();
			  byte[] bytes = file.getBytes();
			  return super.defineClass(name, bytes, 0, bytes.length);
			}
		  }
		  return super.findClass(name);
		}
	}
}