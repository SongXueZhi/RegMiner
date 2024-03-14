# RegMiner 2.0

A Mining Approach towards Automating Regression Dataset Construction from Code Evolution History.

Now regminer has an interactive visualization platform, you can use our platform to observe the data mining process and
the mined regressions.
The use of the platform is demonstrated as follows:

[![RegMiner Data Platform](https://github.com/SongXueZhi/images/blob/main/regminer/platshow.png)](https://youtu.be/yzcM9Y4unok "RegMiner Data Platform")

## Regressions4J

We also provide UI-free tools to help users replay test results for each regression bug,get that support in
the [regs4J](https://github.com/SongXueZhi/regressions4j) project.

## Tutorial

**Env requirements for RegMiner:**

1. OS: MacOs/Ubuntu/CenterOS 

2. JDK: 11

3. Python: 3.0+

Although we have support for the Windows system in our implementation, it has not undergone thorough testing.
**Env requirements for mining projects:**

1. JDK LTS: 1.7,1.8,11,17 

2. Jenv*(optional)

3. Others*: The specific environment required for a particular mining project, such as projects related to MongoDB middleware, may necessitate the installation of MongoDB.

**Easy Start:**

 Steps are as follows:

(1) Prepare the data.

1. Create a workspace for Miner.
   ``mkdir miner_space``

2. Create a directory for maintaining the source code of the mined project. 

```bash
  cd miner_space
  mkdir meta_projects
```

3. Prepare source code of the mined project. In example, we use uniVocity/univocity-parsers.

```bash
  cd meta_projects
  git clone https://github.com/uniVocity/univocity-parsers.git 
```
(2) Generate the configuration.

1. Generate a configuration file.

```bash
cd /xxx/xxxx/RegMiner/scripts
rm env.properties
python gen_config.py
mv env.properties ../
```

You can check the env.properties file to see if the configuration is correct. You can set sql_enable flag as 0 to disable writing to the database.

If you want to store the mined data in a database, you can ask the author for the database structure and the database configuration file.


2. Load RegMiner Project to IDEA.
3. Config debug params.
4. 
   ```
   -ws /xxx/xxx/miner_space/ -pj univocity-parsers -cfg env.properties -t bfc
   ```
   ``-t bfc`` means just mine bfc, ``-t bfc&bic`` means search regression.

5. Run RegMiner in ``miner/src/main/java/org/regminer/miner/start/MinerCli.java``, you can find progress info in ``logs/app.log``


## Run multiple projects
You can run multiple projects at the same time.

1. Prepare a list of project names in a file, one project per line, such as ``projects.txt``.

2. Run the ``run_multi_projects.py`` to mine the projects in the list.

You can see the comments in the scripts for more details.



## Paper

1. Xuezhi Song, Yun Lin*, Siang Hwee Ng, Yijian Wu, Xin Peng, Jin Song Dong and Hong Mei. RegMiner: Towards Constructing
   a Large Regression Dataset from Code Evolution History  **[⚡ CCF Prototype Competition Award (1st Prize)]**

2. Xuezhi Song, Yun Lin*, Yijian Wu, Yifan Zhang, Xin Peng, Jin Song Dong, and Hong Mei. RegMiner: Mining Replicable
   Regression Dataset from Code Repositories
