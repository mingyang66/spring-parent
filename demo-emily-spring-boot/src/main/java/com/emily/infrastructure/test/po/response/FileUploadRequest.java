package com.emily.infrastructure.test.po.response;

import com.emily.infrastructure.sensitive.JsonNullField;
import com.emily.infrastructure.sensitive.JsonSensitive;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传参数
 *
 * @author Emily
 * @since Created in 2023/7/2 1:21 PM
 */
@JsonSensitive
public class FileUploadRequest {
    @JsonNullField
    private MultipartFile file;
    @JsonNullField
    private MultipartFile imageFile;
    private String accountCode;
    private String address;

    public MultipartFile getFile() {
        return file;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
