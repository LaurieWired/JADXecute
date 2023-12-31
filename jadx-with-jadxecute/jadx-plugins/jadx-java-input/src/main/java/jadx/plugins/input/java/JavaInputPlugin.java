package jadx.plugins.input.java;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import jadx.api.plugins.JadxPluginInfo;
import jadx.api.plugins.input.JadxInputPlugin;
import jadx.api.plugins.input.data.ILoadResult;
import jadx.api.plugins.input.data.impl.EmptyLoadResult;
import jadx.plugins.input.java.utils.JavaClassParseException;

public class JavaInputPlugin implements JadxInputPlugin {

	public static final JadxPluginInfo PLUGIN_INFO = new JadxPluginInfo(
			"java-input",
			"JavaInput",
			"Load .class and .jar files");

	@Override
	public JadxPluginInfo getPluginInfo() {
		return PLUGIN_INFO;
	}

	@Override
	public ILoadResult loadFiles(List<Path> inputFiles) {
		return loadClassFiles(inputFiles, null);
	}

	public static ILoadResult loadClassFiles(List<Path> inputFiles, @Nullable Closeable closeable) {
		List<JavaClassReader> readers = new JavaInputLoader().collectFiles(inputFiles);
		if (readers.isEmpty()) {
			return EmptyLoadResult.INSTANCE;
		}
		return new JavaLoadResult(readers, closeable);
	}

	public static ILoadResult loadClassFiles(List<Path> inputFiles) {
		return loadClassFiles(inputFiles, null);
	}

	/**
	 * Method for provide several inputs by using load methods from {@link JavaInputLoader} class.
	 */
	public static ILoadResult load(Function<JavaInputLoader, List<JavaClassReader>> loader) {
		return wrapClassReaders(loader.apply(new JavaInputLoader()));
	}

	/**
	 * Convenient method for load class file or jar from input stream.
	 * Should be used only once per JadxDecompiler instance.
	 * For load several times use {@link JavaInputPlugin#load(Function)} method.
	 */
	public static ILoadResult loadFromInputStream(InputStream in, String fileName) {
		try {
			return wrapClassReaders(new JavaInputLoader().loadInputStream(in, fileName));
		} catch (Exception e) {
			throw new JavaClassParseException("Failed to read input stream", e);
		}
	}

	/**
	 * Convenient method for load single class file by content.
	 * Should be used only once per JadxDecompiler instance.
	 * For load several times use {@link JavaInputPlugin#load(Function)} method.
	 */
	public static ILoadResult loadSingleClass(byte[] content, String fileName) {
		JavaClassReader reader = new JavaInputLoader().loadClass(content, fileName);
		return new JavaLoadResult(Collections.singletonList(reader));
	}

	public static ILoadResult wrapClassReaders(List<JavaClassReader> readers) {
		if (readers.isEmpty()) {
			return EmptyLoadResult.INSTANCE;
		}
		return new JavaLoadResult(readers);
	}
}
