package org.regminer.migrate.api.core;

import java.io.File;

/**
 * @Author: sxz
 * @Date: 2022/06/08/23:39
 * @Description:
 */
public abstract class AbstractTestMigrator {
 public abstract boolean migrateFromTo(File from , File to);
}
