### RestTemplate将返回数据转换为指定类型

##### 1.exchange方法中支持ParameterizedTypeReference<T>响应类型参数，可以将返回值转换为任何指定的类型

```java
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return nonNull(execute(url, method, requestCallback, responseExtractor, uriVariables));
	}
```

使用示例：

```java
    @GetMapping("xxx")
    public ResponseData<List<GmFundPageInfoResData.Bk>> test6(){
        String url = "http://192.xx.xx.3:xx/api/xx/home/xx";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("xx", "xx");

        HttpEntity<?> httpEntity = new HttpEntity<>(paramMap, null);
        ResponseEntity<ResponseData<List<xx.Bk>>> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ResponseData<List<xx.Bk>>>() {});
        System.out.println(JSONUtils.toJSONString(result.getBody()));

        return result.getBody();
    }
```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/httpclient](https://github.com/mingyang66/spring-parent/tree/master/doc/httpclient)

