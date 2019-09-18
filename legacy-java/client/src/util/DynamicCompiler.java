package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import menus.ExpandableMenu;

/**
 * This class was copied from the location below and was created by david, the author of the article.
 * This class is used to dynamically compile any .java classes in the directory for custom layer types.
 *
 * http://www.beyondlinux.com
 *
 * @author david 2011/07
 *
 */
public class DynamicCompiler {
	/**
	 * The directory where all compiled classes will be saved. This should be set before use.
	 */
	public static String classOutputFolder = "";
	/**
	 * After a .java file is compiled, it will be saved as 'name.class'. This parameter should be set before each
	 * use to ensure that .class files are named properly.
	 */
	public static String name = "default";

	/**
	 * This class will print diagnostic information about any failed compilations.
	 * @author david 2011/07
	 *
	 */
	public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {
		public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			String display = "";
			display += "Line Number->" + diagnostic.getLineNumber() + "\r\n";
			display += "code->" + diagnostic.getCode() + "\r\n";
			display += "Message->"
                    + diagnostic.getMessage(Locale.ENGLISH) + "\r\n";
			display += "Source->" + diagnostic.getSource() + "\r\n";
			
			ExpandableMenu.displayAlert("Failed to compile custom layer.", "Diagnostic information:", display);
		}
	}

	/**
	 * java File Object represents an in-memory java source file <br>
	 * so there is no need to put the source file on hard disk
	 **/
	public static class InMemoryJavaFileObject extends SimpleJavaFileObject {
		private String contents = null;

		public InMemoryJavaFileObject(String className, String contents) throws Exception {
			super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.contents = contents;
		}

		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return contents;
		}
	}

	/**
	 * Get a simple Java File Object ,<br>
	 * It is just for demo, content of the source code is dynamic in real use
	 * case
	 * @param javaFile the contents of the class to be compiled
	 * @return an instance of the JavaFileObject
	 */
	public static JavaFileObject getJavaFileObject(String javaFile) {
		StringBuilder contents = new StringBuilder(javaFile);
		JavaFileObject so = null;
		try {
			so = new InMemoryJavaFileObject(name, contents.toString());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return so;
	}

	/** compile your files by JavaCompiler. If the class was succesfuly compiled, it will print 'Succeded'. Otherwise, it
	 * will print diagnositc information. 
	 * @param files takes in an Arrays.asList of the JavaFileObject
	 * @throws NullPointerException this is thrown if the java file is run from a JRE instead of a JDK.
	 */
	public static void compile(Iterable<? extends JavaFileObject> files) throws NullPointerException {
		// get system compiler:
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		// for compilation diagnostic message processing on compilation
		// WARNING/ERROR
		MyDiagnosticListener c = new MyDiagnosticListener();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(c, Locale.ENGLISH, null);
		// specify classes output folder
		Iterable options = Arrays.asList("-d", classOutputFolder);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, c, options, null, files);
		Boolean result = task.call();
		if (result == true) {
			System.out.println("Succeeded");
		} else
			System.out.println("Failed");
	}

	/** returns an instance of the compiled class, or null if the class could not be compiled. 
	 * @return a Class object for the compiled class (ie, HistogramLayer.class).
	*/
	public static Class instanceOf() {
		// Create a File object on the root of the directory
		// containing the class file
		File file = new File(classOutputFolder);

		try {
			// Convert File to a URL
			URL url = file.toURL(); // file:/classes/demo
			URL[] urls = new URL[] { url };

			// Create a new class loader with the directory
			ClassLoader loader = new URLClassLoader(urls);

			// Load in the class; Class.childclass should be located in
			// the directory file:/class/demo/
			Class thisClass = loader.loadClass(classOutputFolder + "fractal/" + name);
			return thisClass;
		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}