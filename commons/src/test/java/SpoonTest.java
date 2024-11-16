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

import org.junit.Test;
import org.regminer.commons.code.analysis.SpoonCodeAnalyst;
import spoon.Launcher;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * @Author: sxz
 * @Date: 2024/05/27/20:14
 * @Description:
 */
public class SpoonTest {
    @Test
    public void test_0() {
        SpoonCodeAnalyst codeAnalyst = new SpoonCodeAnalyst();
        Launcher launcher = codeAnalyst.modelCode("src/test/resources/Test0.java");
        launcher.getModel().getElements(new TypeFilter<>(CtField.class)).stream().forEach(
                ctField -> System.out.println(ctField.getType())
        );

    }
}
