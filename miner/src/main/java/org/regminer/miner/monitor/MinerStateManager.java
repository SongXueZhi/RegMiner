package org.regminer.miner.monitor;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author: sxz
 * @Date: 2023/12/26/16:45
 * @Description: Manage the state of the miner.
 */
public class MinerStateManager {
    private String filePath;

    public MinerStateManager(String filePath) {
        this.filePath = filePath;
    }

    public void updateState(String lastProcessedCommitId) {
        try {
            Files.writeString(Paths.get(filePath), lastProcessedCommitId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readState() {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                return Files.readString(Paths.get(filePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }
}
