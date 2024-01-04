#!/bin/bash

# 定义变量
SOURCE_DIR="/Users/doughit/fdse/RegMiner"
DESTINATION="root@10.176.34.99:/root/data/lzj_miner_space/scripts"

# 需要上传JAR包相对路径列表
JAR_PATHS=(
    "bic-search/target/bic-search-2.0-SNAPSHOT.jar"
    "code-migrate/target/code-migrate-2.0-SNAPSHOT.jar"
    "common/target/common-2.0-SNAPSHOT-jar-with-dependencies.jar"
    "miner/target/miner-1.0-SNAPSHOT-jar-with-dependencies.jar"
    "project-builder/target/project-builder-2.0-SNAPSHOT.jar"
    # ... 添加更多的路径
)

# 构造包含所有JAR包路径的字符串
JAR_FILES=""
for jar in "${JAR_PATHS[@]}"; do
    JAR_FILES+="${SOURCE_DIR}/${jar} "
done

# 使用scp一次性上传所有JAR包
scp $JAR_FILES "$DESTINATION"

echo "所有JAR文件已上传到远程服务器。"
