# spring-parent
<h3>Spring Security OAuth2</h3> 

<h4>1.四种授权码模式</h4>
- 授权码模式
- 密码模式
- 客户端模式
- 简化模式
<h4>2.密码模式</h4>
<pre>http://localhost:9001/oauth/token?username=user&password=user&grant_type=password&client_id=client&client_secret=secret</pre>
- grant_type:授权类型，必选，此处固定值“password”
- username：表示用户名，必选
- password：表示用户密码，必选
- scope：权限范围，可选
