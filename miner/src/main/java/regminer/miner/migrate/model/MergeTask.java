package regminer.miner.migrate.model;

import regminer.model.ChangedFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MergeTask {
    List<ChangedFile> elementList = new ArrayList<>();

    HashMap<String, ChangedFile> map = new HashMap<>();

    public MergeTask addAll(List<? extends ChangedFile> subList) {
        elementList.addAll(subList);
        return this;
    }

    //对任务通过路径唯一化
    public void compute() {
        for (ChangedFile file : elementList) {
            String key = file.getNewPath();
            if (map.containsKey(key)) {
                continue;
            }
            map.put(file.getNewPath(), file);
        }
    }

    public HashMap<String, ChangedFile> getMap() {
        return map;
    }
}
