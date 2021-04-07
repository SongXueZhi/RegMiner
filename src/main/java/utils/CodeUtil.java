package utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class CodeUtil {

	public static JavaClass lookupClass(String className) throws Exception {
		String resourceName = className.replace('.', '/') + ".class";
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

}
