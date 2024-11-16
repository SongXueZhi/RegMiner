package org.regminer.commons.tool.parser;

import org.regminer.commons.model.ModuleNode;

import java.io.File;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/8 16:51
 */

public class ProjectAnalysisManager {
    private final ProjectParser parser;

    public ProjectAnalysisManager(ProjectParser parser) {
        this.parser = parser;
    }

    public ModuleNode analyze(File projectDirectory) throws Exception {
        return parser.parseProject(projectDirectory);
    }
}
