package com.emily.sample.request.controller;

import com.emily.sample.request.entity.User;
import com.emily.sample.request.entity.UserRes;
import org.springframework.web.bind.annotation.*;

/**
 * <a href="https://apidocjs.com/">apiDoc</a>
 */
@RestController
public class User2Controller {
    /**
     * @api {get} /api/v2/request/getUsername 获取用户姓名
     * @apiVersion 1.0.0
     * @apiName getUsername
     * @apiGroup 2、用户请求控制器
     * @apiParam {String} [name] 用户名必填.
     * @apiSuccessExample 请求成功:
     * HTTP/1.1 200 OK
     * <pre>{@code
     * {
     *     "data": "钱钟书",
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
    @GetMapping("api/v2/request/getUsername")
    public String getUsername(@RequestParam String name) {
        return "钱钟书";
    }

    /**
     * @api {get} /api/v2/request/getUserById/:id 通过ID获取用户名
     * @apiVersion 1.0.0
     * @apiName getUserById
     * @apiGroup 2、用户请求控制器
     * @apiParam {String} id 用户唯一标识 必填.
     * @apiSuccess {String} firstname 用户名
     * @apiSuccess {String} lastname 姓氏
     * @apiSuccessExample Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "firstname": "John",
     * "lastname": "Doe"
     * }
     * @apiError UserNotFound 用户ID未找到
     */
    @GetMapping("api/v2/request/getUserById/{id}")
    public String getUserById(@PathVariable String id) {
        return "钱钟书" + id;
    }

    /**
     * @api {POST} /api/v2/request/getUserInfo 获取用户详细信息
     * @apiVersion 1.0.0
     * @apiName getUserInfo
     * @apiGroup 2、用户请求控制器
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
    @PostMapping("api/v2/request/getUserInfo")
    public UserRes getUserInfo(@RequestBody User user) {
        UserRes userRes = new UserRes();
        userRes.id = 12;
        userRes.age = 51;
        userRes.name = "刘震云";
        return userRes;
    }
}
