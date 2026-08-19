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
../mvnw clean deploy -pl emily-logback
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
../mvnw clean deploy -pl aop-spring-boot-starter -am
../mvnw clean deploy -pl tracing-spring-boot-starter
../mvnw clean deploy -pl logger-spring-boot-starter
../mvnw clean deploy -pl redis-spring-boot-starter
../mvnw clean deploy -pl validation-spring-boot-starter
# 依赖其它
../mvnw clean deploy -pl desensitize-spring-boot-starter
../mvnw clean deploy -pl datasource-spring-boot-starter
../mvnw clean deploy -pl amqp-spring-boot-starter
../mvnw clean deploy -pl transfer-spring-boot-transfer
../mvnw clean deploy -pl i18n-spring-boot-starter
../mvnw clean deploy -pl rateLimiter-spring-boot-starter
../mvnw clean deploy -pl security-spring-boot-starter
../mvnw clean deploy -pl web-spring-boot-starter
cd ..
echo '#########package end...'