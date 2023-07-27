package com.emily.infrastructure.test.po.sensitive;

import com.emily.infrastructure.sensitive.JsonNullField;
import com.emily.infrastructure.sensitive.JsonSensitive;
import org.springframework.web.multipart.MultipartFile;

/**
 *  文件上传
 * @author  Emily
 * @since  Created in 2023/5/14 4:44 PM
 */
@JsonSensitive
public class UploadRequest {
    @JsonNullField
    private MultipartFile file;
    @JsonNullField
    private String fileName;
    @JsonNullField
    private int age;
    @JsonNullField
    private Integer ageW;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getAgeW() {
        return ageW;
    }

    public void setAgeW(Integer ageW) {
        this.ageW = ageW;
    }

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
