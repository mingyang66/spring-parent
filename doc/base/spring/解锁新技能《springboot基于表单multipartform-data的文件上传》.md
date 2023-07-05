#### 解锁新技能《springboot基于表单multipart/form-data的文件上传》

> springboot的文件上传有多种实现方案，个人比较推荐方案三通过实体类属性绑定的方式，这样可以方便文件及其关联的相关属性字段；

开源依赖pom

```xml
<!-- 基于springboot的请求AOP拦截、返回值包装、全局异常处理SDK -->
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-starter</artifactId>
    <version>4.3.5</version>
</dependency>
```



##### 一、单个文件上传及其它属性字段

```java
    @PostMapping(value = "/uploadSingle", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadSingle(@RequestParam("file") MultipartFile file, @RequestParam("file1") MultipartFile file1, @RequestParam("name") String name, @RequestParam("desc") String desc) throws IOException {
        byte[] fileContent = file.getBytes();
        byte[] fileContent1 = file1.getBytes();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }
```

##### 二、通过数组方式上传多个文件

```java
    @PostMapping(value = "/uploadArray", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        byte[] fileContent = files[0].getBytes();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }
```

##### 三、通过实体类绑定方式上传文件及传递其它属性值

```java
    @PostMapping(value = "/uploadMul", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMul(@ModelAttribute("request") FileUploadRequest request) throws IOException {
        byte[] fileContent = request.getFile().getBytes();
        byte[] imageFileContent = request.getImageFile().getBytes();
        String accountCode = request.getAccountCode();
        String address = request.getAddress();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }
```

##### 四、通过实体类绑定方式上传多个文件

```java
    @PostMapping(value = "/uploadMuls", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMuls(@ModelAttribute FileUploadRequest[] request) throws IOException {
        byte[] fileContent = request[0].getFile().getBytes();
        byte[] imageFileContent = request[0].getImageFile().getBytes();
        String accountCode = request[0].getAccountCode();
        String address = request[0].getAddress();
        // 处理上传的文件内容
        return "File uploaded successfully!";
    }
```

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)