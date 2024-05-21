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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Regression implements Serializable {
    String id;
    String projectFullName;
    String errorType;
    String regressionUUID;
//    ProjectEntity projectEntity;
    String bugId;
    String bfcId;
    String buggyId;
    String bicId;
    String workId;
    String testCase;
    int withGap;

    public Regression(String bfcId, String buggyId,
                      String bicId, String workId,
                      String testCase,
                      int withGap) {
        this.bfcId = bfcId;
        this.buggyId = buggyId;
        this.bicId = bicId;
        this.workId = workId;
        this.testCase = testCase;
        this.withGap = withGap;
    }

}
