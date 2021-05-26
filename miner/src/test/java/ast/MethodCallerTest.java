package ast;

import model.CallNode;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.junit.Before;
import org.junit.Test;
import utils.CodeUtil;

import java.io.File;
import java.util.List;

public class MethodCallerTest {

    JavaClass clazz;

    @Before
    public  void setUp(){
    clazz = CodeUtil.lookupClassbyFile("resouse/Demo.class");
    }

    @Test
    public void testGetMethodCall(){
        Method[] methods = clazz.getMethods();
        for ( Method m :methods
             ) {
            MethodCaller methodCaller =new MethodCaller();
            CallNode node  = methodCaller.getMethodCall(m,clazz);
           List<CallNode> nodes =node.childList;
            for (CallNode no:
                 nodes) {
                System.out.println(node.getMethodName());
            }
        }
    }

}
