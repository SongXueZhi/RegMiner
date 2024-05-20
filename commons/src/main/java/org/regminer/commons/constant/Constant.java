package org.regminer.commons.constant;

import java.util.List;

public class Constant {
    private Constant() {
        // utility class
    }

    public static final String NONE_PATH = "/dev/null";
    public static final String BFC_TASK = "bfc";
    public static final String BFC_BIC_TASK = "bfc&bic";
    public static final String BIC_TASK = "bic";
    public static final Integer SEARCH_DEPTH = 5;
    public static final Integer TEST_CASE_THRESHOLD = 3;
    public static final List<String> TASK_LIST = List.of(BFC_TASK, BFC_BIC_TASK, BIC_TASK);

}