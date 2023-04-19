./mvnw versions:set -DnewVersion=4.2.0
echo '修改版本号'
./mvnw versions:commit
echo '提交修改'
./mvnw clean install -pl emily-spring-boot-starter -am
echo '#########emily-spring-boot-starter打包完成...'
./mvnw clean install -pl emily-spring-cloud-starter -am
echo '#########emily-spring-cloud-starter打包完成...'
./mvnw clean install -pl emily-spring-boot-datasource -am
echo '#########emily-spring-boot-datasource打包完成...'
./mvnw clean install -pl emily-spring-boot-redis -am
echo '#########emily-spring-boot-redis打包完成...'
./mvnw clean install -pl emily-spring-boot-rabbitmq -am
echo '#########emily-spring-boot-rabbitmq打包完成...'