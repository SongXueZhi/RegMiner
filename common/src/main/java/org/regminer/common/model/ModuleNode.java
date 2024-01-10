package org.regminer.common.model;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry Zhang <zhangjian16@fudan.edu.cn>
 * @desc
 * @date 2024/1/8 16:47
 */
@Data
public class ModuleNode {
    private String name;
    private File directory;
    private List<ModuleNode> subModules;

    public ModuleNode(String name, File directory) {
        this.name = name;
        this.directory = directory;
        this.subModules = new ArrayList<>();
    }

    public void addSubModule(ModuleNode subModule) {
        subModules.add(subModule);
    }
}