[TOC]

In this document, we provide three aspects of artifact evaluation.

# Getting Started

RegMiner is a tool to retrieve the runnable regressions from code evolution history. Specifically, taking input as a set of git repository, RegMiner can search and isolate a list of *runnable* regressions from those repositories. Each regression is manifested in terms of a test case passing a fixing version, failing a regression version, and passing the previous working version.

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

# Detailed Description

1. In the quick start example, we prepare a small dataset running with prompt feedback on RegMiner and four of its variants.
2. In the dataset tool, we show how a user can use the mined *regressions.*
3. In the close-world experiment, we introduced the experimental procedure and results.
4. In the open-world experiment, we introduced how to use *RegMiner* to mine *regressions* in open-source projects.

Please follow the process to ensure that everything runs according to expectation. The following experiments are independent of each other, but note that the results may be skewed due to the need to download *runtime dependencies* using tools such as *maven* when compiling.

Note that! Parts 3 and 4 are time-consuming tasks to restore experimental data for the thesis.

## Folder Structure
All the folders that are relevant to the project can be found under ```/issta/regminer/```.

## Quick Start Example

We prepared 5 ground-truth regressions and 5 ground-truth non-regressions to verify the functionality of RegMiner, a process that took about 22 minutes and 3 regressions out.

Functionally, given a bug-fixing commit (either regression fixing commit or non-regression fixing commit) and a test case passing this commit, RegMiner is expected to locate

1. For regression fixing commit

- the regression-inducing commit where the test case fails
- the working commit where the test case passes
- report the result that "search fal"

2. For non-regression fixing commit

- report the result that "search fal". means this is not a regression

### Run Step by Step

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

### Monitoring the Process

RegMiner's runtime log will be output in a ``logmain`` file under each project. Please open another terminal for docker to monitor the lookup process, steps as follow:

**Step1:**  Open a **new terminal** for docker.

```bash
docker exec -it regminer /bin/bash
```

**Step2:**  Entry the target project dir.

```bash
cd issta/regminer/demo/projects/uniVocity_univocity-parsers/
```

**Step3:** Lookup process.

You can use `tail` command  to view progress in real time,

```bash
tail -f logmain
```

or use `cat` command to view history search process,

```bash
cat logmain
```

then you can get the information as like as :

```
1.0%
4bc0c553e40ce1e47d8d2a6a43df5c5923898f8c Start search
bic:1673f46bde0562d7b77151e65a09cd280d9a522d
PASS
Test bic [com.univocity.parsers.issues.github.Github_24#ensureExceptionsAreThrown:TESTSUCCESS]
bic:587a3d59da055afc533edb7df3874edc267c6db0
PASS
Test bic [com.univocity.parsers.issues.github.Github_24#ensureExceptionsAreThrown:TESTSUCCESS]
bic:5904bbee581a0668adef92259c2c1b385d66ebce
 CE 
....
Test bic [com.univocity.parsers.issues.github.Github_24#ensureExceptionsAreThrown:TESTSUCCESS]
bic:e7c1d0c8b888cc09fe4b6afc61ea6b5f1eb98a72
FAL
Test bic [com.univocity.parsers.issues.github.Github_24#ensureExceptionsAreThrown:NONE]
bic:378f318d7cf64a2b778a47a34bcc6f5a75483c11
PASS
Test bic [com.univocity.parsers.issues.github.Github_24#ensureExceptionsAreThrown:TESTSUCCESS]
regression+1
########################END SEARCH################################
```

### Final result 

After running you can see the message in the run window as like as ：

```bash
Start processing RegMiner...
INFO - Input file loaded
INFO - Executing with 4 processes
INFO - Running miner on uniVocity_univocity-parsers
INFO - bug-fixing commit da3d425307356d1e8a9a3569839c47e30d51a939 in project uniVocity_univocity-parsers is processed.identified as a regression bug,its regression-inducing commit is 356ce438d31e93785e3cea93e87fa51ea78fb5ad, and its working commit is 25a3715b9ce7b1d2de86af93e97c8cbf2d2c50bb.
INFO - bug-fixing commit 52e62f8d4d690627a56b8ab084ecdfbc5ae610bd in project uniVocity_univocity-parsers is processed.identified as a regression bug,its regression-inducing commit is abe0de1dc65540e8d8333630b397dae1b694aa34, and its working commit is f13d1d83cb9fa2e30c73b24bd486e613ba6d1820.
INFO - bug-fixing commit 4bc0c553e40ce1e47d8d2a6a43df5c5923898f8c in project uniVocity_univocity-parsers is processed.identified as a regression bug,its regression-inducing commit is e7c1d0c8b888cc09fe4b6afc61ea6b5f1eb98a72, and its working commit is 378f318d7cf64a2b778a47a34bcc6f5a75483c11.
INFO - uniVocity_univocity-parsers done
Running time: 1254.871446609497 Seconds
3/50 regression found!
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

**Step 5:** Lastly, the user can run the similarity command to calculate the similarity between the two commits (RFC and RIC). This is done by recording the code coverage of the two commits on the test case and comparing the two runs. More details of this calculation can be found in our paper.

```
RegMiner > similarity

Calculating similarity score for univocity/univocity-parsers regression bug 10...

Similarity score: 0.6949152542372882
```



## Close-world Experiment + Ablation Study

We prepare 50 regression bugs and 50 non-regression bugs where the details can be referred in ```/issta/regminer/regressions.csv``` and ```/issta/regminer/non-regression.csv```. 
In this module, given a bug-fixing commit (either regression fixing commit or non-regression fixing commit) and a test case passing this commit, 
RegMiner is expected to locate

1. For regression fixing commit
- the regression-inducing commit where the test case fails
- the working commit where the test case passes

2. For non-regression fixing commit
- report the result that "search fal". means this is not a regression

In the following, we prepare RegMiner and four of its variants (i.e., RegMiner¬TDM, RegMiner¬VEM+bisect,RegMiner¬TDM+bisect, RegMiner¬TDM+gitblame) and compare their precision and recall. 
In addition, our result is a *table* where each row is a bug-fixing commit, each column is an approach, and each entry shows yes/no (if yes, the commit ids of its regression-fixing commit and working commit)
The whole process take about 8 hours (we tested it on a  Linux server with 8-core 16-thread  Intel(R) Xeon(R) Silver 4208 CPU @ 2.10GHz, 32 Gigabyte RAM, and the operating system of Ubuntu Linux 18.04. ).

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
Start processing RegMiner...

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

Start processing RegMiner¬TDM...

...

```

**Process log.**   You can see each *regression* search process in the `logmain` file under each project directory, steps as follow:

```bash
docker exec -it regminer /bin/bash #start a new terminal
cd /issta/regminer/closed-world
tail -f projects/apache_commons-lang/logmain  # e.g. look up for apache/commons-lang search process
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



##  Open-world Experiment 

We run RegMiner  on 2 projects, and observe the regressions mined from those projects. 
Here, we prepare 1237 commits from the 2 projects, we expect that we can mine 83 regressions within 12 hours (we tested it on a  Linux server with 8-core 16-thread  Intel(R) Xeon(R) Silver 4208 CPU @ 2.10GHz, 32 Gigabyte RAM, and the operating system of Ubuntu Linux 18.04.).

To use regminer, navigate to ```open-world/regminer```
### Run and Configuration
**Step 1:** Entry the working dir.

```
cd /issta/regminer/open-world/regminer
```
**Step2:** Run the experimental script.

```bash
python3 Automation.py 
```
**Step 3:** The results can then be retrieved from ``regression.csv`` found in each of the project directory.
