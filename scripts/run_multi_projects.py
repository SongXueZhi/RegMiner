# This script is used for running RegMiner in several projects simultaneously.
# Script parameters:
# -jar: name of jar file(not jar path, make sure this script and jar are in the same folder)
# -ws: workspace path, which has a folder named "meta_projects", and the meta_projects folder contains all the projects
# -cfg: config used by RegMiner jar(recommended to put in the same folder with this script), default is env.properties
# -t: task name, 'bfc' is for mining general bugs, 'bfc&bic is for mining regressions', default is bfc
# -maxp: max process number, default is 4
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
parser.add_argument('-cfg', '--config', help='path of config file', default='env.properties')
parser.add_argument('-t', '--task', help='task', default='bfc')
parser.add_argument('-f', '--filter', help='filter', default='filter.txt')
parser.add_argument('-maxp', '--max_processes', help='max process count', default=4, type=int)

args = parser.parse_args()


def process_line(line):
    if line.startswith("#"):
        return  # Skip comment lines
    line = line.strip().split()
    project_name = line[0]
    commits = line[1:]

    print("start mining: " + project_name)
    project_name = project_name.strip()
    # for each project, create a folder, then copy all jars and config file to the folder
    project_dir = f".{os.sep}output_bic{os.sep}{project_name}"
    if not os.path.exists(project_dir):
        os.mkdir(project_dir)
    # if project_dir already exists, do not remove the dir,
    # but also copy the jar files (update the jar files and reserve the log files)
    subprocess.run(f"cp *.jar {args.config} {project_dir}", shell=True)
    # run the jar in the folder belongs to the project
    # jar parameter example:  -ws /Users/reg_space/ -pj univocity-parsers -cfg env.properties -t bfc (read README)
    # if the parameter shares the same name with the script parameter, use the script parameter
    if commits:
        filter_path = os.path.join(project_dir, args.filter)
        with open(filter_path, 'w') as filter_file:
            filter_file.write('\n'.join(commits))
        subprocess.run(['java', '-jar', args.jar,
                        '-ws', args.workspace,
                        '-pj', project_name,
                        '-cfg', args.config,
                        '-t', args.task,
                        '-f', args.filter
                        ], cwd=project_dir)
    else:
        subprocess.run(['java', '-jar', args.jar,
                        '-ws', args.workspace,
                        '-pj', project_name,
                        '-cfg', args.config,
                        '-t', args.task
                        ], cwd=project_dir)

if __name__ == '__main__':
    with open('project_commits.in', 'r') as f:
        lines = f.readlines()

    with Pool(processes=args.max_processes) as p:
        p.map(process_line, lines)
