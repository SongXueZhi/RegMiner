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
import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: sxz
 * @Date: 2022/06/08/21:42
 * @Description:
 */
@Getter
@Setter
public class TestSuiteX {
    public final List<RelatedTestCase> testCaseXList = SetUniqueList.setUniqueList(new LinkedList<>());
    private String filePath;
    private String packageName;
    private String className;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("testCaseXList", testCaseXList)
                .append("filePath", filePath)
                .append("packageName", packageName)
                .append("className", className)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TestSuiteX that)) return false;

        return new EqualsBuilder().append(testCaseXList, that.testCaseXList).append(getFilePath(),
                that.getFilePath()).append(getPackageName(), that.getPackageName()).append(getClassName(),
                that.getClassName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(testCaseXList).append(getFilePath()).append(getPackageName()).append(getClassName()).toHashCode();
    }
}
