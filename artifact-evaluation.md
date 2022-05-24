[TOC]

In this document, we provide three aspects of artifact evaluation. RegMiner retrieves the runnable regressions from code evolution history. Specifically, taking input as a set of git repository, RegMiner can search and isolate a list of *runnable* regressions from those repositories. Each regression is manifested in terms of a test case passing a fixing version, failing a regression version, and passing the previous working version. 

# Getting Started

## Environment

**System**. We recommend to run on **ubuntu 18.04+** system, the system **network** needs to be accessible.

**Docker.** Make sure you have successfully installed docker according to the official docker [documentation](https://docs.docker.com/engine/install/ubuntu/ ). Here are the setup insturctions to run docker:

1）run docker with **sudo** privileges:

```bash
sudo groupadd docker # create docker group 
sudo gpasswd -a ${USER} docker # add current user to docker group 
sudo systemctl restart docker # restart docker service
newgrp docker #refresh
```

2）When using docker for the first time it is need to login first.

```bash
docker login
```

3）Try pulling ubuntu18.04 to make sure the docker is fully functional.

```bash
docker pull ubuntu:18.04
```

## Setup 

We provide RegMiner in a Docker environment. The docker file is issta-artifact.tar.gz.

After downloading the file issta-artifact.tar.gz, please use the following command to unzip the file:

```bash
tar -zxvf issta-artifact.tar.gz
```

Then, build the docker images by:

```bash
cd issta-artifact
docker build -t issta-artifact .
```

Then, build the docker container by:
```bash
docker run --name regminer -it issta-artifact
```

The working directory is `issta/`. Please allow approximately **50g** of disk space for the container, this is due to the fact that RegMiner will be constantly downloading project dependencies.
All the folders that are relevant to the project can be found under ```/issta/regminer/```.

## Make Your Hand Dirty (less than 30min)

For sake of intuitive understanding, we prepared 6 commits (3 regression-fixing commit and 3 non-regression fixing commits) to verify the functionalities of RegMiner, the process is estimated to take about **XXX minutes**.

**Step1:** Entry the woking dir.

```bash
cd /issta/regminer/demo
```

**Step2:** Run the scripts.

```bash
bash start.sh
```

Then, you will see the information on successful running.

```bash
Start processing RegMiner...
INFO - Input file loaded
INFO - Executing with 4 processes
INFO - Running miner on uniVocity_univocity-parsers

```

**Step3:** Check results

User can check the generated CSV file at /XXX/XXX/XXX.csv. 

```
show regressions /XXX/XXX/XXX.csv
```
will give us an overview:

```
Regression | Project | test case  | regression-fixing commit | regression-inducing commit | working commit
============================================================================================================
1          | proj1   |Class.test()| a89401f                  | a89301f                    | a89401d
============================================================================================================
2          | proj3   |Class.test()| b89401f                  | c89301f                    | d89401d
============================================================================================================
...
```

We can run the test on regression with id `id` on version regression-fixing commit by:
```
run regression -id 1 -rfc
```
User can change `rfc` to `ric` or `wc` to observe the test results on regression-inducing commit and working commit.

User can see Section of "Dataset Tool" for more usage.

# Detailed Description (~XXX min)
1. In the close-world experiment, we compare the mining performance of RegMiner and its variants.
2. In the open-world experiment, we evaluate the effectiveness of RegMiner to mine regressions in open-source projects.
3. In the dataset tool, we detail how a user can use the mined *regressions*.

## Close-world Experiment + Ablation Study

We prepare 50 regression-fixing commits and 50 non-regression fixing commits where the details can be referred in ```/issta/regminer/regressions.csv``` and ```/issta/regminer/non-regression.csv```. 


In the following, we prepare RegMiner and four of its variants (i.e., RegMiner¬TDM, RegMiner¬VEM+bisect,RegMiner¬TDM+bisect, RegMiner¬TDM+gitblame) and compare their precision and recall. 
The whole process take about XXX hours (we tested it on a  Linux server with 8-core 16-thread  Intel(R) Xeon(R) Silver 4208 CPU @ 2.10GHz, 32 Gigabyte RAM, and the operating system of Ubuntu Linux 18.04.).

**Step1:** Enter the working directory of the experiment.

```bash
cd /issta/regminer/closed-world
```

**Step2:** Run the experimental script.

```bash
bash start.sh
```

**Step3:** Confirm the experimental results

You can see the running progress as:
```bash
Start processing RegMiner
...
Start processing RegMiner¬TDM+blame
...
INFO - No results from spring-projects_spring-data-rest
INFO - spring-projects_spring-data-rest done
INFO - bug-fixing commit e5210d1f9ef4f1d41ff0a8c4a2ab8e9192d5e087 in project jhy_jsoup is processed.identified as a regression bug,its regression-inducing commit is df272b77c2cf89e9cbe2512bbddf8a3bc28a704b, and its working commit is df272b77c2cf89e9cbe2512bbddf8a3bc28a704b~1.
INFO - bug-fixing commit 397a0caeb374c55b8dcb58e09d0faebb6e017252 in project jhy_jsoup is processed.identified as a regression bug,its regression-inducing commit is b934c5d3e30917de86796c89fcb7cd000f642a80, and its working commit is b934c5d3e30917de86796c89fcb7cd000f642a80~1.
INFO - jhy_jsoup done
INFO - Processing spring-projects_spring-data-commons
INFO - Running miner on spring-projects_spring-data-commons
INFO - No results from spring-projects_spring-data-commons
INFO - spring-projects_spring-data-commons done
Running time: 1185.6796572208405 Seconds
10/50 regression found!
```

**Process log.**   You can see each *regression* search process in the `logmain` file under each project directory, steps as follow:

```bash
docker exec -it regminer /bin/bash #start a new terminal
cd /issta/regminer/closed-world
tail -f projects/apache_commons-lang/logmain  # e.g. look up for apache/commons-lang search process
# or view history search process
cat projects/apache_commons-lang/logmain 
```

**Final Result.** Get final result for RegMiner and four of its variants as follow command :

```bash
/issta/regminer/closed-world/projects
cat regression-miner.csv  # RegMiner
cat regression-tdm.csv    #RegMiner¬TDM
cat regression-vem-bisect.csv #RegMiner¬VEM+bisect
cat regression-tdm-bisect.csv #RegMiner¬TDM+bisect
cat regression-tdm-blame.csv #RegMiner¬TDM+blame
```
//TODO
```
compare-detailed-results
```
will give us: (note that R represents regression-fixing commit, NR represents non-regression fixing commit)
```
commit      | RegMiner | RegMiner¬TDM | RegMiner¬VEM+bisect | RegMiner¬TDM+bisect | RegMiner¬TDM+blame
====================================================================================================
ac9281 (R)  | found    | missed       | found               | missed              | found
====================================================================================================
ac928e (NR) | pass     | mis-report   | pass                | mis-report          | pass
====================================================================================================
...
====================================================================================================
precision   | 100 %    | 92.3 %       | 92.3 %              | 92.3 %               | 92.3 %
====================================================================================================
recall      | 100 %    | 100 %        | 100 %               | 100 %                | 100 %
```

##  Open-world Experiment 

We run RegMiner on 2 projects, and observe the regressions mined from those projects. 
Here, we prepare 1237 commits from the 2 projects, we expect that we can mine 83 regressions within 12 hours (we tested it on a  Linux server with 8-core 16-thread  Intel(R) Xeon(R) Silver 4208 CPU @ 2.10GHz, 32 Gigabyte RAM, and the operating system of Ubuntu Linux 18.04.).

To use regminer, navigate to ```open-world/regminer```
### Run and Configuration
**Step 1:** Entry the working dir.

```bash
cd /issta/regminer/open-world/regminer
```
**Step2:** Run the experimental script.

```bash
python3 Automation.py 
```

//TODO
**Step3:** Check results

User can check the generated CSV file at /XXX/XXX/XXX.csv. 

```
show regressions /XXX/XXX/XXX.csv
```
will give us an overview:

```
Regression | Project | test case  | regression-fixing commit | regression-inducing commit | working commit
============================================================================================================
1          | proj1   |Class.test()| a89401f                  | a89301f                    | a89401d
============================================================================================================
2          | proj3   |Class.test()| b89401f                  | c89301f                    | d89401d
============================================================================================================
...
```

## Dataset Tool

To showcase the possible use cases of the tool, we have also provided a command line interface tool that can help to retrieve and checkout the bugs. The tool can be run with 2 simple steps.

### Run and Configuration

**Step 1:**  Entry the working dir.

```
cd /issta/regminer/tool
```

**Step 2:** Run the tool by running ```CLI.sh```

```
bash CLI.sh
```

### List of Commands

To use the CLI tool, there are 7 different commands. The commands are as follows:

1. ```help``` - print out a help message containing all the available commands
2. ```projects``` - list out all the projects in the database with known regression bugs
3. ```use [project]``` - use the specified project as the basis for all the other operations
4. ```list``` - list the known regression for the specified project
5. ```checkout [idx]``` - checkout the regression at index idx in the list
6. ```similarity``` - get the similarity score for the regression that has been checked out
7. ```exit``` - exit the tool

### Example

An example of how to use the tool is as follows.

**Step 1:** The user will run the projects command to retrieve an up-to-date list of all the projects that have regression bugs in it. This will allow the user to know which project to operate on.

```
RegMiner > projects
Retrieving projects... Done
uniVocity/univocity-parsers              	apache/commons-lang                      
jhy/jsoup                                	jmrozanec/cron-utils
...
```

**Step 2:** After finding an appropriate project to operate on, the user will then specify to the tool to use that project. This will also tell the user how many regression bugs there are for this project.

```
RegMiner > use uniVocity/univocity-parsers
Using project: univocity/univocity-parsers
Retrieving regressions... 25 regressions found
```

**Step 3:** The user can choose to list the regression bugs to see which bug they are interested in. This also provide the testcase which allows the user to know the features that are affected by the regression bug.

```
RegMiner > list
1. rfc: XXXXX | ric: XXXXX | testcase: XXXXX
2. rfc: XXXXX | ric: XXXXX | testcase: XXXXX
...
```

**Step 4:** The user can now checkout one of the bug in the list. This checkout process may take a while, if the repository needs to be cloned. At the end of the checkout process, two paths will be output, where the former is the regression fixing commit directory and the latter is the regression inducing commit directory. These two directories can then be used for further testing if need be.

```
RegMiner > checkout 10
Checking out univocity/univocity-parsers regression 10
... <git messages> ...
rfc directory: /home/regminer/Documents/miner-space/transfer_cache/univocity_univocity-parsers/rfc
ric directory: /home/regminer/Documents/miner-space/transfer_cache/univocity_univocity-parsers/ric
```

**Step 5:** Lastly, the user can run the similarity command to calculate the similarity between the two commits (RFC and RIC), about **5 minutes** need. This is done by recording the code coverage of the two commits on the test case and comparing the two runs. More details of this calculation can be found in our paper.

```
RegMiner > similarity

Calculating similarity score for univocity/univocity-parsers regression bug 10...

Similarity score: 0.6949152542372882
```
