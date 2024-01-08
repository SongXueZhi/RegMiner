package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.dao.AccountMapper;
import com.fudan.annotation.platform.backend.dao.BugToTypeMapper;
import com.fudan.annotation.platform.backend.dao.BugTypeMapper;
import com.fudan.annotation.platform.backend.dao.RegressionMapper;
import com.fudan.annotation.platform.backend.entity.*;
import com.fudan.annotation.platform.backend.service.BugTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * description:
 *
 * @author sunyujie
 * create: 2023-06-28 16:04
 **/

@Service
@Slf4j
public class BugTypeServiceImpl implements BugTypeService {

    private BugTypeMapper bugTypeMapper;
    @Autowired
    private BugToTypeMapper bugToTypeMapper;
    @Autowired
    private RegressionMapper regressionMapper;

    @Autowired
    public void setBugTypeMapper(BugTypeMapper bugTypeMapper) {
        this.bugTypeMapper = bugTypeMapper;
    }

    @Override
    public List<BugTypes> getAllBugTypes(String bugTypeName) {
        return bugTypeMapper.getAllBugTypes(bugTypeName);
    }

    @Override
    public void createNewBugType(CreateBugType newBugType) {
        if (newBugType.getBugTypeName() == null || newBugType.getAccountName() == null) {
            throw new RuntimeException("param loss");
        }
        bugTypeMapper.insertBugType(newBugType);
    }

    @Override
    public void deleteBugTypeById(int bugTypeId) {
        bugTypeMapper.deleteBugTypeById(bugTypeId);
    }

    @Override
    public List<BugToTypeItems> getBugToTypeByRegressionUuid(String regressionUuid) {
        return bugToTypeMapper.getBugToTypeByRegressionUuid(regressionUuid);
    }

    @Override
    public void postAgreeBugType(String regressionUuid, int bugTypeId) {
        List<BugToTypeItems> bugToTypeList = bugToTypeMapper.getBugToTypeByRegressionUuid(regressionUuid);
        if (regressionUuid == null || regressionUuid.equals("")) {
            throw new RuntimeException("param loss");
        }
        if (bugToTypeList == null || bugToTypeList.isEmpty()) {
            throw new RuntimeException("record does not exist");
        }
        bugToTypeMapper.postAgreeBugType(regressionUuid, bugTypeId);
    }

    @Override
    public void postDisagreeBugType(String regressionUuid, int bugTypeId) {
        List<BugToTypeItems> bugToTypeList = bugToTypeMapper.getBugToTypeByRegressionUuid(regressionUuid);
        if (regressionUuid == null || regressionUuid.equals("")) {
            throw new RuntimeException("param loss");
        }
        if (bugToTypeList.isEmpty()) {
            throw new RuntimeException("record does not exist");
        }
        bugToTypeMapper.postDisagreeBugType(regressionUuid, bugTypeId);
    }

    @Override
    public void createBugTypeToRegression(CreateBugToType newBugToType) {
        List<BugTypes> bugTypeList = bugTypeMapper.getAllBugTypes(newBugToType.getBugTypeName());
        List<Regression> regressionList = regressionMapper.selectRegression(-1, newBugToType.getRegressionUuid(), null,
                null, null,null,null,null,null);
        if (newBugToType.getRegressionUuid() == null || newBugToType.getRegressionUuid().equals("") ||
                newBugToType.getBugTypeName() == null || newBugToType.getBugTypeName().equals("") ||
                newBugToType.getAccountName() == null || newBugToType.getAccountName().equals("") ||
                newBugToType.getBugTypeId() == 0) {
            throw new RuntimeException("param loss");
        }
        if (regressionList.isEmpty() || bugTypeList.isEmpty()) {
            throw new RuntimeException("Wrong params, please check if the regressionUuid or bugTypeId is right.");
        }
        bugToTypeMapper.insertBugToType(newBugToType);
    }

}