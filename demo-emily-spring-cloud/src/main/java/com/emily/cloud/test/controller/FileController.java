package com.emily.cloud.test.controller;

import com.emily.cloud.test.helper.FileHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @program: spring-parent
 * @description: 文件控制器
 * @author: Emily
 * @create: 2022/01/11
 */
@RestController
@RequestMapping("api/file")
public class FileController {

    @GetMapping("getUrl")
    public List<String> getUrl() throws IOException {
        String url = FileHelper.getUrl(FileController.class, "application.yml");
        InputStream inputStream = FileController.class.getClassLoader().getResourceAsStream("application.yml");
        File docxFile = new File("docxTemplate.docx");
        FileUtils.copyToFile(inputStream, docxFile);
        return FileUtils.readLines(new File(url));
    }
}
