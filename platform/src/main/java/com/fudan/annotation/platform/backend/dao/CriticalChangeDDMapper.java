package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.CriticalChangeDD;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CriticalChangeDDMapper {
    List<CriticalChangeDD> getCriticalChangeDD(String regressionUuid, String revisionName);

    void setCriticalChangeDD(String regressionUuid, String revisionName, String newPath, String oldPath,
                             int beginA, int beginB, int endA, int endB, String type, String tool);

//    void deleteCriticalChangeDD(Integer criticalChangeId, String regressionUuid, String revisionName);
}
