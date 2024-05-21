package org.regminer.commons.code.analysis;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: sxz
 * @Date: 2024/05/20/13:22
 * @Description:
 */
public class SpoonCodeAnalyst {

    public Graph<CtMethod<?>, DefaultEdge> buildCallGraph(Queue<CtMethod<?>> queue, Set<String> calledMethods) {
        // Initialize the directed graph
        Graph<CtMethod<?>, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // Process methods in the queue
        while (!queue.isEmpty()) {
            CtMethod<?> method = queue.poll();
            String methodSignature = method.getSignature();

            // Skip if the method has already been processed
            if (calledMethods.contains(methodSignature)) {
                continue;
            }

            calledMethods.add(methodSignature);
            graph.addVertex(method);

            // Get method invocations
            List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<>(CtInvocation.class));
            for (CtInvocation<?> invocation : invocations) {
                CtMethod<?> targetMethod = (CtMethod<?>) invocation.getExecutable().getDeclaration();

                // Skip if the target method is invalid or already processed
                if (targetMethod == null) {
                    continue;
                }
                if (!graph.containsVertex(targetMethod)) {
                    graph.addVertex(targetMethod);
                }
                graph.addEdge(method, targetMethod);
                if (!calledMethods.contains(targetMethod.getSignature())) {
                    queue.add(targetMethod);
                }
            }
        }
        return graph;
    }


    public Launcher modelCode(String codeDir) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(codeDir);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();
        return launcher;
    }

    public Launcher modelCode(List<String> codeFiles) {
        Launcher launcher = new Launcher();
        for (String codeFile : codeFiles) {
            launcher.addInputResource(codeFile);
        }
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setComplianceLevel(11);
        launcher.buildModel();
        return launcher;
    }

    public List<CtMethod<?>> getMethods(CtModel model) {
        return model.getElements(new TypeFilter<>(CtMethod.class));
    }

    @SuppressWarnings("unchecked")
    public List<CtMethod<?>> getMethods(CtModel model, String methodName) {
        return (List<CtMethod<?>>) (List<?>) model.getElements(new TypeFilter<>(CtMethod.class)).stream()
                .filter(c -> c.getSimpleName().equals(methodName))
                .collect(Collectors.toList());
    }

    public void replaceMethod(CtMethod<?> methodOld, CtMethod<?> methodNew, CtClass<?> ctClass) {
        ctClass.removeMethod(methodOld);
        ctClass.addMethod(methodNew);
    }
}
