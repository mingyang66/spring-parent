package com.emily.infrastructure.i18n;

import java.util.HashMap;
import java.util.Map;

/**
 * 节点
 *
 * @param <T>
 * @author Emily
 */
public class I18nNode<T> {
    /**
     * 节点级别
     */
    private int level;
    /**
     * 节点key
     */
    private char key;
    /**
     * 节点value
     */
    private T value;
    /**
     * 是否是叶子节点
     */
    private boolean leaf;
    /**
     * 子节点
     */
    private Map<Character, I18nNode<T>> children = new HashMap<>();

    public I18nNode() {

    }

    public I18nNode(char key) {
        super();
        this.level = 0;
        this.key = key;
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public I18nNode<T> addChild(char k) {
        I18nNode<T> node = new I18nNode<T>(k);
        node.level = this.level + 1;
        children.put(k, node);
        return node;
    }

    public Map<Character, I18nNode<T>> getChildren() {
        return children;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
