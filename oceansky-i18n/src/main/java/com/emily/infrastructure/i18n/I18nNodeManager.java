package com.emily.infrastructure.i18n;

/**
 * 节点管理
 *
 * @param <T>
 * @author Emily
 */
public class I18nNodeManager<T> {

    private I18nNode<T> root = new I18nNode<T>();

    public I18nNodeManager() {
        super();
    }

    public void add(char[] w, T value) {
        if (w.length < 1) {
            return;
        }
        I18nNode<T> p = root;
        for (int i = 0; i < w.length; i++) {
            I18nNode<T> n = p.getChildren().get(w[i]);
            if (n == null) {
                n = p.addChild(w[i]);
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
        I18nNode<T> ret = null;
        I18nNode<T> node = root;
        for (int i = offset; i < len; i++) {
            node = node.getChildren().get(sen[i]);
            if (node == null) {
                break;
            }
            if (node.isLeaf()) {
                ret = node;
            }
        }
        return ret;
    }

}
