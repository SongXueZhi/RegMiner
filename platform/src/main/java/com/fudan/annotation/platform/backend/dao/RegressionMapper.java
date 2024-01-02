package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.Regression;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RegressionMapper {

    int deleteByregressionId(String regressionUuid);

    int insert(Regression regressionInfo);

    int insertSelective(Regression regressionInfo);

    List<Regression> selectRegression(Integer id, String regressionUuid, Integer regressionStatus, String projectName,
                                      String bfc, String buggy, String bic, String work,
                                      String keyWord);

    int updateRegressionStatus(String regressionUuid, Integer regressionStatus);

    List<Regression> getRegression();

    void setProjectUuid(String regressionUuid, String projectUuid);

    void setRegressionUuid(Integer id, String regressionUuid);

    Regression getRegressionInfo(String regressionUuid);
}