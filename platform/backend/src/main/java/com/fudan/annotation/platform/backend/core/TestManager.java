package com.fudan.annotation.platform.backend.core;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description:
 *
 * @author Richy
 * create: 2022-03-07 10:55
 **/
public class TestManager {
    final static String REPORT_PATH = "target" + File.separator + "surefire-reports";
    final static String JUNIT_REPORT_PATH = REPORT_PATH + File.separator + "junitreports";
    final static SAXReader reader = new SAXReader();

    public List<String> getErrors(File codePath) {
        File reportDirectory = new File(codePath, JUNIT_REPORT_PATH);
        if (!reportDirectory.exists()) {
            reportDirectory = new File(codePath, REPORT_PATH);
        }

        if (!reportDirectory.exists() || !reportDirectory.isDirectory()) {
            return null;
        }

        List<String> errorMessages = new ArrayList<>();
        for (String file : reportDirectory.list()) {
            if (file.matches("TEST.*\\.xml")) {
                String errorMessage = getError(new File(reportDirectory, file));
                System.out.println(errorMessage);
                if (errorMessage != null) {
                    errorMessages.add(errorMessage);
                }
            }
        }
        return errorMessages;
    }

    private String getError(File codePath) {
        try {
            Document doc = reader.read(codePath);
            Element root = doc.getRootElement();
            Element testCase = root.element("testcase");
            if (testCase == null) {
                return "";
            }
            Element error = testCase.element("error");
            if (error == null) {
                error = testCase.element("failure");
            }

            if (error != null) {
                String errorMsg = error.attributeValue("type");
                if (errorMsg.matches("java.*")) {
                    return errorMsg;
                }
                String errorData = error.getData().toString();
                Pattern p = Pattern.compile("Caused by: (.+)\\s");
                Matcher m = p.matcher(errorData);
                if (m.find()) {
                    String group = m.group(1);
                    int index = group.indexOf(':');
                    if (index >= 0) {
                        group = group.substring(0, index);
                    }
                    return group;
                }
                return errorMsg;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}