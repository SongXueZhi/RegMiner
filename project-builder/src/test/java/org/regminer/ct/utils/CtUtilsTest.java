package org.regminer.ct.utils;

import junit.framework.TestCase;
import org.junit.Test;
import org.regminer.common.model.ModuleNode;
import org.regminer.common.model.RelatedTestCase;

import java.io.File;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/10 17:22
 */

public class CtUtilsTest extends TestCase {
    @Test
    public void testGetModulePath() {
        // 创建模拟的多级模块结构
        ModuleNode root = new ModuleNode("root", new File("/path/to/root"));
        ModuleNode moduleLevel1 = new ModuleNode("moduleLevel1", new File("/path/to/root/moduleLevel1"));
        ModuleNode moduleLevel2 = new ModuleNode("moduleLevel2", new File("/path/to/root/moduleLevel1/moduleLevel2"));
        root.addSubModule(moduleLevel1);
        moduleLevel1.addSubModule(moduleLevel2);

        // 创建一个示例的 RelatedTestCase
        RelatedTestCase testCase = new RelatedTestCase();
        testCase.setRelativeFilePath("moduleLevel2" + File.separator + "SomeTest.java");

        // 测试 getModulePath 方法
        String modulePath = CtUtils.getModulePath(root, testCase);
        assertEquals("moduleLevel1" + File.separator + "moduleLevel2", modulePath);
    }

}