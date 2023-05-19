package com.emily.infrastructure.test.po.sensitive;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Description :  文件上传
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 4:44 PM
 */
public class UploadRequest {
    private MultipartFile file;
    private String fileName;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
