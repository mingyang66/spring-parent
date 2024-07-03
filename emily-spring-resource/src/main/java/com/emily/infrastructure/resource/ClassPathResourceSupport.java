package com.emily.infrastructure.resource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类路径和系统文件路径加载
 *
 * @author :  Emily
 * @since :  2024/7/3 下午23:26
 */
public class ClassPathResourceSupport {

    private static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * 读取指定路径下的文件，如：
     * <pre>@code{
     *      List<String> list = List.of("classpath*:mapper/mysql/*.xml", "classpath:mapper/oracle/*.xml","classpath:*.properties");
     * }</pre>
     *
     * @param list 资源路径
     * @return 检索到的资源路径
     * @throws IOException 检索文件异常
     */
    public List<Resource> getResources(List<String> list) throws IOException {
        List<Resource> resources = new ArrayList<>();
        for (String path : list) {
            resources.addAll(List.of(resolver.getResources(path)));
        }
        return resources;
    }
}
