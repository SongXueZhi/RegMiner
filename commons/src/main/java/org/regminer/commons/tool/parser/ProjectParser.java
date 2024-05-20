package org.regminer.commons.tool.parser;

import org.regminer.commons.model.ModuleNode;

import java.io.File;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/8 16:50
 */

public interface ProjectParser {
    ModuleNode parseProject(File projectDirectory) throws Exception;

    File findTopLevelModuleDirectory(File directory) throws Exception;

    ModuleNode createModuleNode(File directory) throws Exception;

}
