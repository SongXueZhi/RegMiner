import paramiko
import os
import time
import csv
import re


remote_host = '10.176.34.99'
remote_username = 'root'
remote_password = 'Aa110120.'


def read_file_to_list(file_path):
    lines = []
    with open(file_path, 'r') as file:
        for line in file:
            if line.startswith('#'):
                continue
            lines.append(line.rstrip())
    return lines


# upload files(like jar or config/script files) to remote server
def upload_files(files, remote_dir):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(remote_host, username=remote_username, password=remote_password)
    sftp = ssh.open_sftp()
    for local_file in files:
        filename = os.path.basename(local_file)
        remote_file = os.path.join(remote_dir, filename)
        sftp.put(local_file, remote_file)
    sftp.close()
    ssh.close()


def run_remote_command_pend(stdout_file, stderr_file, remote_command, remote_dir):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(remote_host, username=remote_username, password=remote_password)
    ssh.exec_command('cd {}'.format(remote_dir))
    _, stdout, stderr = ssh.exec_command(remote_command)

    with open(stdout_file, 'w') as out_file, open(stderr_file, 'w') as err_file:
        out = stdout.read().decode('utf-8')
        err = stderr.read().decode('utf-8')
        out_file.write(out)
        err_file.write(err)
        print(out)
        print(err)
    stdout.channel.recv_exit_status()
    ssh.close()


def run_remote_command(stdout_file, stderr_file, remote_command, remote_dir):  # todo: cannot exit the shell!
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(remote_host, username=remote_username, password=remote_password)

    # 创建交互式 shell 会话
    shell = ssh.invoke_shell()

    # 发送命令并在交互式 shell 会话中执行
    shell.send('cd {}\n'.format(remote_dir).encode('utf-8'))
    shell.send('{}\n'.format(remote_command).encode('utf-8'))

    # 实时将 stdout 和 stderr 输出写入文件
    with open(stdout_file, 'w') as out_file, open(stderr_file, 'w') as err_file:
        while True:
            if shell.recv_ready():
                stdout_data = shell.recv(1024).decode('utf-8')
                out_file.write(stdout_data)
                print('stdout:', stdout_data)
                time.sleep(0.1)

            if shell.recv_stderr_ready():
                stderr_data = shell.recv_stderr(1024).decode('utf-8')
                err_file.write(stderr_data)
                time.sleep(0.1)
                print('stderr:', stderr_data)
            if not ssh.get_transport().is_active() or shell.exit_status_ready():
                break

    ssh.close()


def download_logs(remote_logs, local_logs):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(remote_host, username=remote_username, password=remote_password)
    sftp = ssh.open_sftp()
    downloaded_logs = []
    for remote_log, local_log in zip(remote_logs, local_logs):
        try:
            sftp.get(remote_log, local_log)
            print(f'Download {remote_log} to {local_log} successfully')
            downloaded_logs.append(local_log)
        except FileNotFoundError:
            print("Remote file", remote_log, "does not exist. Skipping.")
            if os.path.exists(local_log):
                os.remove(local_log)
            continue
    sftp.close()
    ssh.close()
    return downloaded_logs


def calculate_bfc_ce_cnt(log_lines):
    start_point = 'Start bfc&bic task on'
    # end_point = r'find (\d+) BFCs'
    end_point = r'- total bfc: (\d+), find bfc: (\d+)'
    pbfc_pattern = r'pRFC in total :(\d+)'

    is_inside = False
    ce_cnt = 0
    pbfc_cnt = -1
    bfc_cnt = 0
    pbfc_fail = 0
    for line in log_lines:
        if re.search(start_point, line):
            is_inside = True
        elif re.search(end_point, line):
            bfc_cnt = int(re.search(end_point, line).group(2))
            break
        elif is_inside:
            match = re.search(pbfc_pattern, line)
            if match:
                pbfc_cnt = int(match.group(1))
            elif 'BFC compile error' in line:
                ce_cnt += 1
            elif '- BFC all test fal' in line:
                pbfc_fail += 1

    return ce_cnt, pbfc_cnt, bfc_cnt, pbfc_fail


def calculate_bic_cnt(log_lines):
    # start_point = 'start to search bic'
    bic_start = r"Start search (\b[a-fA-F0-9]+\b), search space is (\d+)"
    bic_end = r'find bic'  # find bic:  or  find bic failed
    # start_bic_search = False
    in_bic_search = False
    has_pass_but_not_bic = 0
    bic_cnt = 0
    tmp_str = ''
    for line in log_lines:
        # if not re.search(start_point, line) and not start_bic_search:
        #     continue
        # start_bic_search = True

        if re.search(bic_start, line):
            in_bic_search = True
            tmp_str = ''
        elif re.search(bic_end, line):
            in_bic_search = False
            if 'failed' not in line:
                bic_cnt += 1
            elif '- result: PASS' in tmp_str:
                has_pass_but_not_bic += 1

        elif in_bic_search:
            tmp_str += line
    return bic_cnt, has_pass_but_not_bic


def parse_logs(downloaded_logs, output_file='report.csv'):
    with open(output_file, 'w') as rate_csv_file:
        rate_writer = csv.writer(rate_csv_file)
        rate_writer.writerow(['project', 'pBFC count', 'CE count', 'CE rate', 'BFC count', 'BFC rate', 'pBFC fail',
                              'pBFC fail rate', 'BIC count', 'Has pass but not BIC'])

        for local_log in downloaded_logs:
            project = local_log.split('/')[-1].replace('.log', '')
            with open(local_log, 'r') as f:
                log_content = f.read()
                log_lines = log_content.split('\n')

                ce_cnt, pbfc_cnt, bfc_cnt, pbfc_fail = calculate_bfc_ce_cnt(log_lines)
                bic_cnt, has_pass_but_not_bic = calculate_bic_cnt(log_lines)
                print(f'{project}: pBFC: {pbfc_cnt}, CE: {ce_cnt}, CE rate: {round(ce_cnt / pbfc_cnt * 100, 2)}%, BFC: '
                      f'{bfc_cnt}, BFC: {round(bfc_cnt / pbfc_cnt * 100, 2)}%, pBFC fail: {pbfc_fail}, pBFC fail rate: '
                      f'{round(pbfc_fail / pbfc_cnt * 100, 2)}%, BIC: {bic_cnt}, '
                      f'Has pass but not BIC: {has_pass_but_not_bic}')

                rate_writer.writerow([project, pbfc_cnt, ce_cnt, f'{round(ce_cnt / pbfc_cnt * 100, 2)}%', bfc_cnt,
                                      f'{round(bfc_cnt / pbfc_cnt * 100, 2)}%', pbfc_fail,
                                      f'{round(pbfc_fail / pbfc_cnt * 100, 2)}%', bic_cnt, has_pass_but_not_bic])

