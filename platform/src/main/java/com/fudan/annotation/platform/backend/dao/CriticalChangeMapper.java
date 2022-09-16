package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.HunkEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CriticalChangeMapper {

    void setHunks(String regressionUuid, String revisionName, String newPath, String oldPath,
                  int beginA, int beginB, int endA, int endB, String type);

    List<HunkEntity> getHunks(String regressionUuid, String revisionName);

    void deletHunks(String regressionUuid, String revisionName,  Integer criticalChangeId);
}
