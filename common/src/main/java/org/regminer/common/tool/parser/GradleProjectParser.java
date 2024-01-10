package org.regminer.common.tool.parser;

import org.regminer.common.model.ModuleNode;

import java.io.File;
import java.util.List;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc //todo 解析 gradle 项目结构
 * @date 2024/1/8 16:51
 */
public class GradleProjectParser extends AbstractProjectParser {
    @Override
    protected ModuleNode generateNode(File modelDirectory) {
        return null;
    }

    @Override
    protected boolean isValidProjectRoot(File directory) {
        return false;
    }

    @Override
    protected List<File> getSubModuleDirs(File modelDirectory) {
        return null;
    }
}