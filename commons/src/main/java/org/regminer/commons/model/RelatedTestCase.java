
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
import spoon.reflect.declaration.CtMethod;

import java.util.Objects;

@Setter
@Getter
public class RelatedTestCase {
    private String enclosingClassName;
    private String methodName;
    private CtMethod<?> method;
    private String relativeFilePath;

    @Override
    public String toString() {
        return "TestCase{" +
                "className='" + enclosingClassName + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedTestCase testCaseX)) return false;
        return Objects.equals(getEnclosingClassName(), testCaseX.getEnclosingClassName())
                && Objects.equals(getMethodName(), testCaseX.getMethodName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnclosingClassName(), getMethodName());
    }
}
