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

import junit.framework.TestCase;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;
import org.regminer.commons.code.analysis.SpoonCodeAnalyst;
import org.regminer.commons.utils.MigratorUtil;
import spoon.Launcher;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtMethod;
import spoon.support.compiler.VirtualFile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: sxz
 * @Date: 2024/05/21/15:08
 * @Description:
 */
public class PurgeTest extends TestCase {

    @Test
    public void testPurge() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        final Set<String> targetTestMethod = new HashSet<>(Collections.singleton("testWithinMultipleRanges()"));
        SpoonCodeAnalyst spoonCodeAnalyst = new SpoonCodeAnalyst();
        Path path = Path.of("src/test/resources/RandomStringGeneratorTest.java");
        Launcher launcher = spoonCodeAnalyst.modelCode(path.toString());
        Method removeUnusedTestMethods = MigratorUtil.class.getDeclaredMethod("removeUnusedTestMethods",
                CtCompilationUnit.class
                , Set.class);
        removeUnusedTestMethods.setAccessible(true);
        Object migrateUtil = MigratorUtil.class.getDeclaredConstructor().newInstance();
        CtCompilationUnit compilationUnit =
                launcher.getFactory().CompilationUnit().getMap().get(path.toAbsolutePath().toString());
        final int importOriginNum = compilationUnit.getImports().size();
        final int methodsOriginNum = compilationUnit.getDeclaredTypes().stream(
        ).mapToInt(type -> type.getMethods().size()).sum();

        removeUnusedTestMethods.invoke(migrateUtil, compilationUnit, targetTestMethod);

        Method buildTestCallGraph = MigratorUtil.class.getDeclaredMethod("buildTestCallGraph", CtCompilationUnit.class);
        buildTestCallGraph.setAccessible(true);
        Graph<CtMethod<?>, DefaultEdge> graph = (Graph<CtMethod<?>, DefaultEdge>) buildTestCallGraph.invoke(migrateUtil, compilationUnit);
        Set<CtMethod<?>> vertexSet = graph.vertexSet();

        Method removeUnusedMethods = MigratorUtil.class.getDeclaredMethod("removeUnusedMethods",
                CtCompilationUnit.class, Set.class);
        removeUnusedMethods.setAccessible(true);

        removeUnusedMethods.invoke(migrateUtil, compilationUnit, vertexSet);

        Method removeUnusedFields = MigratorUtil.class.getDeclaredMethod("removeUnusedFields",
                CtCompilationUnit.class, Set.class);
        removeUnusedFields.setAccessible(true);
        removeUnusedFields.invoke(migrateUtil, compilationUnit, vertexSet);

        Method removeUnusedImports = MigratorUtil.class.getDeclaredMethod("removeUnusedImports",
                CtCompilationUnit.class);
        removeUnusedImports.setAccessible(true);

        removeUnusedImports.invoke(migrateUtil, compilationUnit);

        Launcher launcher1 = new Launcher();
        launcher1.addInputResource(new VirtualFile(compilationUnit.prettyprint()));
        launcher1.buildModel();
        CtCompilationUnit compilationUnit1 =
                launcher.getFactory().CompilationUnit().getMap().values().stream().findFirst().orElse(null);

        final int importAfterNum = compilationUnit1.getImports().size();
        final int methodsAfterNum = compilationUnit1.getDeclaredTypes().stream(
        ).mapToInt(type -> type.getMethods().size()).sum();
        final int fieldsAfterNum = compilationUnit1.getDeclaredTypes().stream(
        ).mapToInt(type -> type.getFields().size()).sum();

        assertTrue(importOriginNum > importAfterNum);
        assertTrue(methodsOriginNum > methodsAfterNum);
        assertEquals(2, importAfterNum);
        assertEquals(1, methodsAfterNum);
        assertEquals(0, fieldsAfterNum);
    }

}
