package org.regminer.ct.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.regminer.common.exec.Executor;
import org.regminer.common.tool.MavenManager;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.domain.JDKs;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CompileTestEnv;
import org.regminer.ct.model.CtCommands;

import java.io.File;
import java.util.List;

public enum OriginCompileFixWay {

    JDK_SEARCH(3) {
        @Override
        CompileResult fix(CompileTestEnv compileEnv, String errorMessage) {
            CompileResult compileResult = new CompileResult(CompileResult.CompileState.CE);

            // Iterate left from the current index
            for (int i = JDKs.getCurIndex() - 1; i >= 0; i--) {
                compileResult = tryCompileWithJDK(compileEnv, JDKs.jdkSearchRange[i]);
                if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                    JDKs.setCurIndex(i);
                    return compileResult;
                }
            }

            // Iterate right from the next index if the left iteration didn't succeed
            for (int i = JDKs.getCurIndex() + 1; i < JDKs.jdkSearchRange.length; i++) {
                compileResult = tryCompileWithJDK(compileEnv, JDKs.jdkSearchRange[i]);
                if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                    JDKs.setCurIndex(i);
                    return compileResult;
                }
            }

            return compileResult;
        }

        private CompileResult tryCompileWithJDK(CompileTestEnv compileEnv, JDK jdk) {
            logger.info("Trying to compile with JDK {}", jdk.name());
            compileEnv.getCtCommand().takeCommand(CtCommands.CommandKey.JDK, jdk.getCommand());
            CompileResult result = OriginCompileFixWay.recompileProject(compileEnv);
            if (result.getState() == CompileResult.CompileState.SUCCESS) {
                logger.info("Compile with JDK {} successfully", jdk.name());
                compileEnv.setJdk(jdk);
            } else {
                compileEnv.getCtCommand().remove(CtCommands.CommandKey.JDK);
            }
            return result;
        }

    },
    POM_FIX(1) {
        static final String SNAPSHOT = "-SNAPSHOT";

        @Override
        CompileResult fix(CompileTestEnv compileEnv, String errorMessage) {
            // 分析编译日志，找到有问题的依赖
            List<String> problematicDependencies = CtReferees.analyzeCompilationLog(errorMessage);

            File pomFile = new File(compileEnv.getProjectDir(), "pom.xml");
            MavenManager mavenManager = new MavenManager();

            // 尝试移除每一个有问题的依赖的 SNAPSHOT 版本
            for (String dependency : problematicDependencies) {
                try {
                    // 只修复 SNAPSHOT 问题
                    if (!dependency.endsWith(SNAPSHOT)) {
                        continue;
                    }
                    // 写在循环体内，相当于针对每个 dependency 做修改前都恢复成默认值
                    Model model = mavenManager.getPomModel(pomFile);

                    boolean isModified = false;
                    if (model.getParent() != null &&
                            (dependency.equals(model.getParent().getGroupId() + ":" + model.getParent().getArtifactId() + ":" + model.getParent().getVersion()) ||
                                    dependency.equals(model.getParent().toString()))) {
                        isModified = removeSnapshotFromParent(model);
                    }

                    isModified = isModified || removeSnapshotFromDependency(model, dependency);

                    if (isModified) {
                        mavenManager.saveModel(pomFile, model);

                        // 再次尝试编译
                        CompileResult attemptResult = recompileProject(compileEnv);
                        if (attemptResult.getState() == CompileResult.CompileState.SUCCESS) {
                            logger.info("Compile successful after fixing dependency: {}", dependency);
                            return attemptResult;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while modifying POM for dependency {}: {}", dependency, e.getMessage());
                }
            }

            return new CompileResult(CompileResult.CompileState.CE);
        }

        private boolean removeSnapshotFromParent(Model model) {
            if (model.getParent() != null) {
                logger.info("Trying to remove '{}' of parent {}", SNAPSHOT, model.getParent());
                String parentVersion = model.getParent().getVersion();
                model.getParent().setVersion(parentVersion.replace(SNAPSHOT, ""));
                return true;
            }
            return false;
        }

        private boolean removeSnapshotFromDependency(Model model, String probDepend) {
            boolean isModified = false;
            for (Dependency dependency : model.getDependencies()) {
                // 检查依赖项是否匹配有问题的依赖
                if ((dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion()).equals(probDepend) ||
                        dependency.toString().equals(probDepend)) {
                    logger.info("Trying to remove '{}' from dependency of {}", SNAPSHOT, dependency);
                    String depVersion = dependency.getVersion();
                    dependency.setVersion(depVersion.replace(SNAPSHOT, ""));
                    isModified = true;
                }
            }
            return isModified;
        }
    },
    DEPENDENCY_FIX(2) {
        @Override
        CompileResult fix(CompileTestEnv compileEnv, String errorMessage) {
            // Implement logic for DEPENDENCY_FIX
            // Example: Resolve dependency issues, handle exceptions, and recompile
            return recompileProject(compileEnv);
        }
    };
    private Integer order;

    public Integer getOrder() {
        return this.order;
    }

    OriginCompileFixWay(Integer order) {
        this.order = order;
    }
    protected Logger logger = LogManager.getLogger(OriginCompileFixWay.class);

    private static CompileResult recompileProject(CompileTestEnv compileEnv) {
        String message = new Executor()
                .setDirectory(compileEnv.getProjectDir())
                .exec(compileEnv.getCtCommand().compute())
                .getMessage();

        return new CompileResult(CtReferees.JudgeCompileState(message), compileEnv.getCtCommand(), compileEnv);
    }

    abstract CompileResult fix(CompileTestEnv compileEnv, String errorMessage);
}

