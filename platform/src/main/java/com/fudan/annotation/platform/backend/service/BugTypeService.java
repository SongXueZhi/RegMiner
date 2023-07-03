package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.BugTypes;
import com.fudan.annotation.platform.backend.entity.CreateBugType;

import java.util.List;

public interface BugTypeService {

    /**
     * description 获取所有bug type
     *
     */
    List<BugTypes> getAllBugTypes();

    void createNewBugType(CreateBugType newBugType);

    void deleteBugTypeById(int bugTypeId);

}
