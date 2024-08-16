#./mvnw versions:set -DnewVersion=4.4.5
echo '修改版本号'
#./mvnw versions:commit
echo '提交修改'
./mvnw clean install -pl emily-project -am
echo '#########emily-parent...'
cd emily-project
../mvnw clean install -pl oceansky-json
../mvnw clean install -pl oceansky-logger
../mvnw clean install -pl oceansky-captcha
../mvnw clean install -pl oceansky-language
../mvnw clean install -pl oceansky-sensitive
../mvnw clean install -pl oceansky-jwt
../mvnw clean install -pl oceansky-date
../mvnw clean install -pl oceansky-common
cd ../emily-spring-project
../mvnw clean install -pl otter-spring-resource
../mvnw clean install -pl otter-spring-servlet
cd ../emily-spring-boot-project
../mvnw clean install -pl emily-spring-boot-logger
../mvnw clean install -pl emily-spring-boot-core
../mvnw clean install -pl emily-spring-boot-starter
../mvnw clean install -pl emily-spring-boot-datasource
../mvnw clean install -pl emily-spring-boot-redis
../mvnw clean install -pl emily-spring-boot-rabbitmq
../mvnw clean install -pl emily-spring-boot-transfer
../mvnw clean install -pl emily-spring-boot-validation
../mvnw clean install -pl emily-spring-boot-web

echo '#########打包完成...'