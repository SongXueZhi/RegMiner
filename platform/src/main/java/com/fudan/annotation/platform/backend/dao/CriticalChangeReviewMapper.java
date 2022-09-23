package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.HunkEntityWithTool;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CriticalChangeReviewMapper {

    void setCriticalChangeReview(int criticalChangeId, String regressionUuid, String revisionName, String newPath, String oldPath,
                  int beginA, int beginB, int endA, int endB, String type, String tool);

    List<HunkEntityWithTool> getCriticalChangeReview(String regressionUuid, String revisionName);

//    void deleteCriticalChangeReview(String regressionUuid, String revisionName,  Integer criticalChangeId);
}
