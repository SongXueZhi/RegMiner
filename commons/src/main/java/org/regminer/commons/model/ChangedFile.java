
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

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ChangedFile implements Serializable {
    private String newCommitId;
    private String newPath;
    private String oldPath;
    private List<Edit> editList;
    private Type type;

    public ChangedFile(String newPath) {
        this.newPath = newPath;
    }

    public enum Type {
        TEST_SUITE, TEST_DEPEND, JAVA_FILE, ANOTHER
    }

}
