package org.regminer.miner.bic.api;

import org.regminer.miner.bic.api.core.AbstractBICSearcher;
import org.regminer.ct.api.ProjectBuilder;
import org.regminer.miner.migrate.api.TestMigrator;

/**
 * @Author: sxz
 * @Date: 2022/06/09/00:11
 * @Description:
 */
public class BICSearcher extends AbstractBICSearcher {
    private TestMigrator testMigrator;
    private ProjectBuilder projectBuilder;

    @Override
    public String[] getSearchSpace() {
        return new String[0];
    }

    @Override
    public String search() {
        return null;
    }

    public void setTestMigrator(TestMigrator testMigrator) {
        this.testMigrator = testMigrator;
    }

    public void setProjectBuilder(ProjectBuilder projectBuilder) {
        this.projectBuilder = projectBuilder;
    }
}
