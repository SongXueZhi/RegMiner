# RegMiner

## Tool Env

We recommend RegMiner running on `Linux` distributions and `MacOs`.

### Regminer & backend

> `Java 11`, `Mysql 5.6+`, `python3`

### Interactive UI

> `Node.js`, `Yarn`



## Bug Runtime Env

`Maven`, `gradle`, as many JDK versions as possible.

In this demo we only need `java 8` and `Maven`.



## Step by Step

Here we take the `ubuntu20.04` system as an example.

### Prepare resource content

1. Regminer & backend

   Paste the **miner_space.zip** we provided into the **{user.home}** directory, for example, the path of this article is `/home/sxz`

   ```
   unzip miner_sapce.zip
   ```

   

### Environment configuration

1. install `Java 8` & `python3`

   ```
   sudo apt-get install openjdk-8-jdk
   sudo apt-get install python3
   ```

2. install `Maven`

   ```
   sudo apt-get install maven
   ```

3. install `java 11`

   ```
   sudo apt-get install openjdk-11-jdk
   ```

4. Configure `java 8` as environment variable

   ```
   sudo update-alternatives --config java
   ```

   Select the number for Java 8, and record the path for Java 11 as a backup. For example, the native Java 11 path is ` /usr/lib/jvm/java-11-openjdk-amd64/bin/java`

5. install `Node.js` & `Yarn`

   ```
   curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -
   sudo apt-get install -y nodejs
   npm install yarn
   ```

6. install `Mysql`

   Please install `Mysql` according to the [official documentation](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-22-04). And confirm that the installation is successful (please set the mysql password to Aa110120, and verify that mysql -u root -p can log in), and run the following command to create a table:

   ```
   CREATE DATABASE code_annotation;
   USE code_annotation;
   CREATE TABLE `account` (
     `id` int(255) NOT NULL AUTO_INCREMENT,
     `username` varchar(32) NOT NULL,
     `password` varchar(32) NOT NULL,
     `email` varchar(32) DEFAULT NULL,
     `role` varchar(255) NOT NULL DEFAULT 'user',
     PRIMARY KEY (`id`)
   ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
   CREATE TABLE `project` (
     `project_uuid` varchar(255) CHARACTER SET latin1 NOT NULL,
     `organization` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `project_name` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `url` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     PRIMARY KEY (`project_uuid`),
     UNIQUE KEY `weiyi` (`organization`,`project_name`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
   CREATE TABLE `regression` (
     `id` int(10) unsigned zerofill NOT NULL AUTO_INCREMENT,
     `regression_uuid` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `regression_status` int(11) DEFAULT '0',
     `project_uuid` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `project_full_name` varchar(255) CHARACTER SET latin1 NOT NULL,
     `bug_id` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `bfc` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `buggy` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `bic` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `work` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
     `testcase` text CHARACTER SET latin1,
     `order_value` int(255) DEFAULT NULL,
     `descript_value` text COLLATE utf8mb4_bin,
     `with_gap` int(255) DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE KEY `weiyi` (`bug_id`) USING BTREE
   ) ENGINE=InnoDB AUTO_INCREMENT=904 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;
   ```

   

### Start 

1. start RegMiner
   First configure the `java 11` path for **{user.home}/miner_space/miner_jar/bash.sh**

   ```
   # The previously saved java 11 path, if it is inconsistent here, please change it
   /usr/lib/jvm/java-11-openjdk-amd64/bin/java -jar ./miner.jar
   cd {user.home}/miner_space/
   ```

2. Modify the Mysql configuration in lines 64-68 of **{user.home}/miner_space/Automation.py**, such as the configuration in this machine:

   ```
   if SQL_SUPPORT:
       output.append("sql_enable=1")
       output.append("sql_url=jdbc:mysql://127.0.0.1:3306/code_annotation?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF8")
       output.append("username=root")
       output.append("passwd=Aa110120")
   ```

3. Start

   ```
   python3 Automation.py 
   ```



### Start backend

```
/usr/lib/jvm/java-11-openjdk-amd64/bin/java -jar RegMiner-Backend-0.0.1-SNAPSHOT.jar
```



### Start UI

```
cd {user.home}/miner_space/code-annotation-web
yarn install
yarn start
```

