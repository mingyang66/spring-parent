#./mvnw versions:set -DnewVersion=4.4.5
echo '修改版本号'
#./mvnw versions:commit
echo '提交修改'
./mvnw clean deploy -pl emily-parent -am
echo '#########emily-parent...'
./mvnw clean deploy -pl oceansky-json
echo '#########oceansky-json打包完成...'
./mvnw clean deploy -pl oceansky-logger
./mvnw clean deploy -pl oceansky-captcha
./mvnw clean deploy -pl oceansky-language
./mvnw clean deploy -pl oceansky-sensitive
./mvnw clean deploy -pl oceansky-jwt
./mvnw clean deploy -pl oceansky-date
./mvnw clean deploy -pl oceansky-common
echo '#########oceansky-json打包完成...'
#./mvnw clean deploy -pl emily-spring-boot-parent
#echo '#########emily-spring-boot-parent打包完成...'
./mvnw clean deploy -pl emily-spring-boot-logger
echo '#########emily-spring-boot-logger打包完成...'
./mvnw clean deploy -pl emily-spring-boot-core
echo '#########emily-spring-boot-core打包完成...'
./mvnw clean deploy -pl emily-spring-boot-starter
echo '#########emily-spring-boot-starter打包完成...'
./mvnw clean deploy -pl emily-spring-cloud-starter
echo '#########emily-spring-cloud-starter打包完成...'
./mvnw clean deploy -pl emily-spring-boot-datasource
echo '#########emily-spring-boot-datasource打包完成...'
./mvnw clean deploy -pl emily-spring-boot-redis
echo '#########emily-spring-boot-redis打包完成...'
./mvnw clean deploy -pl emily-spring-boot-rabbitmq
echo '#########emily-spring-boot-rabbitmq打包完成...'
./mvnw clean deploy -pl emily-spring
echo '#########emily-spring打包完成...'
./mvnw clean deploy -pl emily-bom
echo '#########emily-bom...'