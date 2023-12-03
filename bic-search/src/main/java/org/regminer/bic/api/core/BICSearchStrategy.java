package org.regminer.bic.api.core;

import org.regminer.common.model.PotentialBFC;

import java.io.File;

import org.apache.commons.lang3.tuple.Triple;
/**
 * @Author: sxz
 * @Date: 2023/12/01/15:02
 * @Description:
 */
public abstract class BICSearchStrategy {

    public abstract String[] getSearchSpace(String startPoint, File codeDir);

    public abstract Triple<String,String,Integer> search(PotentialBFC potentialBFC);
}
