package ast;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import utils.CodeUtil;

public class ByteCodeAnlyzer {

	public static void main(String[] args) throws Exception {

		JavaClass clazz = CodeUtil.lookupClassbyFile(
				"/home/sxz/Desktop/fastjson/meta/target/test-classes/com/alibaba/fastjson/serializer/SerializeWriterToBytesTest.class");
//		// TODO Auto-generated method stub
//		System.out.println(clazz);
//		printCode(clazz.getMethods());
//		String meta = "/home/sxz/Documents/fastjson/";
		// String meta2 = "/home/sxz/Documents/fastjson2/";
//		Repository repository = CodeUtil.getRepository(new File(meta));
		// Repository repository2 = CodeUtil.getRepository(new File(meta2));
//		try {
//			JavaClass classz = repository.findClass("com.alibaba.fastjson.serializer.SerializeWriterToBytesTest");
		// JavaClass classz2 = repository2.findClass(classz.getClassName());
//			long a1 = System.currentTimeMillis();
//
//			CodeUtil.getClassFilePath(classz.getClassName(), new File(meta));

//			long a2 = System.currentTimeMillis();
//			System.out.println((a2 - a1));
		// CodeUtil.getMethodByName(classz, "");
//			List<String> mc = new ClassVisitor(clazz).start().methodCalls();
//			for (String m : mc) {
//				System.out.println(m);
//			}
//		} catch (Exception ce) {
//
//		}
		MethodCaller mc = new MethodCaller();
		for (Method m : clazz.getMethods()) {
			mc.getMethodCall(m, clazz);
		}

	}

}
