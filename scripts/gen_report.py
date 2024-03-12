import os
from utils import read_file_to_list, download_logs, parse_logs


# remote server info and command
# todo: remote_dir and remote_ci_dir
remote_dir = '/root/data/lzj_miner_ci_space/scripts/'
remote_output_dir = 'output_bic/'


# local files/logs
local_log_dir = './logs/'

if not os.path.exists(local_log_dir):
    os.mkdir(local_log_dir)

project_list = read_file_to_list('./projects.txt')

local_logs = [f'{local_log_dir}{project}.log' for project in project_list]

remote_logs = [f'{remote_dir}{remote_output_dir}{project}/logs/app.log' for project in project_list]


if __name__ == '__main__':
    download_logs(remote_logs=remote_logs, local_logs=local_logs)
    parse_logs(local_log_dir=local_log_dir, project_list=project_list)
