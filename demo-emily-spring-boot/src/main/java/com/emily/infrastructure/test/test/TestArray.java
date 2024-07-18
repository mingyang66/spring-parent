package com.emily.infrastructure.test.test;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.test.entity.Node;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/6/7 下午11:06
 */
public class TestArray {
    public static void main(String[] args) {
        List<People> list = List.of(
                new People("Emily", 18, 20),
                new People("田晓霞", 28, 165),
                new People("lili", 19, 28),
                new People("lili", 19, 21),
                new People("lili", 19, 22)
        );
        //System.out.println(JsonUtils.toJSONString(list));
        //List<People> data = list.stream().sorted(Comparator.comparing(People::getAge).thenComparing(People::getHeight).reversed()).toList();
        // Map<Integer, List<People>> map = list.stream().collect(Collectors.groupingBy(People::getAge)); //默认HashMap
        // Map<Integer, List<People>> map1 = list.stream().collect(Collectors.groupingBy(People::getAge, TreeMap::new, Collectors.toList())).descendingMap();
        //Map<Integer, List<Integer>> map2 = list.stream().collect(Collectors.groupingBy(People::getAge, LinkedHashMap::new, Collectors.mapping(People::getHeight, Collectors.toList())));
        //System.out.println(JsonUtils.toJSONPrettyString(map1));
        List<Node> l = list.stream().map(f -> {
            Node node = new Node();
            node.setCreator(f.getName());
            return node;
        }).toList();
        System.out.println(JsonUtils.toJSONPrettyString(l));
        List<People> p = list.stream().peek(f -> {
            System.out.println(f.getName());
        }).toList();
    }
}
