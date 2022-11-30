package regminer.miner.dd;

import regminer.model.HunkEntity;
import regminer.utils.FileUtilx;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lsn
 * @date 2022/11/25 11:52 AM
 */
public class BFCFix {
    String path;
    String bfcPath;
    List<HunkEntity> hunkEntities;

    public BFCFix(String path, String bfcPath, List<HunkEntity> hunkEntities){
        this.path = path;
        this.bfcPath = bfcPath;
        this.hunkEntities = hunkEntities;
    }

    public void fix(String path, String bfcPath, List<HunkEntity> hunkEntities){
        try{
            Map<String,List<HunkEntity>> stringListMap = hunkEntities.stream().collect(Collectors.groupingBy(HunkEntity::getNewPath));
            for (Map.Entry<String,List<HunkEntity>> entry: stringListMap.entrySet()){
                fixFile(path,bfcPath,entry.getValue());
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }


    public static List<String> fixFile(String tmpPath, String bfcPath, List<HunkEntity> hunkEntities) throws IOException {
        HunkEntity tmpHunk = hunkEntities.get(0);
        List<String> line = new ArrayList<>();
        if(!Objects.equals(tmpHunk.getNewPath(), tmpHunk.getOldPath())){
            if(Objects.equals(tmpHunk.getOldPath(), "/dev/null")) {
                String fileFullRfcPath = bfcPath + File.separator + tmpHunk.getNewPath();
                String fileFullTmpPath = tmpPath + File.separator + tmpHunk.getNewPath();
                FileUtilx.copyFileToTarget(fileFullRfcPath,fileFullTmpPath);
            }
            else if(Objects.equals(tmpHunk.getNewPath(), "/dev/null")){
                line = FileUtilx.readListFromFile(tmpPath + File.separator + tmpHunk.getOldPath());
            }
            else {
                String fileFullRfcPath = bfcPath + File.separator + tmpHunk.getNewPath();
                String fileFullTmpPath = tmpPath + File.separator + tmpHunk.getNewPath();
                FileUtilx.copyFileToTarget(fileFullRfcPath,fileFullTmpPath);
                line = FileUtilx.readListFromFile(tmpPath + File.separator + tmpHunk.getOldPath());
            }

        }else {
            line = FileUtilx.readListFromFile(tmpPath + File.separator + tmpHunk.getOldPath());
        }
        hunkEntities.sort(new Comparator<HunkEntity>() {
            @Override
            public int compare(HunkEntity p1, HunkEntity p2) {
                return p2.getBeginA() - p1.getBeginA();
            }
        });

        for(HunkEntity hunkEntity: hunkEntities){
            HunkEntity.HunkType type = hunkEntity.getType();
            switch (type){
                case DELETE:
                    line.subList(hunkEntity.getBeginA(), hunkEntity.getEndA()).clear();
                    break;
                case INSERT:
                    List<String> newLine = getLinesFromRfcVersion(bfcPath,hunkEntity);
                    line.addAll(hunkEntity.getBeginA(),newLine);
                    break;
                case REPLACE:
                    line.subList(hunkEntity.getBeginA(), hunkEntity.getEndA()).clear();
                    List<String> replaceLine = getLinesFromRfcVersion(bfcPath,hunkEntity);
                    line.addAll(hunkEntity.getBeginA(),replaceLine);
                    break;
                case EMPTY:
                    break;
            }
        }
        FileUtilx.writeListToFile(tmpPath + File.separator + tmpHunk.getOldPath(),line);
        return line;
    }

    public static List<String> getLinesFromRfcVersion(String rfcPath, HunkEntity hunk){
        List<String> result = new ArrayList<>();
        List<String> line = FileUtilx.readListFromFile(rfcPath + File.separator + hunk.getNewPath());
        result = line.subList(hunk.getBeginA(), hunk.getEndA());
        return result;
    }
}
