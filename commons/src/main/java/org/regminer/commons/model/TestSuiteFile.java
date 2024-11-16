/*
 * Copyright 2019-2024   XueZhi Song, Yun Lin and RegMiner contributors
 *
 * This file is part of RegMiner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.regminer.commons.model;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.diff.Edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: sxz
 * @Date: 2024/05/21/19:15
 * @Description:
 */
@Getter
@Setter
public class TestSuiteFile extends TestSourceFile{
    final Map<String, RelatedTestCase> testMethodMap = new HashMap<>();

     public TestSuiteFile(TestSourceFile sourceFile) {
        super(sourceFile.getNewPath());
        this.setNewCommitId(sourceFile.getNewCommitId());
        this.setOldPath(sourceFile.getOldPath());
        this.setEditList(sourceFile.getEditList());
        this.setType(sourceFile.getType());
    }

    public List<RelatedTestCase> getTestCaseList() {
        return new ArrayList<>(testMethodMap.values());
    }

    public String joinTestcaseString() {
        if (testMethodMap == null || testMethodMap.isEmpty()) {
            return "";
        }
        ArrayList<RelatedTestCase> testCaseArrayList = new ArrayList<>(testMethodMap.values());
        StringBuilder sb = new StringBuilder(testCaseArrayList.get(0).getMethodName());
        for (int i = 1; i < testCaseArrayList.size(); i++) {
            sb.append(";").append(testCaseArrayList.get(i).getMethod());
        }
        return sb.toString();
    }
}
