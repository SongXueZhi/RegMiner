package com.fudan.annotation.platform.backend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    /**
     * properties
     */
    private static Properties properties = null;

    /**
     * 根据key获取value值
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        if (properties == null) {
            properties = loadConfProperties();
        }
        String value = properties.getProperty(key);
        System.out.println("从配置文件读取参数： " + key + " -->> " + value);
        return value;
    }

    /**
     * 初始化propertiies
     *
     * @return
     */
    public static Properties loadConfProperties() {
        Properties properties = new Properties();
        InputStream in = null;

        // 优先从项目路径获取连接信息
        String confPath = System.getProperty("user.dir");
        confPath = confPath + File.separator + "conn.properties";
        File file = new File(confPath);
        if (file.exists()) {
            System.out.println("配置文件路径---->>" + confPath);
            try {
                in = new FileInputStream(new File(confPath));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // 未传入路径时，读取classpath路径
        else {
            System.out.println("项目路径[" + confPath + "]下无连接信息，从classpath路径下加载");
            in = PropertiesUtils.class.getClassLoader().getResourceAsStream("conn.properties");
        }
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}

