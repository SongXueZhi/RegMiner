package com.fudan.annotation.platform.backend.util;

import com.fudan.annotation.platform.backend.entity.ProgressInfo;
import com.fudan.annotation.platform.backend.entity.SearchDetails;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2022/06/03/15:13
 * @Description:
 */
public class ProgressUtils {
    public final static String WORK_SPACE = System.getProperty("user.home") + File.separator + "miner_space";
    private final static String PROJECT_LIST_FILE = "project.in";
    private final static String PROJECT_POSTFIX = "current.project";
    private final static String NONE = "N.A.";
    private final static String PRFC_TOTAL_PREFIX = "pRFC total:";
    private final static String PROGRESS_FILE = "progress.details";
    private final static String BFC_LOG = "miner-log.bfc";
    private final static String RFC_LOG = "miner-log.rfc";

    private final static String TIME_FILE = "start.time";

    public static ProgressInfo getRegMinerProgress() throws IOException {
        ProgressInfo progressInfo = new ProgressInfo();
        File workDir = new File(WORK_SPACE);
        //total start time
        progressInfo.setTotalStartTime(FileUtils.readLines(new File(workDir, TIME_FILE)).get(0));
        // project info
        progressInfo.setTotalProjectNum(FileUtils.readLines(new File(workDir, PROJECT_LIST_FILE),
                StandardCharsets.UTF_8).size());
        File file = new File(workDir, PROJECT_POSTFIX);
        List<String> content = FileUtils.readLines(file, StandardCharsets.UTF_8);
        progressInfo.setCurrentProjectName(content.get(0));
        progressInfo.setProjectQueueNum(content.get(2));
        progressInfo.setProjectStatTime(content.get(1));

        //rPFC
        File projrctDir = new File(
                workDir, progressInfo.getCurrentProjectName().split("/")[1]);
        List<String> pRFCList = FileUtils.readLines(new File(projrctDir, File.separator + BFC_LOG),
                StandardCharsets.UTF_8);

        Iterator<String> iterator = pRFCList.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().startsWith(PRFC_TOTAL_PREFIX)) {
                iterator.remove();
            }
        }
        progressInfo.setTotalPRFCNum(pRFCList.get(pRFCList.size() - 1).split(PRFC_TOTAL_PREFIX)[1]);
        File file1 = new File(projrctDir, PROGRESS_FILE);
        if (file1.exists()) {
            progressInfo.setPRFCDoneNum(FileUtils.readLines(file1, StandardCharsets.UTF_8).size());
        } else {
            progressInfo.setPRFCDoneNum(0);
        }

        return progressInfo;
    }

    public static SearchDetails getSearchDetails(String projectName, String rfcID) throws IOException {
        SearchDetails searchDetails = new SearchDetails();
        String[] allContent = FileUtils.readFileToString(new File(WORK_SPACE, projectName + File.separator + RFC_LOG),
                StandardCharsets.UTF_8).split("queue size:");
        List<String[]> steps = new ArrayList<>();
        for (String item : allContent) {
            if (item.equals("")) {
                continue;
            }
            String[] lines = item.split("\n");
            if (lines[1].startsWith(rfcID)) {
                searchDetails.setSearchSpaceNum(lines[2].split(":")[1]);
                for (int i = 3; i < lines.length; i++) {
                    if (lines[i].startsWith("index")) {
                        String[] details = lines[i].split(":");
                        steps.add(new String[]{details[1], details[2], getTestStatus(lines[i + 1])});
                    }
                }
            }
        }
        searchDetails.setOrderList(steps);
        return searchDetails;
    }

    static String getTestStatus(String message) {
        if (message.contains("PASS")) {
            return "PASS";
        } else if (message.contains("FAL")) {
            return "FAL";
        } else if (message.contains("CE")) {
            return "CE";
        } else {
            return "UNKNOWN";
        }
    }
}
