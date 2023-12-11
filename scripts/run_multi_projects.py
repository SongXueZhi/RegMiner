# This script is used for running RegMiner in several projects simultaneously.
# Script parameters:
# -jar: name of jar file(not jar path, make sure this script and jar are in the same folder)
# -ws: workspace path, which has a folder named "meta_projects", and the meta_projects folder contains all the projects
# -cfg: config file used by RegMiner jar(the config file is recommended to put in the same folder with this script)
# -t: task name, 'bfc' is for mining general bugs
#
# You should also provide a file named "project.in"(in the same folder),
# which contains the names of projects you want to mine.
#
# This script will create a folder for each project, and copy the jar and config file into the folder.
# Then this script will generate log files in each folder.
import subprocess
import os
from multiprocessing import Pool
import argparse

# script parameter example:
# -jar miner-1.0-SNAPSHOT-jar-with-dependencies.jar -ws /Users/user/reg_space/ -cfg env.properties -t bfc
parser = argparse.ArgumentParser(description='multi-process miner')
parser.add_argument('-jar', '--jar', help='name of jar file')
parser.add_argument('-ws', '--workspace', help='workspace path')
parser.add_argument('-cfg', '--config', help='path of config file')
parser.add_argument('-t', '--task', help='task')

args = parser.parse_args()


def process_line(project_name):
    print("start mining: " + project_name)
    project_name = project_name.strip()
    # for each project, create a folder, then copy all jars in the folder and config file
    subprocess.run(f"mkdir {project_name}_out;cp *.jar {args.config} .{os.sep}{project_name}_out", shell=True)
    # run the jar in the folder belongs to the project
    # jar parameter example:  -ws /Users/reg_space/ -pj univocity-parsers -cfg env.properties -t bfc (read README)
    # if the parameter shares the same name with the script parameter, use the script parameter
    subprocess.run(['java', '-jar', args.jar,
                    '-ws', args.workspace,
                    '-pj', project_name,
                    '-cfg', args.config,
                    '-t', args.task
                    ], cwd=f'.{os.sep}{project_name}_out')


if __name__ == '__main__':
    with open('project.in', 'r') as f:
        lines = f.readlines()

    with Pool() as p:
        p.map(process_line, lines)
