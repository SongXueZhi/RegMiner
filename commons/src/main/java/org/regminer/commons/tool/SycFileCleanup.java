package org.regminer.commons.tool;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Improved file cleanup utility with directory existence check.
 */
public class SycFileCleanup {

    private static final Logger LOGGER = LoggerFactory.getLogger(SycFileCleanup.class);

    public void cleanDirectoryOnFilter(File dir, List<String> filter) {
        if (!dir.exists()) {
            return;
        }
        new SycCleaner(dir, new HashSet<>(filter), true).start();
    }

    public void cleanDirectory(File dir) {
        if (!dir.exists()) {
            return;
        }
        new SycCleaner(dir, null, false).start();
    }

    static class SycCleaner extends Thread {
        File dir;
        Set<String> filter;
        boolean onFilter;

        public SycCleaner(File dir, Set<String> filter, boolean onFilter) {
            this.dir = dir;
            this.filter = filter;
            this.onFilter = onFilter;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                if (onFilter) {
                    cleanDirectoryOnFilter();
                } else {
                    cleanDirectory();
                }
            } catch (Exception e) {
                LOGGER.error("Error cleaning directory: {}", e.getMessage(), e);
            }
        }

        private void cleanDirectoryOnFilter() {
            File[] childrenArray = dir.listFiles();
            if (childrenArray != null) {
                for (File file : childrenArray) {
                    if (filter.contains(file.getName())) {
                        continue;
                    }
                    deleteFile(file);
                }
            }
        }

        private void cleanDirectory() {
            deleteFile(dir);
        }

        private void deleteFile(File file) {
            try {
                if (!file.exists()) {
                    return;
                }
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                LOGGER.error("Failed to delete file: {}", file.getAbsolutePath(), e);
            }
        }
    }
}
