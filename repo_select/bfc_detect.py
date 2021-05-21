import os
from pathlib import Path
import subprocess
from github import Github
import time
import pymongo

work_space ='/home/sxz/cache'
myclient = pymongo.MongoClient("mongodb://10.176.34.95:27017/")
mydb = myclient["gitdata_2"]
mycol = mydb["repo2"]
g = Github("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", per_page=100)

def findAllFile(base):
    num = 0
    flagP = False
    flagG = False
    numP = 0
    numG = 0
    build = "NONE"
    for root, ds, fs in os.walk(base):
        for f in fs:
            if "pom.xml" in f:
                flagP = True
                numP = numP + 1
            if "build.gradle" in f:
                flagG = True
                numG = numG + 1
            # fn = os.path.join(root, f)
            # if "test" in fn and ".java" in fn:
            #     num = num + 1
    if flagP:
        build = "mvn"
    elif flagG:
        build = "gradle"
    res = {"flag": flagP or flagG, "build": build, "pom_num": numP, "gradle_num": numG}
    return res

def exec(cmd):
    os.chdir(work_space);
    os.system(cmd+" >> log")
    # 读取文件里面每一行
    with open('log','rU') as file_object: 
        lines = file_object.readlines()
        file_object.close()
        os.system("rm -rf log")
        return lines
def clone(url):
    i =0; 
    while True:
        exec("rm -rf meta")
        exec("export ALL_PROXY=socks5://127.0.0.1:7891 && git clone "+url+" meta")
        my_file = Path("/home/sxz/cache/meta")
        if  my_file.is_dir and len(os.listdir("/home/sxz/cache/meta")) > 0:
            print("clone 成功")
            return
        else:
            i =i +1
            if i>30:
                os.system("bash fd-net-auth.sh")
def bfc_detect(url):
    clone(url)
    lines = exec("java -jar BFCDetect.jar meta/.git")
    for line in lines:
       if line.find('bfc num :') > -1:
           num = line.split('bfc num :')[1]
           num=num.replace('\n','')
           return num


if __name__ == "__main__":

    range_list = list()
    da={"low":3000,"high":100000}
    range_list.append(da)
    da1={"low":1600,"high":3000}
    range_list.append(da1)
    da2={"low":1000,"high":1600}
    range_list.append(da2)
    da3={"low":700,"high":1000}
    range_list.append(da3)

    start = time.time()
    while True:
        try:      
            for da in range_list:
                repositories = g.search_repositories(query='language:Java stars:%d..%d'% (da["low"], da["high"]), sort='stars', order='desc')
                # repositories = g.search_repositories(query='language:Java', sort='stars', order='desc')
                print("total task " + str(repositories.totalCount))
                for repo in repositories:
                    fullname= repo.full_name;
                    url = repo.clone_url;
                    res = mycol.count_documents({'_id':fullname}) # 可以理解为数据在mongo中出现的次数
                    if res != 0: 
                        continue
                    print(fullname)
                    num = bfc_detect(url)
                    dict = {"_id":fullname,"full_name":fullname,
                            "repo_size": repo.size,"clone_url":url,"bfc_num":num}
                    mycol.save(dict)
        except Exception as ex:
            print(ex)
            time.sleep(86400)
            exec('bash fd-net-auth.sh')
    end = time.time()
    print("total time use " + str(end - start))
   

    
    