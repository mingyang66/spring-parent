### Common-io工具方法读取远程服务器文件

```java
    /**
     *
     * @param url 服务器文件地址，如http://xxx.xx.xx.xx/a.txt
     * @return 文件内容
     */
    public static String readRemoteFile(String url){
        return readRemoteFile(url, null);
    }

    /**
     * 读取远程服务器上的文件
     * @param url 服务器文件地址，如http://xxx.xx.xx.xx/a.txt
     * @param encoding 编码方式，默认UTF8
     * @return 文件内容
     */
    public static String readRemoteFile(String url, String encoding){
        try {
            return IOUtils.toString(new URL(url).openStream(), encoding);
        } catch (Exception e) {
            throw new BusinessException(AppHttpStatus.READ_REMOTE_RESOURSE_EXCEPTION.getStatus(), StringUtils.join("读取文件：", url, "发生异常", e));
        }
    }
```

GitHub地址：[]()

