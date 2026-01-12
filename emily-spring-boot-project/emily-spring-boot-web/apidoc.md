##### 一、apidDoc创建源码的内部文档，官网地址

```properties
https://apidocjs.com/
```

##### 二、安装apiDoc

- 首先要安装node

```java
https://nodejs.org/en/download
```

- 安装apiDoc

```java
npm install apidoc -g
```

- 创建配置文件apidoc.json文件

```
{
  "name": "Spring Boot API文档",
  "version": "1.0.0",
  "description": "用户管理系统接口文档",
  "url": "http://localhost:8080",
  "input": ["/src/main/java/com/emily/sample/request/controller"],
  "output": "src/main/resources/static/doc",
  "apidoc": {
    "header": {
      "title": "Introduction",
      "filename": "header.md"
    },
    "footer": {
      "title": "Best practices",
      "filename": "footer.md"
    }
  }
}
```

- 在控制器的方法上编写apidoc注释

```java
    /**
     * @api {POST} /api/request/getUserInfo 获取用户详细信息
     * @apiVersion 1.0.0
     * @apiName getUserInfo
     * @apiGroup 用户请求控制器
     * @apiBody {Number} id 用户唯一标识
     * @apiBody {String=刘慈欣} name=刘婵 用户名
     * @apiBody {Boolean=true} sex 性别
     * @apiBody {String[]} arrays 字符串数组
     * @apiBody {String[]={刘备,刘婵}} list={刘婵} 字符串列表
     * @apiSuccess {Object} user 用户返回实体类
     * @apiSuccess {Number} user.id 用户唯一标识
     * @apiSuccess {String} user.name  用户名
     * @apiSuccess {Number} user.age  年龄
     * @apiSuccessExample 请求成功:
     * HTTP/1.1 200 OK
     * <pre>{@code
     * {
     *     "data": {
     *         "age": 51,
     *         "id": 12,
     *         "name": "刘震云"
     *     },
     *     "message": "SUCCESS",
     *     "spentTime": 0,
     *     "status": 0
     * }
     * }</pre>
     * @apiErrorExample {json} 请求失败:
     * <pre>{@code
     * {
     *     "data": null,
     *     "message": "非法参数",
     *     "spentTime": 0,
     *     "status": 100001
     * }
     * }</pre>
     */
    @PostMapping("api/request/getUserInfo")
    public UserRes getUserInfo(@RequestBody User user) {
        UserRes userRes = new UserRes();
        userRes.id = 12;
        userRes.age = 51;
        userRes.name = "刘震云";
        return userRes;
    }
```

- 生成apidoc文档

```java
apidoc -c apidoc.json
```

