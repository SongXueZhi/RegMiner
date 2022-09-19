package com.fudan.annotation.platform.backend.dao;

import com.fudan.annotation.platform.backend.entity.Comments;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommentsMapper {

    void setComments(String regressionUuid, String accountName, String context);

    List<Comments> getComments(String regressionUuid);

    void deleteComments(String regressionUuid, String accountName, int commentId);
}
