package org.regminer.ct.batch;

import junit.framework.TestCase;
import org.junit.Test;
import org.regminer.ct.ConfigLoader;

public class CtContextBatchTest extends TestCase {
    @Test
    public void testCompileBatch() throws Exception {
        ConfigLoader.refresh();
        CtContextBatch contextBatch = new CtContextBatch();
        contextBatch.compileBatch();
    }

    @Test
    public void testCompileBatchProjects() throws Exception {
        ConfigLoader.refresh();
        CtContextBatch contextBatch = new CtContextBatch();
        contextBatch.compileBatchProjects();
    }


}