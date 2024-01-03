package com.fudan.annotation.platform.backend.controller;

import com.fudan.annotation.platform.backend.entity.*;
import com.fudan.annotation.platform.backend.service.RegressionService;
import com.fudan.annotation.platform.backend.vo.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * description: regression controller
 *
 * @author Richy
 * create: 2021-12-10 16:02
 **/

@RestController
@ResponseBody
@RequestMapping(value = "/regression")
public class RegressionController {
    private RegressionService regressionService;

    @Autowired
    public void setRegressionService(RegressionService regressionService) {
        this.regressionService = regressionService;
    }

    @GetMapping(value = "/all")
    public ResponseBean<List<Regression>> getAllRegressions(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "regression_uuid", required = false) String regressionUuid,
            @RequestParam(name = "regression_status", required = false) Integer regressionStatus,
            @RequestParam(name = "project_name", required = false) String projectName,
            @RequestParam(name = "bfc",required = false) String bfc,
            @RequestParam(name = "buggy", required = false) String buggy,
            @RequestParam(name = "bic", required = false) String bic,
            @RequestParam(name = "work", required = false) String work,
            @RequestParam(name = "keyword", required = false) String keyWord,
            @RequestParam(name = "bug_type_name", required = false) List<String> bugTypeName) {
        try {
            List<Regression> regressionList = regressionService.getRegressions(id,regressionUuid, regressionStatus,
                    projectName, bfc, buggy, bic, work, keyWord, bugTypeName);
            return new ResponseBean<>(200, "get regression info success", regressionList);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/add")
    public ResponseBean addRegression(@RequestBody Regression regression) {
        try {
            regressionService.insertRegression(regression);
            return new ResponseBean<>(200, "add regression success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "add regression failed :" + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseBean deleteRegression(@RequestParam("regression_uuid") String regressionUuid) {
        try {
            regressionService.deleteRegression(regressionUuid);
            return new ResponseBean<>(200, "delete success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "delete failed :" + e.getMessage(), null);
        }
    }

    @PutMapping(value = "/status")
    public ResponseBean resetStatus(@RequestParam(name = "regression_uuid") String regressionUuid,
                                    @RequestParam(name = "regression_status") Integer regressionStatus) {
        try {
            regressionService.resetStatus(regressionUuid, regressionStatus);
            return new ResponseBean<>(200, "reset success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "reset failed :" + e.getMessage(), null);
        }
    }

    @PutMapping(value = "/project_uuid")
    public ResponseBean addProjectUuid() {
        try {
            regressionService.addProjectUuid();
            return new ResponseBean<>(200, "add project uuid success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "add project uuid failed :" + e.getMessage(), null);
        }
    }

    @PutMapping(value = "/regression_uuid")
    public ResponseBean addRegressionUuid() {
        try {
            regressionService.addRegressionUuid();
            return new ResponseBean<>(200, "add regression uuid success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "add regression uuid failed :" + e.getMessage(), null);
        }
    }

    @GetMapping(value = "/detail")
    public ResponseBean<RegressionDetail> getChangedFiles(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam String userToken) {
        try {
            RegressionDetail changedFiles = regressionService.getChangedFiles(regressionUuid, userToken);
            return new ResponseBean<>(200, "get regression info success", changedFiles);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
        }
    }

    @GetMapping(value = "/migrate")
    public ResponseBean<RegressionDetail> getMigrateFiles(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "bic") String bic,
            @RequestParam String userToken) {
        try {
            RegressionDetail changedFiles = regressionService.getMigrateInfo(regressionUuid, bic, userToken);
            return new ResponseBean<>(200, "get regression info success", changedFiles);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get failed :" + e.getMessage(), null);
        }
    }

    @PutMapping(value = "/checkout")
    public ResponseBean<RegressionDetail> checkoutByUser(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam String userToken) {
        try {
            regressionService.checkoutByUser(regressionUuid, userToken);
            return new ResponseBean<>(200, "checkout success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "checkout failed :" + e.getMessage(), null);
        }
    }

    @GetMapping(value = "/code")
    public ResponseBean<CodeDetails> getCode(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam String userToken,
            @RequestParam(required = false) String filename,
            @RequestParam(name = "old_path") String oldPath,
            @RequestParam(name = "new_path") String newPath,
            @RequestParam String revisionFlag) {
        try {
            CodeDetails revisionCode = regressionService.getFilesCode(regressionUuid, userToken, filename, oldPath,
                    newPath, revisionFlag);
            return new ResponseBean<>(200, "get code success", revisionCode);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get code failed :" + e.getMessage(), null);
        }
    }

    //  获取控制台输出
    @GetMapping(value = "/console")
    public ResponseBean<String> getConsoleResult(
            @RequestParam(name = "path") String path) {
        try {
            String revisionRunResult = regressionService.readRuntimeResult(URLDecoder.decode(path, "UTF-8"));
            return new ResponseBean<>(200, "get result success", revisionRunResult);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get result failed :" + e.getMessage(), null);
        }
    }

    //运行测试用例
    @GetMapping(value = "/test")
    public ResponseBean<String> test(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam String userToken,
            @RequestParam String revisionFlag,
            @RequestParam(required = false) String command) {
        try {
            System.out.println("command: " + command);
            String logPath = regressionService.runTest(regressionUuid, userToken, revisionFlag, command);
            logPath = URLEncoder.encode(logPath, "UTF-8");
            return new ResponseBean<>(200, "test success", logPath);
        } catch (Exception e) {
            return new ResponseBean<>(401, "test failed :" + e.getMessage(), null);
        }
    }

    @PutMapping(value = "/hunk")
    public ResponseBean<String> applyHunks(
            @RequestParam String userToken,
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "old_revision") String oldRevision,
            @RequestParam(name = "new_revision") String newRevision,
            @RequestBody List<HunkEntity> hunkList) {
        try {
            String code = regressionService.applyHunks(userToken, regressionUuid, oldRevision, newRevision, hunkList);
            return new ResponseBean<>(200, "apply hunks success", code);
        } catch (Exception e) {
            return new ResponseBean<>(401, "apply hunks failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/modified")
    public ResponseBean modifiedCode(
            @RequestParam String userToken,
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "old_path") String oldPath,
            @RequestParam(name = "revision_name") String revisionName,
            @RequestBody String newCode,
            @RequestParam(name = "cover_status") Integer coverStatus) {
        try {
            regressionService.modifiedCode(userToken, regressionUuid, oldPath, revisionName, newCode, coverStatus);
            return new ResponseBean<>(200, "modified code success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "modified code failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/update")
    public ResponseBean updateCode(
            @RequestParam String userToken,
            @RequestBody String code,
            @RequestParam(name = "projectFullName") String projectName,
            @RequestParam(name = "regressionUuid") String regressionUuid,
            @RequestParam(name = "revisionName") String revisionName,
            @RequestParam(name = "filePath") String filePath) {
        try {
            regressionService.updateCode(userToken, code, projectName, regressionUuid, revisionName, filePath);
            return new ResponseBean<>(200, "update code success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "update code failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/revert")
    public ResponseBean revertCode(
            @RequestParam String userToken,
            @RequestParam(name = "projectFullName") String projectName,
            @RequestParam(name = "regressionUuid") String regressionUuid,
            @RequestParam(name = "revisionName") String revisionName,
            @RequestParam(name = "filePath") String filePath) {
        try {
            regressionService.revertCode(userToken, projectName, regressionUuid, revisionName, filePath);
            return new ResponseBean<>(200, "revert code success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "revert code failed :" + e.getMessage(), null);
        }
    }

    /**
     * 接口3 clearCache （前端在后端接口完成后，刷新界面）
     * 参数：{projectName，userid，regressionid}
     * delete file({projectName，userid，regressionid，revisionName，filePath})
     */

    @PostMapping(value = "/clearCache")
    public ResponseBean clearCache(
            @RequestParam String userToken,
            @RequestParam(name = "projectFullName") String projectName,
            @RequestParam(name = "regressionUuid") String regressionUuid) {
        try {
            regressionService.clearCache(userToken, projectName, regressionUuid);
            return new ResponseBean<>(200, "clear cache success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "clear cache failed :" + e.getMessage(), null);
        }

    }

    @GetMapping(value = "/comments")
    public ResponseBean<List<Comments>> getCommentsList(
            @RequestParam(name = "regression_uuid") String regressionUuid) {
        try {
            List<Comments> commentsList = regressionService.getComment(regressionUuid);
            return new ResponseBean<>(200, "get comments success", commentsList);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get comments failed :" + e.getMessage(), null);
        }
    }

    @PostMapping(value = "/comments")
    public ResponseBean setComment(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "account_name") String accountName,
            @RequestParam(name = "context") String context) {
        try {
            regressionService.setComment(regressionUuid, accountName, context);
            return new ResponseBean<>(200, "add comment success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "add comment failed :" + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/comments")
    public ResponseBean deleteComments(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "account_name") String accountName,
            @RequestParam(name = "comment_id") int commentId) {
        try {
            regressionService.deleteComments(regressionUuid, accountName, commentId);
            return new ResponseBean<>(200, "comment deleted", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "deleted failed :" + e.getMessage(), null);
        }
    }

    @GetMapping(value = "/criticalChange/review")
    public ResponseBean getCriticalChangeReview(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "revision_name") String revisionName) {
        try {
            CriticalChangeReview criticalChangeReview = regressionService.getCriticalChangeReview(regressionUuid,
                    revisionName);
            return new ResponseBean<>(200, "get critical change review success", criticalChangeReview);
        } catch (Exception e) {
            return new ResponseBean<>(401, "get critical change review failed: " + e.getMessage(), null);
        }
    }

    @PutMapping(value = "/criticalChange/review")
    public ResponseBean setCriticalChangeReview(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "revision_name") String revisionName,
            @RequestParam(name = "account_name") String accountName,
            @RequestParam(name = "feedback") String feedback,
            @RequestParam(name = "review_id", required = false) Integer reviewId,
            @RequestBody HunkEntity hunkEntityDTO) {
        try {
            regressionService.setCriticalChangeReview(regressionUuid, revisionName, reviewId, accountName, feedback,
                    hunkEntityDTO);
            return new ResponseBean<>(200, "record critical change success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "record critical change failed :" + e.getMessage(), null);
        }
    }

    @DeleteMapping(value = "/criticalChange/review")
    public ResponseBean<List<HunkEntity>> deleteCriticalChangeReview(
            @RequestParam(name = "regression_uuid") String regressionUuid,
            @RequestParam(name = "revision_name") String revisionName,
            @RequestParam(name = "review_id") Integer reviewId) {
        try {
            regressionService.deleteCriticalChangeReview(reviewId, regressionUuid, revisionName);
            return new ResponseBean<>(200, "delete critical change success", null);
        } catch (Exception e) {
            return new ResponseBean<>(401, "delete critical change failed :" + e.getMessage(), null);
        }
    }
}