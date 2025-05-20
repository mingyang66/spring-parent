#./mvnw versions:set -DnewVersion=4.4.5
#echo '修改版本号'
#./mvnw versions:commit
#echo '提交修改'
./mvnw clean deploy -pl emily-project -am
./mvnw clean deploy -pl emily-dependencies -am
./mvnw clean deploy -pl emily-spring-boot-parent
echo '#########emily-parent...'
cd emily-project
../mvnw clean deploy -pl emily-json
../mvnw clean deploy -pl emily-logger
../mvnw clean deploy -pl emily-captcha
../mvnw clean deploy -pl emily-language
../mvnw clean deploy -pl emily-desensitize
../mvnw clean deploy -pl emily-jwt
../mvnw clean deploy -pl emily-date
../mvnw clean deploy -pl emily-common

cd ../emily-spring-project
../mvnw clean deploy -pl otter-spring-resource -am
../mvnw clean deploy -pl otter-spring-servlet
cd ../emily-spring-boot-project
# 独立
../mvnw clean deploy -pl emily-spring-boot-aop -am
../mvnw clean deploy -pl emily-spring-boot-tracing
../mvnw clean deploy -pl emily-spring-boot-logger
../mvnw clean deploy -pl emily-spring-boot-redis
../mvnw clean deploy -pl emily-spring-boot-validation
# 依赖其它
../mvnw clean deploy -pl emily-spring-boot-datasource
../mvnw clean deploy -pl emily-spring-boot-rabbitmq
../mvnw clean deploy -pl emily-spring-boot-starter
../mvnw clean deploy -pl emily-spring-boot-transfer
../mvnw clean deploy -pl emily-spring-boot-desensitize
../mvnw clean deploy -pl emily-spring-boot-i18n
../mvnw clean deploy -pl emily-spring-boot-rateLimiter
../mvnw clean deploy -pl emily-spring-boot-security
../mvnw clean deploy -pl emily-spring-boot-web
echo '#########打包完成...'