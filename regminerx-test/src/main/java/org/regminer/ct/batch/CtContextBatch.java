package org.regminer.ct.batch;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.regminer.common.tool.RepositoryProvider;
import org.regminer.ct.ConfigLoader;
import org.regminer.ct.api.AutoCompileAndTest;
import org.regminer.ct.api.CtContext;
import org.regminer.ct.domain.JDK;
import org.regminer.ct.model.CompileResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class CtContextBatch {

    public void compileBatch() throws Exception {
        File projectDir = new File(ConfigLoader.projectPath);
        projectDir.setExecutable(true);
        projectDir.setReadable(true);
        compileProjectCommits(projectDir);
    }

    public void compileBatchProjects() throws Exception{
        Map<String, Map<JDK,Integer>> jdkNumberForProjects = new HashMap<>();
        ArrayList<String> projects = getProjectIn(ConfigLoader.projectIn);
        File projectsDir = new File(ConfigLoader.projectsDir);
        File[] files = projectsDir.listFiles();

        if(files == null || files.length == 0){
            System.out.println("Empty File Dir");
            return;
        }
        for (File file : files) {
            if (projects.contains(file.getName())) {
                projects.remove(file.getName());
                compileProjectCommits(new File(file.getAbsolutePath()));
            }
        }
        if(projects.size() > 0){
            System.out.println("PROJECT " + projects + " Not Found");
        }
    }

    public void compileProjectCommits(File projectDir) throws Exception {
        HashMap<JDK, Integer> jdkNumber = new HashMap<>();
        int commitNumber = 0;
        int commitError = 0;
        try (Repository repository = RepositoryProvider.getRepoFromLocal(projectDir); Git git = new Git(repository)) {
            Iterator<RevCommit> commits = git.log().all().call().iterator();
            int i = 0;
            System.out.println(projectDir.getName() + " START ···" );
            while (commits.hasNext()) {
                RevCommit revCommit = commits.next();
                i++;
                if (i % 2000 == 1) {
                    commitNumber++;
                    git.checkout().setName(revCommit.getName()).setForced(true).call();
                    CtContext ctContext = new CtContext(new AutoCompileAndTest());
                    ctContext.setProjectDir(projectDir);
                    CompileResult result = ctContext.compile();

                    git.reset().setMode(ResetCommand.ResetType.HARD).call();
                    if (result.getCompileWay() != null) {
                        if (jdkNumber.containsKey(result.getCompileWay().getJdk())) {
                            int oldValue = jdkNumber.get(result.getCompileWay().getJdk());
                            jdkNumber.put(result.getCompileWay().getJdk(), oldValue + 1);
                        } else {
                            jdkNumber.put(result.getCompileWay().getJdk(), 1);
                        }

                        System.out.println(revCommit.getName() + ":" + result.getState().name()
                                + ":" + result.getCompileWay().getCompiler().name() + ":" + result.getCompileWay().getJdk().name());
                    } else {
                        commitError++;
                        System.out.println(revCommit.getName() + ":" + result.getState().name() + ":" + "CompileWay NULL");
                    }
                }
            }
            System.out.println(projectDir.getName() + " FINISH. ");
            System.out.println("Commit Number: " + commitNumber);
            System.out.println("Commit Error: " + commitError);
            Set<JDK> keys = jdkNumber.keySet();
            for (JDK key : keys) {
                System.out.print(key + " = " + jdkNumber.get(key) + ";  ");
            }
            System.out.println("\n-------------------");
        }
    }
    public ArrayList<String> getProjectIn(String projectIn){
        ArrayList<String> projects = new ArrayList<>();
        try {
            FileReader fr = new FileReader(projectIn);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            while ((str = bf.readLine()) != null) {
                projects.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projects;
    }
}
