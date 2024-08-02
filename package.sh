./mvnw versions:set -DnewVersion=4.4.4
echo '修改版本号'
./mvnw versions:commit
echo '提交修改'
./mvnw clean install -pl oceansky-json -am
echo '#########oceansky-json打包完成...'
./mvnw clean install -pl oceansky-logger
./mvnw clean install -pl oceansky-captcha
./mvnw clean install -pl oceansky-language
./mvnw clean install -pl oceansky-sensitive
./mvnw clean install -pl oceansky-jwt
./mvnw clean install -pl oceansky-date
./mvnw clean install -pl oceansky-common
echo '#########oceansky-json打包完成...'
./mvnw clean install -pl emily-spring-boot-parent
echo '#########emily-spring-boot-parent打包完成...'
./mvnw clean install -pl emily-spring-boot-logger
echo '#########emily-spring-boot-logger打包完成...'
./mvnw clean install -pl emily-spring-boot-core
echo '#########emily-spring-boot-core打包完成...'
./mvnw clean install -pl emily-spring-boot-starter
echo '#########emily-spring-boot-starter打包完成...'
./mvnw clean install -pl emily-spring-cloud-starter
echo '#########emily-spring-cloud-starter打包完成...'
./mvnw clean install -pl emily-spring-boot-datasource
echo '#########emily-spring-boot-datasource打包完成...'
./mvnw clean install -pl emily-spring-boot-redis
echo '#########emily-spring-boot-redis打包完成...'
./mvnw clean install -pl emily-spring-boot-rabbitmq
echo '#########emily-spring-boot-rabbitmq打包完成...'
./mvnw clean install -pl emily-spring
echo '#########emily-spring打包完成...'
./mvnw clean install -pl emily-bom
echo '#########emily-bom...'