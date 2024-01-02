package org.regminer.fl;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlUtil {

    public static Iterable<Object> readMultipleYaml(String filePath) {
        return readMultipleYaml(new File(filePath));
    }

    public static Iterable<Object> readMultipleYaml(File file) {
        if (!file.exists()) {
            assert false : String.format("readYaml file: %s does not exist!", file.toString());
        }
        Iterable<Object> data = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            Yaml yaml = new Yaml();
            data = yaml.loadAll(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Map<String, Object> readYaml(String filePath) {
        return readYaml(new File(filePath));
    }

    public static Map<String, Object> readYaml(File file) {
        if (!file.exists()) {
            assert false : String.format("readYaml file: %s does not exist!", file.toString());
        }
        Map<String, Object> data = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            Yaml yaml = new Yaml();
            data = yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeYaml(Map<String, Object> data, String filePath) {
        Yaml yaml = new Yaml();
        try {
            PrintWriter writer = new PrintWriter(new File(filePath));
            yaml.dump(data, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}