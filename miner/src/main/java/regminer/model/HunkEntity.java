package regminer.model;

/**
 * @author lsn
 * @date 2022/11/25 10:18 AM
 */
public class HunkEntity {
    String oldPath;
    String newPath;
    int beginA;
    int endA;
    int beginB;
    int endB;
    HunkType type;

    public enum HunkType{
        DELETE,
        REPLACE,
        INSERT,
        EMPTY
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public int getBeginA() {
        return beginA;
    }

    public void setBeginA(int beginA) {
        this.beginA = beginA;
    }

    public int getBeginB() {
        return beginB;
    }

    public void setBeginB(int beginB) {
        this.beginB = beginB;
    }

    public int getEndA() {
        return endA;
    }

    public void setEndA(int endA) {
        this.endA = endA;
    }

    public int getEndB() {
        return endB;
    }

    public void setEndB(int endB) {
        this.endB = endB;
    }

    public HunkType getType() {
        return type;
    }

    public void setType(String type) {
        switch (type) {
            case "DELETE":
                this.type = HunkType.DELETE;
                break;
            case "REPLACE":
                this.type = HunkType.REPLACE;
                break;
            case "INSERT":
                this.type = HunkType.INSERT;
                break;
            case "EMPTY":
                this.type = HunkType.EMPTY;
                break;
        }
    }

    @Override
    public String toString() {
        return "HunkEntity{" +
                "oldPath='" + oldPath + '\'' +
                ", newPath='" + newPath + '\'' +
                ", beginA=" + beginA +
                ", endA=" + endA +
                ", beginB=" + beginB +
                ", endB=" + endB +
                ", type=" + type +
                '}';
    }
}
