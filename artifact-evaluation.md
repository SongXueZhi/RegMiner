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
docker run -it issta-artifact
```

The working directory is `issta/`. Please allow approximately **50g** of disk space for the container, this is due to the fact that RegMiner will be constantly downloading project dependencies.

# Detailed Description

1. In the close-world experiment, we introduced the experimental procedure and results.
2. In the open-world experiment, we introduced how to use *RegMiner* to mine *regressions* in open-source projects.
3. In the dataset tool, we show how a user can use the mined *regressions.*

Please follow the process to ensure that everything runs according to expectation. The following experiments are independent of each other, but note that the results may be skewed due to the need to download *runtime dependencies* using tools such as *maven* when compiling.

## Folder Structure
All the folders that are relevant to the project can be found under ```/issta/regminer/```.

## Close-world Experiment + Ablation Study

We prepare 50 regression bugs and 50 non-regression bugs where the details can be referred in ```/issta/regminer/regressions.csv``` and ```/issta/regminer/non-regression.csv```. 
In this module, given a bug-fixing commit (either regression fixing commit or non-regression fixing commit) and a test case passing this commit, 
RegMiner is expected to locate

1. For regression fixing commit
- the regression-inducing commit where the test case fails
- the working commit where the test case passes

2. For non-regression fixing commit
- report the result that "this is not a regression"

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
```
Start processing RegMiner...

bug-fixing commit kdh0138 in project XXX is processed, identified as a regression bug,
its regression-inducing commit is abh0138, and its working commit is abh0169.

bug-fixing commit kdh0138 in project XXX is processed, not identified as a regression bug.

...

Start processing RegMiner¬TDM...

...

```

**Process log.**   You can see each *regression* search process in the `logmain` file under each project directory, steps as follow:

```bash
docker exec -it issta-artifact /bin/bash #start a new terminal
cd /issta/regminer/closed-world
cat -f projects/apache_commons-lang/logmain  # e.g. look up for apache/commons-lang search process
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