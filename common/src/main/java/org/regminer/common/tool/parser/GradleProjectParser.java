package org.regminer.common.tool.parser;

import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.regminer.common.model.ModuleNode;
import org.regminer.common.tool.GradleManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/8 16:51
 */
public class GradleProjectParser extends AbstractProjectParser {
    private final GradleManager gradleManager = new GradleManager();

    @Override
    protected boolean isValidProjectRoot(File directory) {
        try {
            IdeaProject model = gradleManager.getGradleModel(directory);
            return model != null;
        } catch (Exception e) {
            return false;
        }
    }

    protected ModuleNode generateNode(File modelDirectory) {
        try {
            IdeaProject model = gradleManager.getGradleModel(modelDirectory);
            return createModuleNodeRecursive(model, modelDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ModuleNode createModuleNodeRecursive(IdeaProject project, File parentDirectory) {
        ModuleNode rootNode = new ModuleNode(project.getName(), parentDirectory);

        for (IdeaModule ideaModule : project.getModules()) {
            File moduleDir = new File(parentDirectory, ideaModule.getName());
            ModuleNode subModuleNode = new ModuleNode(ideaModule.getName(), moduleDir);
            rootNode.addSubModule(subModuleNode);
            // 注意：假设每个子模块也是一个完整的 IdeaProject，递归处理每个子模块
            // 这可能需要根据你的具体项目结构进行调整
            // 暂时不处理递归情况
        }

        return rootNode;
    }

    @Override
    protected List<File> getSubModuleDirs(File modelDirectory) {
        return new ArrayList<>();
    }
}