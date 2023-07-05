package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.autoconfigure.response.annotation.ApiResponseWrapperIgnore;
import com.emily.infrastructure.common.PropertiesUtils;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.test.po.response.FileUploadRequest;
import com.emily.infrastructure.test.po.response.Wrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @Description :  返回值包装测试控制器
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/27 1:59 PM
 */
@RestController
@RequestMapping("api/response")
public class ResponseController {

    @GetMapping("wrapper")
    public String wrapper() {
        return null;
    }

    @GetMapping("wrapperException")
    public String wrapperException() {
        throw new IllegalArgumentException("wrapperException");
    }

    @GetMapping("ignore")
    @ApiResponseWrapperIgnore
    public String ignore() {
        return "ignore";
    }

    @GetMapping("ignoreException")
    @ApiResponseWrapperIgnore
    public String ignoreException() {
        throw new IllegalArgumentException("非法参数");
    }

    @GetMapping("getBaseResponse")
    public BaseResponse<Wrapper> getBaseResponse() {
        Wrapper wrapper = new Wrapper();
        wrapper.username = "田晓霞";
        wrapper.password = "密码";

        BaseResponse<Wrapper> response = new BaseResponse<>();
        response.setStatus(2000);
        response.setMessage("成功了");
        response.setData(wrapper);
        return response;
    }

    @GetMapping("getEntity")
    public Wrapper getEntity() {
        Wrapper wrapper = new Wrapper();
        wrapper.username = "田晓霞";
        wrapper.password = "密码";
        return wrapper;
    }

    @GetMapping("getVoid")
    public void getVoid() {
        System.out.println("返回值为void");
    }

    @GetMapping("getResponseEntity")
    public ResponseEntity<String> getResponseEntity() {
        return ResponseEntity.ok("测试");
    }

    @GetMapping("getResponseEntityWrapper")
    public ResponseEntity<Wrapper> getResponseEntityWrapper() {
        Wrapper wrapper = new Wrapper();
        wrapper.username = "田晓霞";
        wrapper.password = "密码";
        return ResponseEntity.ok(wrapper);
    }

    @GetMapping("getResponseEntityString")
    public ResponseEntity<BaseResponse<String>> getResponseEntityString() {
        BaseResponse<String> response = new BaseResponse<>();
        response.setStatus(2000);
        response.setMessage("成功了");
        response.setData("strdding");
        return ResponseEntity.ok(response);
    }

    @GetMapping("getBytes")
    public byte[] getBytes() {
        return "abcdesf".getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("getInts")
    public Integer[] getInts() {
        Integer[] s = new Integer[]{1, 2, 3};
        return s;
    }

    @GetMapping("getImage")
    public void getImage() throws IOException {
        InputStream fis = PropertiesUtils.getFileAsStream("image/12.png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        byte[] imageBytes = bos.toByteArray();
        fis.close();
        bos.close();
        RequestUtils.getResponse().setContentType(MediaType.IMAGE_PNG_VALUE);
        RequestUtils.getResponse().getOutputStream().write(imageBytes);
    }

    @GetMapping(path = "getImageByte", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageByte() throws IOException {
        InputStream fis = PropertiesUtils.getFileAsStream("image/12.png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        byte[] imageBytes = bos.toByteArray();
        fis.close();
        bos.close();
        return imageBytes;
    }

    /**
     * 文件上传，支持同时传多个参数
     */
    @PostMapping(value = "/uploadMul", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMul(@ModelAttribute("request") FileUploadRequest request) throws IOException {
        byte[] fileContent = request.getFile().getBytes();
        byte[] imageFileContent = request.getImageFile().getBytes();
        String accountCode = request.getAccountCode();
        String address = request.getAddress();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }

    @PostMapping(value = "/uploadMuls", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMuls(@ModelAttribute FileUploadRequest[] request) throws IOException {
        byte[] fileContent = request[0].getFile().getBytes();
        byte[] imageFileContent = request[0].getImageFile().getBytes();
        String accountCode = request[0].getAccountCode();
        String address = request[0].getAddress();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }

    @PostMapping(value = "/uploadSingle", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadSingle(@RequestParam("file") MultipartFile file, @RequestParam("file1") MultipartFile file1, @RequestParam("name") String name, @RequestParam("desc") String desc) throws IOException {
        byte[] fileContent = file.getBytes();
        byte[] fileContent1 = file1.getBytes();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }

    @PostMapping(value = "/uploadArray", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        byte[] fileContent = files[0].getBytes();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }

}
