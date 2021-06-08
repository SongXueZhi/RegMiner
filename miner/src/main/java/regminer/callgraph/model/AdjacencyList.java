package regminer.callgraph.model;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyList {
    List<Integer> list = new ArrayList<>();

    public AdjacencyList add(Integer index){
        list.add(index);
        return this;
    }
}
