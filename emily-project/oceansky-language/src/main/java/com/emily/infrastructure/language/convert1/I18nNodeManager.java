package com.emily.infrastructure.language.convert1;

/**
 * 节点管理
 *
 * @param <T> 数据类型
 * @author Emily
 */
public class I18nNodeManager<T> {

    private final I18nNode<T> root = new I18nNode<T>();

    public I18nNodeManager() {
        super();
    }

    public void add(char[] w, T value) {
        if (w.length < 1) {
            return;
        }
        I18nNode<T> p = root;
        for (char c : w) {
            I18nNode<T> n = p.getChildren().get(c);
            if (n == null) {
                n = p.addChild(c);
            }
            p = n;
        }
        p.setLeaf(true);
        p.setValue(value);
    }

    public void add(String w, T value) {
        if (null == w) {
            return;
        }
        add(w.toCharArray(), value);

    }

    /**
     * 从Root节点读取匹配到节点
     *
     * @param sen    字符数组
     * @param offset 偏移量
     * @param len    字符个数
     * @return 匹配到的字符节点
     */
    public I18nNode<T> match(char[] sen, int offset, int len) {
        I18nNode<T> result = null;
        I18nNode<T> node = root;
        for (int i = offset; i < len; i++) {
            node = node.getChildren().get(sen[i]);
            if (node == null) {
                break;
            }
            if (node.isLeaf()) {
                result = node;
            }
        }
        return result;
    }

}
