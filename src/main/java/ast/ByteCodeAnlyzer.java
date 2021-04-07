package ast;

import java.io.IOException;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;

import utils.CodeUtil;

public class ByteCodeAnlyzer {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

//		JavaClass clazz = Repository.lookupClass(
//				"/home/sxz/Desktop/fastjson/meta/target/test-classes/com/alibaba/fastjson/serializer/SerializeWriterToBytesTest");
//		// TODO Auto-generated method stub
//		System.out.println(clazz);
//		printCode(clazz.getMethods());
		try {
			JavaClass classz = CodeUtil.lookupClass(
					"/home/sxz/Desktop/fastjson/meta/target/test-classes/com/alibaba/fastjson/serializer/SerializeWriterToBytesTest");
			List<String> mc = new ClassVisitor(classz).start().methodCalls();
			for (String m : mc) {
				System.out.println(m);
			}
		} catch (Exception ce) {

		}
	}
}
