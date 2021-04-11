package ast;

import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.Repository;

import utils.CodeUtil;

public class ByteCodeAnlyzer {

	public static void main(String[] args) throws Exception {

//		JavaClass clazz = Repository.lookupClass(
//				"/home/sxz/Desktop/fastjson/meta/target/test-classes/com/alibaba/fastjson/serializer/SerializeWriterToBytesTest");
//		// TODO Auto-generated method stub
//		System.out.println(clazz);
//		printCode(clazz.getMethods());
		String meta = "/home/sxz/Documents/fastjson/";
		String meta2 = "/home/sxz/Documents/fastjson2/";
		Repository repository = CodeUtil.getRepository(meta);
		Repository repository2 = CodeUtil.getRepository(meta2);
		try {
			JavaClass classz = repository.findClass("com.alibaba.fastjson.serializer.SerializeWriterToBytesTest");
			JavaClass classz2 = repository2.findClass(classz.getClassName());
			CodeUtil.getMethodByName(classz, "");
			List<String> mc = new ClassVisitor(classz).start().methodCalls();
			for (String m : mc) {
				System.out.println(m);
			}
		} catch (Exception ce) {

		}
	}

}
