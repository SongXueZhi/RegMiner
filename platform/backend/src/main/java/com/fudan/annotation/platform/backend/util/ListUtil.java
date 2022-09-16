package com.fudan.annotation.platform.backend.util;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Richy
 * create: 2022-03-07 17:42
 **/
public class ListUtil {
    public static <T> List<T> castList(Class<? extends T> c, List<?> collection) {
        // adapted from https://stackoverflow.com/a/2848268
        List<T> r = new ArrayList<>(collection.size());
        for (Object obj: collection) {
            r.add(c.cast(obj));
        }
        return r;
    }
}