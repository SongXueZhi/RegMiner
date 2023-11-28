package org.regminer.ct.utils;

import org.regminer.ct.domain.OS;

public class OSUtils {

    public static String getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains(OS.WINDOWS)) {
            return OS.WINDOWS;
        } else {
            return OS.UNIX;
        }
    }
}
