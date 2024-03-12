import os
from utils import read_file_to_list, download_logs, parse_logs


# remote server info and command
remote_dir = '/root/data/lzj_miner_space/scripts/'
remote_output_dir = 'output_bic/'


# local files/logs
local_log_dir = './logs/'

if not os.path.exists(local_log_dir):
    os.mkdir(local_log_dir)

project_list = read_file_to_list('./projects.in')

local_logs = [f'{local_log_dir}{project}.log' for project in project_list]

remote_logs = [f'{remote_dir}{remote_output_dir}{project}/logs/app.log' for project in project_list]


if __name__ == '__main__':
    downloaded_logs = download_logs(remote_logs=remote_logs, local_logs=local_logs)
    parse_logs(downloaded_logs=downloaded_logs, output_file='./report.csv')
