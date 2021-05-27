package callgraph.model;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

public class MethodCallGraph {
    //邻接列表顶点数组
    List<Node<Method>> vertexList;
    //邻接链表
    HashMap<Integer,AdjacencyList> matrix;
    //index = vertexList.size()-1
    int index;

    /**
     * 这么写的原因是为了直观的理解，每个graph被new时候都是初始状态
     * 其实没必要
     */
    public MethodCallGraph(){
        this.vertexList =new ArrayList<>();
        this.matrix = new HashMap<>();
        this.index = -1;
    }

    /**
     *
     * @param node
     * @return  若存在直接返回列表中index，若不存在则添加，返回指针index
     */
    public int addVertexIfNotContain(Node<Method> node){
        //顶点添加要求唯一
        if (vertexList.contains(node)){
            return  vertexList.indexOf(node);
        }
        //vertexList中不存在该顶点则添加
        vertexList.add(node);
        //指针指向新添加的节点
        ++index;
        //节点添加完成后需要matrix添加一个新的初始化的链表
        matrix.put(index,new AdjacencyList());
        return index;
    }

    /**
     *
     * @param parent 父亲节点
     * @param node 需要被添加的节点
     * @return
     * @throws NoParentNodeException 父亲节点不存在，请调用addNode(@NotNull Node<Method> node)，先添加父亲节点
     */
    public MethodCallGraph addNode(Node<Method> parent, @NotNull Node<Method> node) throws NoParentNodeException {
        //添加节点
        // 1. 首先确认vertexList中是否存在该节点,不存则添加
        //即对应方法 addVertexIfNotContain
        int nodeIndex =  addVertexIfNotContain(node);
       // 2.为目标节点的邻接表添加元素
        //获取parent节点在vertexList中的索引
        int vertexIndex = vertexList.indexOf(parent);
        if (vertexIndex < 0) {
           throw  new NoParentNodeException();
        }
        //parent的邻接列表中中添加元素
        matrix.get(vertexIndex).add(nodeIndex);
        return this;
    }

    /**
     * 方法允许添加没有父亲的节点
     * @param node 需要被添加的节点
     * @return
     */
    public MethodCallGraph addNode(@NotNull Node<Method> node) {
        //添加节点
        // 1. 首先确认vertexList中是否存在该节点,不存则添加
        //即对应方法 addVertexIfNotContain
        int nodeIndex =  addVertexIfNotContain(node);
        return this;
    }
    class NoParentNodeException extends Exception{

    }
}
