package org.regminer.ct.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.regminer.common.exec.Executor;
import org.regminer.common.tool.MavenDependencyProvider;
import org.regminer.common.tool.MavenManager;
import org.regminer.common.utils.FileUtil;
import org.regminer.ct.CtReferees;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.domain.JDKs;
import org.regminer.ct.model.CompileResult;
import org.regminer.ct.model.CompileTestEnv;
import org.regminer.ct.model.CtCommands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public enum OriginCompileFixWay {

    JDK_SEARCH(3) {
        @Override
        CompileResult fix(CompileTestEnv compileEnv, String errorMessage) {
            CompileResult compileResult = new CompileResult(CompileResult.CompileState.CE);
            compileResult.setExceptionMessage(errorMessage);
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
            CompileResult compileResult = new CompileResult(CompileResult.CompileState.CE);
            compileResult.setExceptionMessage(errorMessage);
            // 分析编译日志，找到有问题的依赖
            List<String> problematicDependencies = CtReferees.detectProblematicDependencies(errorMessage);

            File pomFile = new File(compileEnv.getProjectDir(), "pom.xml");
            MavenManager mavenManager = new MavenManager();

            // 只修复 SNAPSHOT 问题
            problematicDependencies = problematicDependencies
                    .stream().filter(s -> s.endsWith(SNAPSHOT))
                    .collect(Collectors.toList());
            try {
                // 尝试移除每一个有问题的依赖的 SNAPSHOT 版本
                Model model = mavenManager.getPomModel(pomFile);
                boolean isModified = false;
                for (String dependency : problematicDependencies) {
                    isModified |= removeSnapshotFromCurrent(model, dependency);
                    isModified |= removeSnapshotFromParent(model, dependency);
                    isModified |= removeSnapshotFromDependency(model, dependency);
                }
                if (isModified) {
                    mavenManager.saveModel(pomFile, model);
                    logger.info("removed");
                    // 再次尝试编译
                    CompileResult attemptResult = recompileProject(compileEnv);
                    if (attemptResult.getState() == CompileResult.CompileState.SUCCESS) {
                        logger.info("Compile successful after fixing dependencies: {}", problematicDependencies);
                        return attemptResult;
                    }
                    compileResult.setExceptionMessage(attemptResult.getExceptionMessage());
                }
            } catch (Exception e) {
                logger.error("Error while modifying POM for dependencies {}: {}", problematicDependencies, e.getMessage());
            }

            return compileResult;
        }

        private boolean removeSnapshotFromCurrent(Model model, String probDepend) {
            if (model.getVersion() != null
                    && probDepend.startsWith(model.getGroupId() + ":" + model.getArtifactId())
                    && model.getVersion().endsWith(SNAPSHOT)) {
                logger.info("Trying to remove '{}' from {}", SNAPSHOT, model.getVersion());
                model.setVersion(model.getVersion().replace(SNAPSHOT, ""));
                return true;
            }
            return false;
        }

        private boolean removeSnapshotFromParent(Model model, String probDepend) {
            if (model.getParent() != null
                    && probDepend.startsWith(model.getParent().getGroupId() + ":" + model.getParent().getArtifactId())
                    && model.getParent().getVersion().endsWith(SNAPSHOT)) {
                logger.info("Trying to remove '{}' of parent {}", SNAPSHOT, model.getParent());
                String parentVersion = model.getParent().getVersion();
                model.getParent().setVersion(parentVersion.replace(SNAPSHOT, ""));
                return true;
            }
            return false;
        }

        private boolean removeSnapshotFromDependency(Model model, String probDepend) {
            boolean isModified = false;
            Properties properties = model.getProperties();
            for (Dependency dependency : model.getDependencies()) {
                // 构建 groupId 和 artifactId 的组合
                String dependencyIdentifier = dependency.getGroupId() + ":" + dependency.getArtifactId();

                if (probDepend.startsWith(dependencyIdentifier)) {
                    logger.info("Trying to remove '{}' from dependency of {}", SNAPSHOT, dependency);
                    String depVersion = resolveProperty(dependency.getVersion(), properties);

                    if (depVersion.endsWith(SNAPSHOT)) {
                        dependency.setVersion(depVersion.replace(SNAPSHOT, ""));
                        isModified = true;
                    }
                }
            }
            return isModified;
        }

        private String resolveProperty(String value, Properties properties) {
            if (value.startsWith("${") && value.endsWith("}")) {
                String propertyName = value.substring(2, value.length() - 1);
                return properties.getProperty(propertyName, value);
            }
            return value;
        }
    },
    DEPENDENCY_FIX(2) {
        @Override
        CompileResult fix(CompileTestEnv compileEnv, String errorMessage) {
            CompileResult compileResult = new CompileResult(CompileResult.CompileState.CE);
            compileResult.setExceptionMessage(errorMessage);
            // Implement logic for DEPENDENCY_FIX
            // Example: Resolve dependency issues, handle exceptions, and recompile
            Set<String> missingDependencies = CtReferees.findMissingDependencies(errorMessage);
            List<Dependency> dependencies = new ArrayList<>();
            for (String missingDependency : missingDependencies) {
                dependencies.addAll(MavenDependencyProvider.getAllMavenDependencies(missingDependency));
            }
            File pomFile = new File(compileEnv.getProjectDir(), "pom.xml");
            MavenManager mavenManager = new MavenManager();
            try {
                logger.info("Trying to add missing dependencies");
                Model model = mavenManager.getPomModel(pomFile);
                // 可能只有一个是有用的
                for (Dependency dependency : dependencies) {
                    Model modelCopy = cloneModel(model);
                    modelCopy.addDependency(dependency);
                    mavenManager.saveModel(pomFile, modelCopy);
//                    logger.info("added");
                    // 再次尝试编译
                    compileResult = recompileProject(compileEnv);
                    if (compileResult.getState() == CompileResult.CompileState.SUCCESS) {
                        logger.info("Compile successful after add dependency: {}", dependency);
                        return compileResult;
                    }
                }
                // 如果都没成功，就恢复原文件
                mavenManager.saveModel(pomFile, model);

            } catch (Exception e) {
                logger.error("Error while modifying POM for dependencies {}: {}", dependencies, e.getMessage());
            }
            return compileResult;
        }
        private Model cloneModel(Model originalModel) throws Exception {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            MavenXpp3Writer writer = new MavenXpp3Writer();
            StringWriter stringWriter = new StringWriter();

            writer.write(stringWriter, originalModel);
            return reader.read(new StringReader(stringWriter.toString()));
        }

    },
    PACKAGE_FIX(4) {
        @Override
        CompileResult fix(CompileTestEnv compileEnv, String errorMessage) {
            CompileResult compileResult = new CompileResult(CompileResult.CompileState.CE);
            compileResult.setExceptionMessage(errorMessage);
            // 分析编译日志，找到有冲突的包
            Map<String, List<String>> conflictingPackages = CtReferees.detectClassNameConflicts(errorMessage);

            for (Map.Entry<String, List<String>> entry : conflictingPackages.entrySet()) {
                String fileName = entry.getKey();
                List<String> packages = entry.getValue();

                for (String pkg : packages) {
                    try {
                        logger.info("Trying to import {} in {}", pkg, fileName);
                        // 添加正确的包导入
                        FileUtil.addImportStatement(fileName, pkg);
                        logger.info("imported");
                        // 再次尝试编译
                        CompileResult attemptResult = recompileProject(compileEnv);
                        if (attemptResult.getState() == CompileResult.CompileState.SUCCESS) {
                            logger.info("Compile successful after import {} in {}", pkg, fileName);
                            return attemptResult;
                        }
                    } catch (Exception e) {
                        logger.error("Error while modifying {} for package statement {}: {}", fileName, pkg, e.getMessage());
                    }
                    // 只尝试引入一个 package
                    break;
                }
            }

            return compileResult;
        }
    };
    private Integer order;

    public Integer getOrder() {
        return this.order;
    }

    OriginCompileFixWay(Integer order) {
        this.order = order;
    }


//    protected Logger logger = LogManager.getLogManager().getLogger(this.name());
    protected Logger logger = LogManager.getLogger(this.name());


    private static CompileResult recompileProject(CompileTestEnv compileEnv) {
        String message = new Executor()
                .setDirectory(compileEnv.getProjectDir())
                .exec(compileEnv.getCtCommand().compute())
                .getMessage();

        CompileResult result = new CompileResult(CtReferees.JudgeCompileState(message), compileEnv.getCtCommand(), compileEnv);
        result.setExceptionMessage(message);
        return result;
    }

    abstract CompileResult fix(CompileTestEnv compileEnv, String errorMessage);
}

