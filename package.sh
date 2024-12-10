#./mvnw versions:set -DnewVersion=4.4.5
#echo '修改版本号'
#./mvnw versions:commit
#echo '提交修改'
./mvnw clean deploy -pl emily-project -am
./mvnw clean deploy -pl emily-dependencies -am
echo '#########emily-parent...'
cd emily-project
../mvnw clean deploy -pl oceansky-json
../mvnw clean deploy -pl oceansky-logger
../mvnw clean deploy -pl oceansky-captcha
../mvnw clean deploy -pl oceansky-language
../mvnw clean deploy -pl oceansky-sensitize
../mvnw clean deploy -pl oceansky-jwt
../mvnw clean deploy -pl oceansky-date
../mvnw clean deploy -pl oceansky-common
../mvnw clean deploy -pl emily-spring-boot-parent
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
../mvnw clean deploy -pl emily-spring-boot-web
../mvnw clean deploy -pl emily-spring-boot-rateLimiter
../mvnw clean deploy -pl emily-spring-boot-i18n
echo '#########打包完成...'