from github import Github
import time
import pymongo

pom_file_name = "pom.xml"
gradle_file_name = "gradlew"

myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["gitdata_2"]
mycol = mydb["repository"]

g = Github("e05127ac04cbc19d896f171a5f125c0a12bd1bd3", per_page=100)
# 3000~100000 789
# 1600-3000 923
# 1000-1600 991
# 700-1000  100
# low = 3000
# high =100000
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
for t in (1,25):
    try:      
        for da in range_list:
            repositories = g.search_repositories(query='language:Java stars:%d..%d'% (da["low"], da["high"]), sort='stars', order='desc')
            # repositories = g.search_repositories(query='language:Java', sort='stars', order='desc')
            print("total task " + str(repositories.totalCount))
            for repo in repositories:
                print(repo,end=",")
                print("repo size "+str(repo.size),end=",")
                issues = repo.get_issues(state="closed")
                issue_len = issues.totalCount
                print('issue size ' + str(issue_len),end=",")
                commits = repo.get_commits()
                commits_len = commits.totalCount
                print('commit size ' + str(commits_len),end=",")
                dict = {"_id":repo.full_name,"full_name": repo.full_name, "issue_size": issue_len, "commit_size": commits_len,
                        "repo_size": repo.size, "status": False,"clone_url":repo.clone_url}
                if commits_len > 200 and repo.size < 102400:
                    dict["status"] = True
                    # res.append(repo)
                    # passlist.append(dict)
                    print("PASS",end=",")
                    print(repo.url)
                else:
                    print("reject")
                mycol.save(dict)
    except Exception as ex:
        print(ex)
        time.sleep(86400)
end = time.time()
print("total time use " + str(end - start))