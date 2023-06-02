package com.emily.infrastructure.file.test;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Description :  路径单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/28 10:38 AM
 */
public class FileUtilsTest {
    @Test
    public void path() throws IOException {
        File file = new File("/Users/yaomingyang/Documents/IDE/workplace-java/spring-parent/oceansky-file/src/test/resources/a.properties");
        if(!file.exists()){
            file.createNewFile();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Hello, world!");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
        }
    }
}
