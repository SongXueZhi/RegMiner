from git import Repo
import os
from pathlib import Path
import time
import pymongo
import traceback

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
            fn = os.path.join(root, f)
            if "test" in fn and ".java" in fn:
                num = num + 1
    if flagP:
        build = "mvn"
    elif flagG:
        build = "gradle"
    res = {"num": num, "flag": flagP or flagG, "build": build, "pom_num": numP, "gradle_num": numG}
    return res

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["gitdata_2"]
mycol = mydb["repository"]
passsite = mydb["passsite"]
i = 0.0
#    dict = {"_id":repo.full_name,"full_name": repo.full_name, "issue_size": issue_len, "commit_size": commits_len,
#             "repo_size": repo.size, "status": False,"clone_url":repo.clone_url}
qury = {"status":"true"}

for n in (1,10000):
    try:
        cc = mycol.find(qury);

        cclist = list();
        for c in cc:
            cclist.append(c)

        print(len(cclist))
        for d in cclist:
            query = {"_id":d["full_name"]}
            count = passsite.count_documents(query)
            count1 = passsite.find(query).count()
            if count > 0 or count1 > 0:
                continue
            clone_url = d["clone_url"]
            fullname = d["full_name"].replace("/", "_")
            project_dir = "/home/sxz/db/" + fullname
            filex = Path(project_dir)
            if not filex.is_dir():
                try:
                    os.system("git clone "+clone_url+" "+project_dir)
                except Exception as ee:
                    print(ee)
                    time.sleep(15)
                    try:
                        os.system("bash /home/sxz/fd-net-auth.sh")
                        os.system("git clone "+clone_url+" "+project_dir)
                    except Exception:
                        print("clone Fal " + clone_url)
                        os.system("bash /home/sxz/fd-net-auth.sh")
                        continue              
            res = findAllFile(project_dir)
            testfiles_num = res["num"]
            flag = res["flag"]
            build = res["build"]
            print(fullname + " " + str(testfiles_num) + " " + build)
            os.system("rm -rf "+ project_dir)
            if (testfiles_num > 10 and flag):
                print("pass_x+1")
                d["tesfile_num"] = testfiles_num
                d["pom_num"] = res["pom_num"]
                d["gradle_num"] = res["gradle_num"]
                #         pass_dict ={"_id":re.full_name,"fullname":re.full_name,"tesfile_num":testfiles_num,"clone_url":clone_url,"build":build,
                #                     "pom_num":res["pom_num"], "gradle_num":res["gradle_num"]}
                passsite.save(d)
    except Exception as ex:
        traceback.print_exc()
