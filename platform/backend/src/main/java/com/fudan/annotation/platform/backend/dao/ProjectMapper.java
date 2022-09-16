package com.fudan.annotation.platform.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ProjectMapper {

    String getProjectUuid(String organization, String projectName);

}
