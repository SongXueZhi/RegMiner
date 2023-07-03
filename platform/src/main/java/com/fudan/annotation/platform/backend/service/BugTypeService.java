package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.BugToTypeItems;
import com.fudan.annotation.platform.backend.entity.BugTypes;
import com.fudan.annotation.platform.backend.entity.CreateBugToType;
import com.fudan.annotation.platform.backend.entity.CreateBugType;

import java.util.List;

public interface BugTypeService {

    /**
     * description 获取所有bug type
     *
     */
    List<BugTypes> getAllBugTypes(String bugTypeName);

    /**
     * description 新建一个bug type
     * @body newBugType bug type name && account name
     */
    void createNewBugType(CreateBugType newBugType);

    /**
     * description 删除一个bug type
     * @param bugTypeId
     */
    void deleteBugTypeById(int bugTypeId);

    /**
     * description 获取bug to type表
     * @param regressionUuid
     */
    List<BugToTypeItems> getBugToTypeByRegressionUuid(String regressionUuid);

    /**
     * description agree bug type
     * @param regressionUuid
     * @param bugTypeId
     */
    void postAgreeBugType(String regressionUuid, int bugTypeId);

    /**
     * description disagree bug type
     * @param regressionUuid
     * @param bugTypeId
     */
    void postDisagreeBugType(String regressionUuid, int bugTypeId);

    /**
     * description 新建一个bug to type
     * @body CreateBugToType
     */
    void createBugTypeToRegression(CreateBugToType newBugToType);
}
