package org.regminer.commons.tool.parser;

import org.regminer.commons.model.ModuleNode;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/8 17:01
 */

public abstract class AbstractProjectParser implements ProjectParser {
    @Override
    public ModuleNode parseProject(File projectDirectory) {
        File topLevelDirectory = findTopLevelModuleDirectory(projectDirectory);
        if (topLevelDirectory == null) {
            throw new IllegalArgumentException("No top-level module found for " + projectDirectory);
        }
        return createModuleNode(topLevelDirectory);
    }

    public ModuleNode createModuleNode(File directory) {
        ModuleNode node = generateNode(directory);
        List<File> subModuleDirs = getSubModuleDirs(directory);
        for (File subModuleDir : subModuleDirs) {
            ModuleNode subModuleNode = createModuleNode(subModuleDir);
            node.addSubModule(subModuleNode);
        }
        return node;
    }

    public File findTopLevelModuleDirectory(File startDir) {
        Queue<File> dirs = new LinkedList<>();
        dirs.add(startDir);

        while (!dirs.isEmpty()) {
            File currentDir = dirs.poll();
            if (isValidProjectRoot(currentDir)) {
                return currentDir;
            }
            // 将当前目录的所有子目录添加到队列中
            File[] subDirs = currentDir.listFiles(File::isDirectory);
            if (subDirs != null) {
                Collections.addAll(dirs, subDirs);
            }
        }
        return null;
    }
    protected abstract boolean isValidProjectRoot(File directory);

    protected abstract ModuleNode generateNode(File modelDirectory);

    protected abstract List<File> getSubModuleDirs(File modelDirectory);
}

