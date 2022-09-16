package com.fudan.annotation.platform.backend.service;

import com.fudan.annotation.platform.backend.entity.ChangedFile;
import com.fudan.annotation.platform.backend.entity.CodeElement;
import com.fudan.annotation.platform.backend.entity.ProgressInfo;
import com.fudan.annotation.platform.backend.entity.SearchDetails;

import java.io.IOException;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2022/06/03/14:48
 * @Description:
 */
public interface ProgressService {
    ProgressInfo getProgressInfo() throws IOException;
    SearchDetails getSearchDetails(String projectName, String bfc) throws IOException;
}
