package utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.tools.ant.DirectoryScanner;

public class CodeUtil {

	public static JavaClass lookupClassbyFile(String resourceName) throws Exception {
		byte[] b = new byte[(int) new File(resourceName).length()];
		InputStream in = new FileInputStream(resourceName);
		new DataInputStream(in).readFully(b);
		in.close();
		ClassParser parser = new ClassParser(new ByteArrayInputStream(b), resourceName);
		boolean parsedClass = false;
		JavaClass javaClass = parser.parse();
		parsedClass = true;
		return javaClass;
	}

	public static Method getMethodByName(JavaClass clazz, String methodName) {
		Method[] ms = clazz.getMethods();
		for (Method method : ms) {
			String s = method.getSignature();
			String ss = method.toString();
			System.out.println("");
		}
		return null;
	}

	public static Repository getRepository(String meta) {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(new File(meta));
		scanner.setIncludes(new String[] { "**/*.class" });
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] files = scanner.getIncludedFiles();
		Repository repository = SyntheticRepository.getInstance(new ClassPath(ClassPath.SYSTEM_CLASS_PATH, meta));
		for (int i = 0; i < files.length; i++) {
			File f = new File(meta, files[i]);
			JavaClass oldclazz = extractClass(f, repository);
			repository.storeClass(oldclazz);
		}
		return repository;
	}

	private static JavaClass extractClass(File f, Repository repository) {
		InputStream is = null;
		try {
			is = new FileInputStream(f);
			ClassParser parser = new ClassParser(is, f.getName());
			JavaClass clazz = parser.parse();
			clazz.setRepository(repository);
			return clazz;
		} catch (IOException ex) {
			return null;
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
	}
}
