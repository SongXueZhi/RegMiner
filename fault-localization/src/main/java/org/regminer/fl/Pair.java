package org.regminer.fl;

/**
 * @Author: sxz
 * @Date: 2023/12/27/20:07
 * @Description:
 */
public class Pair<L, R> {

    private L left;

    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair))
            return false;
        Pair<?, ?> pairo = (Pair<?, ?>) o;
        return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
    }

    @Override
    public String toString() {
        return String.format("Pair: %s,%s", left.toString(), right.toString());
    }
}