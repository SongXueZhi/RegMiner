# RegMiner

A Mining Approach towards Automating Regression Dataset Construction from Code Evolution History.

Now regminer has an interactive visualization platform, you can use our platform to observe the data mining process and the mined regressions.
The use of the platform is demonstrated as follows:

[![RegMiner Data Platform](https://github.com/SongXueZhi/images/blob/main/regminer/platshow.png)](https://youtu.be/yzcM9Y4unok "RegMiner Data Platform")
We have included visualization platforms in the latest release of RegMiner, you can also launch them from code:

UI: https://github.com/SongXueZhi/code-annotation-web

Visualization Platforms: https://github.com/SongXueZhi/code-annotation-platform

## Contribute to miner

 **Env requirement:**  

 Distinguishing between miner and mined projects, Miner relies on JDK11 and we recommend developing in Ubuntu.  

 **Regression test:**  

 Regression testing needs to be done before submitting the code. Steps are as follow:  

1. Create a workspace for Miner,
 ``mkdir miner_space``

2. Create a directory of projects to be mined(we used univocity-parsers as the test data in the regression test),

```bash
  cd miner_space
  mkdir univocity-parsers
```

3. Provide metadata for miner on projects,

```bash
  cd miner_space
  git clone https://github.com/uniVocity/univocity-parsers.git meta
```

4. Configure the  workspace of miner and project names for the projects to be mined, and turn off the use of SQL functionality,

```properties
# Configuration file path : xxx/Regminer/miner/env.properties
project_name =univocity-parsers
root_dir =/home/xxx/miner_space/
# Turn off sql function
sql_enable =0
```

Note that! Miner does not reprocess already processed commits, so regression testing requires removing progress files generated in the mined project directory and turning off SQL functionality.
```bash
rm -f xxxx/miner_space/univocity-parsers/progress.details
```

## Contribute regressions

1. Download our latest [realase](https://github.com/SongXueZhi/RegMiner/releases), or install miner by self.
   
2. In order to avoid data duplication, we will update the latest batch database files continuously, load our database into local mysql, and set ``SQL_enable =1``.

3. Next add the configuration for local mysql
   
```properties
# Configuration file path : xxx/Regminer/miner/env.properties
project_name =univocity-parsers
root_dir =/home/xxx/miner_space/
################mysql config######################
sql_enable =1
sql_url =jdbc:mysql://x.x.x.x:3306/regression?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF8
username =root
passwd =xxxx
```

4. Execute "Regminer/miner/resources/sql/regression.sql" file in mysql to import data.

5. Configure the JDk11 path for the execution file "run.sh"
   E.g.

   ```bash
   /usr/lib/jvm/java-11-openjdk-amd64/bin/java -jar ./miner.jar
   ```

## Paper

Xuezhi Song, Yun Lin*, Siang Hwee Ng, Yijian Wu, Xin Peng, Jin Song Dong and Hong Mei. RegMiner: Towards Constructing a Large Regression Dataset from Code Evolution History  **[⚡ CCF Prototype Competition Award (1st Prize)]**
