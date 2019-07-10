# spring-parent
<h3>Spring Security OAuth2</h3> 

<h4>1.四种授权码模式</h4>
- 授权码模式
- 密码模式
- 客户端模式
- 简化模式
<h4>2.密码模式</h4>

```
http://localhost:9001/oauth/token?username=user&password=user&grant_type=password&client_id=client&client_secret=secret
```
- grant_type:授权类型，必选，此处固定值“password”<br>
- username：表示用户名，必选<br>
- password：表示用户密码，必选<br>
- scope：权限范围，可选<br>
<h4>3.授权码模式</h4>
- client_id：客户端ID，必选
- response_type：必须为code，必选
- redirect_uri：回掉url,必选
* 获取授权码：

```
http://localhost:9001/oauth/authorize?client_id=client&response_type=code&redirect_uri=http://localhost:9001/auth_user/get_auth_code
```
* 获取access_token

```
http://localhost:9001/oauth/token?grant_type=authorization_code&code=XQfMUi&client_id=client&client_secret=secret&redirect_uri=http://localhost:9001/auth_user/get_token_info
```
* 通过refresh_token获取access_token
```
http://localhost:9001/oauth/token?grant_type=refresh_token&refresh_token=Beared5d74d532ba446b58f78186013f5e170&client_id=client&client_secret=secret
```