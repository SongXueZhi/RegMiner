package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.core.Migrator;
import com.fudan.annotation.platform.backend.entity.ChangedFile;
import com.fudan.annotation.platform.backend.entity.CodeElement;
import com.fudan.annotation.platform.backend.entity.ProgressInfo;
import com.fudan.annotation.platform.backend.entity.SearchDetails;
import com.fudan.annotation.platform.backend.service.ProgressService;
import com.fudan.annotation.platform.backend.util.ProgressUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2022/06/03/14:50
 * @Description:
 */
@Service
@Slf4j
public class ProgressServiceImpl implements ProgressService {
    @Autowired
    private Migrator migrator;

    @Override
    public ProgressInfo getProgressInfo() throws IOException {
        return ProgressUtils.getRegMinerProgress();

    }

    @Override
    public SearchDetails getSearchDetails(String projectName, String bfc) throws IOException {
        return ProgressUtils.getSearchDetails(projectName,bfc);
    }

}
