package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.BugTypes;
import com.fudan.annotation.platform.backend.entity.CreateBugType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BugTypeMapper {

    List<BugTypes> getAllBugTypes();

    void insertBugType(CreateBugType newBugType);

    int deleteBugTypeById(int bugTypeId);

}