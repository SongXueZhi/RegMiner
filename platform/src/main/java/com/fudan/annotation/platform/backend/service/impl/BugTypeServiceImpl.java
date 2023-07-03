package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.dao.BugTypeMapper;
import com.fudan.annotation.platform.backend.entity.BugTypes;
import com.fudan.annotation.platform.backend.entity.CreateBugType;
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
    @Autowired
    private BugTypeMapper bugTypeMapper;

    @Override
    public List<BugTypes> getAllBugTypes() {
        return bugTypeMapper.getAllBugTypes();
    }

    @Override
    public void createNewBugType(CreateBugType newBugType) {
        if(newBugType.getBugTypeName() == null || newBugType.getAccountName() == null) {
            throw new RuntimeException("param loss");
        }
        bugTypeMapper.insertBugType(newBugType);
    }

    @Override
    public void deleteBugTypeById(int bugTypeId) {
        bugTypeMapper.deleteBugTypeById(bugTypeId);
    }

}