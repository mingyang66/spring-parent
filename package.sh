#./mvnw versions:set -DnewVersion=4.4.5
#echo '修改版本号'
#./mvnw versions:commit
#echo '提交修改'
./mvnw clean install -pl emily-project -am
./mvnw clean install -pl emily-dependencies -am
./mvnw clean install -pl emily-spring-boot-parent
echo '#########emily-parent...'
cd emily-project
../mvnw clean install -pl emily-json
../mvnw clean install -pl emily-logger
../mvnw clean install -pl emily-captcha
../mvnw clean install -pl emily-language
../mvnw clean install -pl emily-desensitize
../mvnw clean install -pl emily-jwt
../mvnw clean install -pl emily-date
../mvnw clean install -pl emily-common

cd ../emily-spring-project
../mvnw clean install -pl otter-spring-resource -am
../mvnw clean install -pl otter-spring-servlet
cd ../emily-spring-boot-project
# 独立
../mvnw clean install -pl emily-spring-boot-aop -am
../mvnw clean install -pl emily-spring-boot-tracing
../mvnw clean install -pl emily-spring-boot-logger
../mvnw clean install -pl emily-spring-boot-redis
../mvnw clean install -pl emily-spring-boot-validation
# 依赖其它
../mvnw clean install -pl emily-spring-boot-datasource
../mvnw clean install -pl emily-spring-boot-rabbitmq
../mvnw clean install -pl emily-spring-boot-starter
../mvnw clean install -pl emily-spring-boot-transfer
../mvnw clean install -pl emily-spring-boot-desensitize
../mvnw clean install -pl emily-spring-boot-i18n
../mvnw clean install -pl emily-spring-boot-rateLimiter
../mvnw clean install -pl emily-spring-boot-security
../mvnw clean install -pl emily-spring-boot-web
echo '#########打包完成...'