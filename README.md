# RegMiner 2.0

A Mining Approach towards Automating Regression Dataset Construction from Code Evolution History. 
This project is powerd by [SE lab](http://www.se.fudan.edu.cn/) in fudan university

## Regressions4J

We also provide UI-free tools to help users replay test results for each regression bug,get that support in
the [regs4J](https://github.com/SongXueZhi/regressions4j) project.

## Tutorial

**Env requirements for RegMiner:**

1. OS: MacOs/Ubuntu/CenterOS 

2. JDK: 11

3. Python: 3.0+

Note that! We have discovered that RegMiner does not run well on Windows OS, and currently, we don't plan to fix it.

**Env requirements for mining projects:**

1. JDK LTS: 1.7,1.8,11,17 
2. maven & gradle
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

3. Prepare source code of the mined project. In example, we use ``uniVocity/univocity-parsers``.

```bash
  cd meta_projects
  git clone https://github.com/uniVocity/univocity-parsers.git 
```
(2) Generate the configuration.

1. Modify the configuration related to the database in ``scripts/gen_config.py``.
  
 ```bash
 sql_enable=0
 ```
This modification will disable the MySQL functionality of RegMiner. We currently do not provide the SQL table structure. If you need it, please contact [Xuezhi](songxuezhi@fudan.edu.cn).

3. Generate the configuration file.
   
```bash
cd /xxx/xxxx/RegMiner/scripts
rm env.properties
python gen_config.py
mv env.properties ../
```
These commands will generate the necessary configuration for running Regminer. The script will automatically detect the JDK installed on the system.

3. Load RegMiner Project to IDEA.
4. Config debug params.
   
```
-ws /xxx/xxx/miner_space/ -pj univocity-parsers -cfg env.properties -t bfc
```
``-t bfc`` means just mine bfc, ``-t bfc&bic`` means search regression.

5. Run RegMiner in ``miner/src/main/java/org/regminer/miner/start/MinerCli.java``, you can find progress info in ``logs/app.log``


## Automate batch mining.
Automated mining of target project sets.
1. Build the JAR package for RegMiner and place it in the ``scripts`` directory, and move ``scripts`` directory to ``miner_space``.
2. run ``python gen_config.py`` under ``scripts`` directory.
3. Prepare a list of project names in file ``projects.in`` under ``scripts`` directory, with one project per line.
4. Clone the source code of these projects with ``.git`` files into ``miner_space/meta_projects``.
5. Run the ``run_multi_projects.py`` under ``scripts`` directory to mine the projects in the list.

You can see the comments in the scripts for more details.



## Paper

1. Xuezhi Song, Yun Lin*, Siang Hwee Ng, Yijian Wu, Xin Peng, Jin Song Dong and Hong Mei. RegMiner: Towards Constructing
   a Large Regression Dataset from Code Evolution History  **[⚡ CCF Prototype Competition Award (1st Prize)]**

2. Xuezhi Song, Yun Lin*, Yijian Wu, Yifan Zhang, Xin Peng, Jin Song Dong, and Hong Mei. RegMiner: Mining Replicable
   Regression Dataset from Code Repositories
3. Xuezhi Song, Yijian Wu*, Junming Cao, Bihuan Chen, Yun Lin, Zhengjie Lu, Dingji Wang, Xin Peng. BugMiner: Automating Precise Bug Dataset Construction by Code Evolution History Mining   

## Acknowledgments
We are great thanks for [JetBrains](https://jb.gg/OpenSourceSupport) support on RegMiner.
