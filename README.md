# RegMiner

A Mining Approach towards Automating Regression Dataset Construction from Code Evolution History.

Now regminer has an interactive visualization platform, you can use our platform to observe the data mining process and
the mined regressions.
The use of the platform is demonstrated as follows:

[![RegMiner Data Platform](https://github.com/SongXueZhi/images/blob/main/regminer/platshow.png)](https://youtu.be/yzcM9Y4unok "RegMiner Data Platform")

## Regressions4J

We also provide UI-free tools to help users replay test results for each regression bug,get that support in
the [regs4J](https://github.com/SongXueZhi/regressions4j) project.

## Tutorial

**Env requirements for Regminer:**

OS: Macos/Ubuntu/CenterOS 
JDK: 11
Python: 3.0+

Although we have support for the Windows system in our implementation, it has not undergone thorough testing.
**Env requirements for mining projects:**

JDK LTS: 1.7,1.8,11,17 
Jenv*(optional)
Others*: The specific environment required for a particular mining project, such as projects related to MongoDB middleware, may necessitate the installation of MongoDB.

**Easy Start:**

 Steps are as follow:
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

2. Load RegMiner Project to IDEA.
3. Config debug params.
4. 
   ```
   -ws /xxx/xxx/miner_space/ -pj univocity-parsers -cfg env.properties -t bfc
   ```
   ``-t bfc`` means just mine bfc, ``-t bfc&bic`` mean search regression.

5. Run RegMimer in ``miner/src/main/java/org/regminer/miner/start/MinerCli.java``, you can find progress info in ``logs/app.log``


## Paper

1. Xuezhi Song, Yun Lin*, Siang Hwee Ng, Yijian Wu, Xin Peng, Jin Song Dong and Hong Mei. RegMiner: Towards Constructing
   a Large Regression Dataset from Code Evolution History  **[⚡ CCF Prototype Competition Award (1st Prize)]**

2. Xuezhi Song, Yun Lin*, Yijian Wu, Yifan Zhang, Xin Peng, Jin Song Dong, and Hong Mei. RegMiner: Mining Replicable
   Regression Dataset from Code Repositories
