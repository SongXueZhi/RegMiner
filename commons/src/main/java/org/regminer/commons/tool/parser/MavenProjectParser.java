package org.regminer.commons.tool.parser;

import org.apache.maven.model.Model;
import org.regminer.commons.model.ModuleNode;
import org.regminer.commons.tool.MavenManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/8 16:51
 */

public class MavenProjectParser extends AbstractProjectParser {
    private final MavenManager mavenManager = new MavenManager();
    private static final String CONFIG_FILE = "pom.xml";

    @Override
    protected boolean isValidProjectRoot(File directory) {
        File pomFile = new File(directory, CONFIG_FILE);
        return pomFile.exists();
    }


    @Override
    protected ModuleNode generateNode(File modelDirectory) {
        File pomFile = new File(modelDirectory, CONFIG_FILE);
        if (!pomFile.exists()) {
            return null;
        }
        try {
            Model model = mavenManager.getPomModel(pomFile);
            return new ModuleNode(model.getArtifactId(), pomFile.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected List<File> getSubModuleDirs(File modelDirectory) {
        List<File> modules = new ArrayList<>();
        File pomFile = new File(modelDirectory, CONFIG_FILE);
        if (!pomFile.exists()) {
            return modules;
        }
        try {
            Model mavenModel  = mavenManager.getPomModel(pomFile);
            List<String> moduleNames = mavenModel.getModules();
            for (String moduleName : moduleNames) {
                modules.add(new File(modelDirectory, moduleName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modules;
    }
}