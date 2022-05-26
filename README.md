# RegMiner

A Mining Approach towards Automating Regression Dataset Construction from Code Evolution History.

## Catalog

```
RegMiner:
	|
	|--- miner: Implementation of the core algorithm for searching bugs.
	 		|
	 		|--- src: Core code.
	 		|--- env.properties: Related configuration information of the project that needs to be mined.
			|
	|--- pre_compile: Precompile the target project and boost the model.
	|--- bfc_detector: Tool for detecting the number of BFC in the target project.
	|--- repo_select:
			|
			|--- gitminer.py: Collect open source projects through Github API. 
			|--- repository.csv: Current results of gitminer.py.
			|--- task2.py: Filter items in respository.csv.
			|--- passsite.csv: Projects that meet the conditions in task.py.
			|
```

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
