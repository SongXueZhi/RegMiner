package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.BugTypes;

import java.util.List;

public interface BugTypeService {

    /**
     * description 获取所有bug type
     *
     */
    List<BugTypes> getAllBugTypes();

}
