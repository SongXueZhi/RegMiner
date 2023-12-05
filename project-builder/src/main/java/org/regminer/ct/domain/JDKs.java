package org.regminer.ct.domain;

import java.util.Arrays;

/**
 * @Author: sxz
 * @Date: 2023/12/05/15:43
 * @Description:
 */
public class JDKs {

    public static JDK[] jdkSearchRange;

    private static int curIndex = 0;

    static {
        jdkSearchRange = JDK.values();
        setCurIndexByJDK(JDK.J8);
    }

    public static void setCurIndexByJDK(JDK jdk) {
        curIndex = Arrays.asList(jdkSearchRange).indexOf(jdk);
    }

    public static int getCurIndex() {
        return curIndex;
    }

    public static void setCurIndex(int index) {
        curIndex = index;
    }
}
