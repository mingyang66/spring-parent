package com.emily.boot.test.controller;

import com.emily.boot.test.helper.FileHelper;
import com.emily.infrastructure.common.utils.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
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
    public List<String> getUrl() {
        String url = FileHelper.getUrl(FileController.class, "application.yml");
        InputStream inputStream = FileController.class.getClassLoader().getResourceAsStream("application.yml");
        File docxFile = new File("docxTemplate.docx");
        FileUtils.copyToFile(inputStream, docxFile);
        return FileUtils.readLines(url);
    }
}
