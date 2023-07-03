package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.dao.BugTypeMapper;
import com.fudan.annotation.platform.backend.entity.BugTypes;
import com.fudan.annotation.platform.backend.service.BugTypeService;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public List<BugTypes> getAllBugTypes() {
        return bugTypeMapper.getAllBugTypes();
    }

}