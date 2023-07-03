package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.BugToTypeItems;
import com.fudan.annotation.platform.backend.entity.CreateBugToType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BugToTypeMapper {

    List<BugToTypeItems> getBugToTypeByRegressionUuid(String regressionUuid);

    void postAgreeBugType(String regressionUuid, int bugTypeId);

    void postDisagreeBugType(String regressionUuid, int bugTypeId);

    void insertBugToType(CreateBugToType newBugToType);
}