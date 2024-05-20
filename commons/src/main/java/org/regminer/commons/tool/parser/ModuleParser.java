package org.regminer.commons.tool.parser;

import org.regminer.commons.model.ModuleNode;

import java.io.File;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/9 15:39
 */

public enum ModuleParser {
    MVN_PARSER {
        @Override
        public ModuleNode parser(File projectDir) throws Exception {
            ProjectAnalysisManager manager = new ProjectAnalysisManager(new MavenProjectParser());
            return manager.analyze(projectDir);
        }
    },
    GRADLE_PARSER {
        @Override
       public ModuleNode parser(File projectDir) throws Exception {
            ProjectAnalysisManager manager = new ProjectAnalysisManager(new GradleProjectParser());
            return manager.analyze(projectDir);
        }
    };

    public abstract ModuleNode parser(File projectDir) throws Exception;
}
