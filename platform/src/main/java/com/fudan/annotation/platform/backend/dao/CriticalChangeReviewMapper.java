package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.HunkEntityPlus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CriticalChangeReviewMapper {

    void setCriticalChangeReview(String regressionUuid, String revisionName, String newPath, String oldPath,
                                 int beginA, int beginB, int endA, int endB, String type, String tool,
                                 String accountName, String feedback);

    void updateCriticalChangeReview(Integer reviewId, int beginA, int beginB, int endA, int endB, String tool,
                                    String accountName, String feedback);

    List<HunkEntityPlus> getCriticalChangeReview(String regressionUuid, String revisionName);

    void deleteCriticalChangeReview(Integer reviewId, String regressionUuid, String revisionName);
}
