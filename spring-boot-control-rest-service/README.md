### Spring boot之 RestTemplate组件

#### 1.Rest HTTP Client请求组件【com.eastmoney.emis.rest】
>支持POST、GET请求，支持文件上传、支持数组参数传递、支持HTTP、HTTPS

* 使用方式,示例如下
```
    @Autowired
    private HttpClientService httpClientService;
    @RequestMapping(value = "/handler/test4")
    public void testUrl1(@RequestBody @Valid User user) throws IOException{
        String url = "http://172.30.67.122:9000/handler/upload";
        FileSystemResource resource = new FileSystemResource(new File("D:\\work\\ssr\\pac.txt"));
        FileSystemResource resource1 = new FileSystemResource(new File("D:\\work\\ssr\\gui-config.json"));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.put("jarFile", Arrays.asList(resource, resource1));
        params.put("fileName", Arrays.asList("liming", "hello"));
        String result = httpClientService.postMulti(url, params, String.class);
        System.out.println(result);
    }
```
