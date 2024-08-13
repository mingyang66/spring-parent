#./mvnw versions:set -DnewVersion=4.4.5
echo '修改版本号'
#./mvnw versions:commit
echo '提交修改'
./mvnw clean deploy -pl emily-project -am
echo '#########emily-parent...'
cd emily-project
../mvnw clean deploy -pl oceansky-json
../mvnw clean deploy -pl oceansky-logger
../mvnw clean deploy -pl oceansky-captcha
../mvnw clean deploy -pl oceansky-language
../mvnw clean deploy -pl oceansky-sensitive
../mvnw clean deploy -pl oceansky-jwt
../mvnw clean deploy -pl oceansky-date
../mvnw clean deploy -pl oceansky-common
cd ../emily-spring-boot-project
../mvnw clean deploy -pl emily-spring-boot-logger
../mvnw clean deploy -pl emily-spring-boot-core
../mvnw clean deploy -pl emily-spring-boot-starter
../mvnw clean deploy -pl emily-spring-cloud-starter
../mvnw clean deploy -pl emily-spring-boot-datasource
../mvnw clean deploy -pl emily-spring-boot-redis
../mvnw clean deploy -pl emily-spring-boot-rabbitmq
../mvnw clean deploy -pl emily-spring
echo '#########打包完成...'