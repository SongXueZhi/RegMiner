package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.*;
import org.dom4j.Text;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface RegressionService {

    /**
     * description 获取所有regression
     *
     * @param regressionUuid   regressionID
     * @param regressionStatus regression状态
     */
    List<Regression> getRegressions(String regressionUuid, Integer regressionStatus, String projectName, String keyWord);

    /**
     * description 插入新regression
     *
     * @param regressionInfo regression信息
     */
    void insertRegression(Regression regressionInfo);

    /**
     * description 删除regression
     *
     * @param regressionUuid regressionUuid
     */
    void deleteRegression(String regressionUuid);

    /**
     * description 重置regression状态
     *
     * @param regressionUuid   regressionUuid
     * @param regressionStatus regression状态
     */
    void resetStatus(String regressionUuid, Integer regressionStatus);

    /**
     * description 添加项目uuid
     */
    void addProjectUuid();

    /**
     * description regression uuid
     */
    void addRegressionUuid();

    /**
     * description get changed files
     *
     * @param regressionUuid regressionUuid
     */
    RegressionDetail getChangedFiles(String regressionUuid, String userToken) throws Exception;

    RegressionDetail getMigrateInfo(String regressionUuid, String bic, String userToken) throws Exception;

    /**
     * description checkout
     *
     * @param regressionUuid regressionUuid
     * @param userToken      userToken
     */
    void checkoutByUser(String regressionUuid, String userToken);

    /**
     * description get files code
     *
     * @param regressionUuid regressionUuid
     * @param userToken      userToken
     * @param filename       filename
     * @param oldPath        oldPath
     * @param newPath        newPath
     * @param revisionFlag   revisionFlag
     */
    CodeDetails getFilesCode(String regressionUuid, String userToken, String filename, String oldPath, String newPath, String revisionFlag);

    String runTest(String regressionUuid, String userToken, String revisionFlag);

    String readRuntimeResult(String filaPath) throws IOException;

    /**
     * description set critical change hunk
     *
     * @param regressionUuid regressionUuid
     * @param revisionName   revision name
     * @param hunkEntityDTO  single hunk patch
     */
    void setCriticalChange(String regressionUuid, String revisionName, HunkEntity hunkEntityDTO);

    /**
     * description get critical change hunk
     *
     * @param regressionUuid regressionUuid
     * @param revisionName   revision name
     */
    CriticalChange getCriticalChange(String regressionUuid, String revisionName);

    /**
     * description delete critical change hunk
     *
     * @param regressionUuid regressionUuid
     * @param revisionName   revision name
     */
    List<HunkEntity> deleteCriticalChange(String regressionUuid, String revisionName,Integer criticalChangeId);

    /**
     * apply hunks to special file and return the code
     *
     * @param userToken      usertoken
     * @param regressionUuid regressionUuid
     * @param oldRevision    old revision name
     * @param newRevision    new revision name
     * @param hunkList       hunks need to apply
     */
    String applyHunks(String userToken, String regressionUuid, String oldRevision, String newRevision, List<HunkEntity> hunkList) throws IOException;

    /**
     * modified a file with new code
     *
     * @param userToken      usertoken
     * @param regressionUuid regressionUuid
     * @param oldPath        old revision path
     * @param revisionName   old revision name(bic->work, bfc->buggy)
     * @param newCode        updated code
     * @param coverStatus    modified or not(0-adopt old code 1-adopt new code)
     */
    void modifiedCode(String userToken, String regressionUuid, String oldPath, String revisionName, String newCode, Integer coverStatus) throws IOException;

    /**
     * update a file with new code
     *
     * @param userToken      usertoken
     * @param code           updated code
     * @param projectName    project name
     * @param regressionUuid regressionUuid
     * @param revisionName   old revision name(bic->work, bfc->buggy)
     * @param filePath       file path
     */
    void updateCode(String userToken, String code, String projectName, String regressionUuid, String revisionName, String filePath) throws IOException;

    /**
     * revert a file to old code
     *
     * @param userToken      usertoken
     * @param projectName    project name
     * @param regressionUuid regressionUuid
     * @param revisionName   old revision name(bic->work, bfc->buggy)
     * @param filePath       file path
     */
    void revertCode(String userToken, String projectName, String regressionUuid, String revisionName, String filePath) throws IOException;

    /**
     * clear cache by regression
     *
     * @param userToken      usertoken
     * @param projectName    project name
     * @param regressionUuid regressionUuid
     */
    void clearCache(String userToken, String projectName, String regressionUuid) throws IOException;

    /**
     * add Comments by user
     *
     * @param regressionUuid regressionUuid
     */
    List<Comments> getComment(String regressionUuid);

    /**
     * add Comments by user
     *
     * @param regressionUuid regressionUuid
     * @param accountName user account
     * @param context comment context
     */
    void setComment(String regressionUuid, String accountName, String context);

    /**
     * add Comments by user
     *
     * @param regressionUuid regressionUuid
     * @param accountName account Name
     * @param commentId comment ID
     */
    void deleteComments(String regressionUuid, String accountName, int commentId);

    /**
     * add Comments by user
     *
     * @param regressionUuid regressionUuid
     * @param accountName account Name
     * @param commentId comment ID
     * @param context context
     */
    void postComment(String regressionUuid, String commentId, String accountName, String context);

    /**
     * get Critical change review
     *
     * @param regressionUuid regressionUuid
     * @param revisionName revision name - bic/bfc
     */
    CriticalChangeReview getCriticalChangeReview(String regressionUuid, String revisionName);
}
