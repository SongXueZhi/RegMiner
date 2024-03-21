import psutil
import datetime
import os
import schedule
import time


def kill_orphan_processes():
    print("=== Killing orphan processes ===")
    white_list = []
    with open('whitelist.txt', 'r') as file:
        for line in file:
            if line.startswith('#'):
                continue
            white_list.append(line.rstrip())

    for proc in psutil.process_iter():
        try:
            flag = True
            # 获取进程详细信息
            process_info = proc.as_dict(attrs=['pid', 'name', 'create_time', 'cmdline'])
            for white_list_item in white_list:
                if any(white_list_item in item for item in process_info['cmdline']):
                    flag = False
                    break
            if flag and process_info['name'] == 'java':
                create_time = datetime.datetime.fromtimestamp(process_info['create_time'])
                running_time = datetime.datetime.now() - create_time
                if running_time > datetime.timedelta(minutes=8*60):
                    print(f"Killing process {process_info['pid']} with command {process_info['cmdline']}")
                    os.kill(process_info['pid'], 9)

        except (psutil.NoSuchProcess, psutil.AccessDenied):
            print('No such process or access denied!')


schedule.every().hour.do(kill_orphan_processes)


while True:
    schedule.run_pending()
    time.sleep(900)
