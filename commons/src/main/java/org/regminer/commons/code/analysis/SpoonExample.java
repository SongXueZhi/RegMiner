package org.regminer.commons.code.analysis;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BFSShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;

public class SpoonExample {
    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.addInputResource("/Users/sxz/reg4j/cache_code/1_rfc/src/main/java");
        launcher.addInputResource("/Users/sxz/reg4j/cache_code/1_rfc/src/test/java");
        launcher.getEnvironment().setComplianceLevel(17);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        Graph<String, DefaultEdge> callGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        String startPointMethodName = "yourStartingMethodName";

        // 获取特定的类和方法
        CtClass<?> exampleClass = model.getElements(new TypeFilter<>(CtClass.class))
                .stream()
                .filter(c -> c.getSimpleName().equals("ListFieldTest"))
                .findFirst()
                .orElse(null);

        if (exampleClass != null) {
            CtMethod<?> testMethod = exampleClass.getMethodsByName("test_for_list").get(0);

            // 创建JGraphT图
            Graph<CtMethod<?>, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
            Set<CtMethod<?>> visitedMethods = new HashSet<>();
            Queue<CtMethod<?>> toVisit = new LinkedList<>();

            // 初始化队列
            toVisit.add(testMethod);

            // 构建调用图
            while (!toVisit.isEmpty()) {
                CtMethod<?> currentMethod = toVisit.remove();
                if (!visitedMethods.contains(currentMethod)) {
                    visitedMethods.add(currentMethod);
                    graph.addVertex(currentMethod);

                    List<CtInvocation<?>> invocations = currentMethod.getElements(new TypeFilter<>(CtInvocation.class));
                    for (CtInvocation<?> invocation : invocations) {
                        CtMethod<?> targetMethod = (CtMethod<?>) invocation.getExecutable().getDeclaration();
                        if (targetMethod != null && !visitedMethods.contains(targetMethod)) {
                            graph.addVertex(targetMethod);
                            graph.addEdge(currentMethod, targetMethod);
                            toVisit.add(targetMethod);
                        }
                    }
                }
            }

            CtMethod<?> method2 = model.getElements(new TypeFilter<>(CtMethod.class))
                    .stream()
                    .filter(c -> c.getSimpleName().equals("degradeValueAssignment"))
                    .findFirst()
                    .orElse(null);// 获取方法1
            int reachable = checkReachabilityAndDistance(graph, testMethod, method2);
            System.out.println("Method1 can reach Method2: " + reachable);
        }
    }
    public static int checkReachabilityAndDistance(Graph<CtMethod<?>, ?> graph, CtMethod<?> method1, CtMethod<?> method2) {
        BFSShortestPath<CtMethod<?>, ?> bfs = new BFSShortestPath<>(graph);
        GraphPath<CtMethod<?>, ?> path = bfs.getPath(method1, method2);

        if (path != null) {
            // 返回路径长度
            return path.getLength();
        } else {
            // 如果没有路径，返回 -1 表示不可达
            return -1;
        }
    }

}