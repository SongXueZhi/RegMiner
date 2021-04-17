# RegMiner

A Mining Approach towards Automating Regression Dataset Construction from Code Evolution History

## Catalog

```
RegMiner:
	|
	|--- miner: Implementation of the core algorithm for searching bugs.
	 		|
	 		|--- src: Core code.
	 		|--- env.properties: Related configuration information of the project that needs to be mined.
			|--- szz_find_bug_introducers-0.1.jar: Implementation of SZZUnleashed[1].
			|--- issues: SZZUnleashed input demo.
                        |--- results: SZZUnleashed output demo.
			|
	|--- pre_compile: Precompile the target project and boost the model.
	|--- bfc_detector: Tool for detecting the number of BFC in the target project.
	|--- repo_select:
			|
			|--- gitminer.py: Collect open source projects through Github API. 
			|--- repository.csv: Current results of gitminer.py.
			|--- task2.py: Filter items in respository.csv.
			|--- passsite.csv: Projects that meet the conditions in task.py.
			|
```

## Reference

[1] SZZUnleashed : an implementation of the SZZ algorithm , https://github.com/wogscpar/SZZUnleashed, Accessed in  21 March, 2021.

