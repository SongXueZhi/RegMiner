import os
from pathlib import Path
import time
import pymongo

# mongo_url = "mongodb://10.176.34.95:27017/"
mongo_url = "mongodb://127.0.0.1:27017/"
work_space ='/Users/junming/cache'
myclient = pymongo.MongoClient(mongo_url)
mydb = myclient["gitdata_2"]
raw_col = mydb["repo2"]
value_col = mydb["repo_with_value"]
min_sync_bfc_num = 10
max_clone_retry = 3
max_compile_sample_num = 50 #对于一个repo最多采样编译的次数
min_compile_sample_interval = 10 #对于一个repo最小采样编译的间隔
mvn_compile_cmd = "mvn compile test-compile"
gradle_compile_cmd = "./gradlew compileJava compileTestJava"

# 将repo2中的repo同步到repo_with_value中，支持增量同步
# 只有bfc_num存在，且大于min_bfc_num的repo才会被同步过去
def synchronizeRepo():
    count = 0
    for item in raw_col.find({"synchronized": {"$exists": False}, "bfc_num": {"$exists": True}}):
        if not item['bfc_num']:
            print("[warning] empty bfc_num for repo %s" % item['_id'])
            continue
        item['bfc_num'] = int(item['bfc_num'])
        if item['bfc_num'] >= min_sync_bfc_num:
            value_col.insert_one(item)
            count += 1
        item["synchronized"] = True
        raw_col.update_one({"_id": item["_id"]}, {"$set": item})
    print("synchronized %d repos completed" % count)

def exec(cmd):
    os.system(cmd+" > log")
    # 读取文件里面每一行
    with open('log','r') as file_object: 
        lines = file_object.readlines()
    return lines

# cmd执行输出没有error，即为success
def isCMDSuccess(lines):
    for line in lines:
        if 'error' in line.lower():
            return False
    return True

def getRepoPath(name):
    return "%s/%s/meta" % (work_space, name)

def clone(url, name):
    i = 0
    repo_path = getRepoPath(name)
    if os.path.exists(repo_path):
        os.chdir(repo_path)
        exec("git checkout master")
        print("[Warning] repo %s already exists. skip clone" % name)
        return True
    exec("mkdir %s" % repo_path)
    while i < max_clone_retry:
        exec("rm -rf %s" % repo_path)
        exec("git clone %s %s" % (url, repo_path))
        if len(os.listdir(repo_path)) > 0:
            print("clone repo %s success" % name)
            return True
        else:
            i = i +1
            print("[Warning] retrying to clone repo %s" % name)
    print("[Warning] failed to clone repo %s" % name)
    return False

# TODO: 区分有没有用gradle wrapper的情况，要再添加一个gradlw的编译命令？
def compile(path):
    compile_cmd = None
    for file in os.listdir(path):
        if file == "pom.xml":
            compile_cmd = mvn_compile_cmd
        elif file == "build.gradle":
            compile_cmd = gradle_compile_cmd
    if not compile_cmd:
        print("[Error] not found compile file for %s" % path)
        return False
    log_lines = exec(compile_cmd)
    if not isCMDSuccess(log_lines):
        print("[Warning] failed to compile %s. cmd: %s" % (path, compile_cmd))
        return False
    return True

# TODO: compile一次commit后，对working dir的改变，会影响到checkout到其他的commit吗？
# TODO; 添加时间限制。计算第一次commit编译的时间，然后总限制时间除以编译的时间就是能尝试编译的次数(每次编译的时间大概会恒定吗)
# TODO; 第一次commit编译失败，就直接将这个项目抛弃？感觉可能存在较大问题(例如编译命令不对，jdk版本不对等)
# return: (isSuccess, commit_num, sample_failed_ratio)
def compile_sample_commits(path):
    os.chdir(path)
    git_log_cmd = "git log --pretty=oneline"
    git_checkout_cmd = "git checkout %s"
    commits = [line.split(" ")[0] for line in exec(git_log_cmd)]
    failed_compile_count = 0
    total_compile_count = 0
    compile_step = min_compile_sample_interval
    if len(commits) > max_compile_sample_num * min_compile_sample_interval: 
        compile_step =  int(len(commits)/max_compile_sample_num )
    i = 0
    pre_commit = commits[0]
    print("Compiling for repo %s. commit_num: %d. step: %d" % (path, len(commits), compile_step))
    while i < len(commits):
        commit = commits[i]
        print("Compiling for repo %s. commit : %s. %d/%d" % (path, commit, i, len(commits)))
        log_lines = exec(git_checkout_cmd % commit)
        if not isCMDSuccess(log_lines):
            print("[Error] failed to checkout %s. commit: %s. pre_commit %s." % (path, commit, pre_commit))
            return False, 0, 0
        else:
            if not compile(path):
                failed_compile_count += 1
        total_compile_count += 1
        pre_commit = commit
        i += compile_step
    return True, len(commits), failed_compile_count/total_compile_count
    
if __name__ == "__main__":
    # synchronizeRepo()

    # TODO: prediction value如何计算？几个参数[bfc_num, commit_num, failed_compile_ratio]
    # value = bfc_num * (1- failed_compile_ratio)
    items = value_col.find({"value_prediction": {"$exists": False}, "failed_clone": {"$exists": False}, "failed_sample_compile": {"$exists": False}})
    for item in items.sort('bfc_num', pymongo.DESCENDING):
        if not clone(item["clone_url"], item["full_name"]):
            item['failed_clone'] = True
        else:
            repo_path = getRepoPath(item['full_name'])
            flag, commit_num, failed_ratio = compile_sample_commits(repo_path)
            if flag:
                item['commit_num'] = commit_num
                item['failed_ratio'] = failed_ratio
                item['value_prediction'] = item['bfc_num'] * failed_ratio
            else:
                item['failed_sample_compile'] = True
        value_col.update_one({'_id': item["_id"]}, {'$set': item})
