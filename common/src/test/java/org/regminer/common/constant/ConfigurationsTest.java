package org.regminer.common.constant;

import org.junit.Test;
import org.junit.Assert;

public class ConfigurationsTest {
    @Test
    public void testLoadConfig() {
        Assert.assertNotNull(Configurations.jdkDir);
        Assert.assertNotNull(Configurations.j8File);
        System.out.println("jdk8: " + Configurations.j8File);
        Assert.assertTrue(Configurations.j8File.contains("8"));
        Assert.assertTrue(Configurations.j8File.contains("jdk") || Configurations.j8File.contains("java"));
        Assert.assertEquals(Configurations.j8File, Configurations.JDK_FILES[8 - 6]);
    }

    @Test
    public void testGetModuleAbsDir() {
        String moduleDir = Configurations.getModuleAbsDir("miner");
        Assert.assertNotNull(moduleDir);
        Assert.assertTrue(moduleDir.endsWith("miner"));
        moduleDir = Configurations.getModuleAbsDir("");
        Assert.assertNotNull(moduleDir);
        Assert.assertFalse(moduleDir.endsWith(Configurations.SEPARATOR));
        Assert.assertTrue(moduleDir.endsWith("RegMiner"));
    }

}