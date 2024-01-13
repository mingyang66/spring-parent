mvn versions:set -DnewVersion=4.4.1
echo '修改版本号'
mvn versions:commit
echo '提交修改'
mvn clean deploy -pl oceansky-json -am
echo '#########oceansky-json打包完成...'
mvn clean deploy -pl oceansky-logger
mvn clean deploy -pl oceansky-captcha
mvn clean deploy -pl oceansky-language
mvn clean deploy -pl oceansky-sensitive
mvn clean deploy -pl oceansky-jwt
mvn clean deploy -pl oceansky-date
mvn clean deploy -pl oceansky-common
echo '#########oceansky-json打包完成...'
mvn clean deploy -pl emily-spring-boot-parent
echo '#########emily-spring-boot-parent打包完成...'
mvn clean deploy -pl emily-spring-boot-logger
echo '#########emily-spring-boot-logger打包完成...'
mvn clean deploy -pl emily-spring-boot-core
echo '#########emily-spring-boot-core打包完成...'
mvn clean deploy -pl emily-spring-boot-starter
echo '#########emily-spring-boot-starter打包完成...'
mvn clean deploy -pl emily-spring-cloud-starter
echo '#########emily-spring-cloud-starter打包完成...'
mvn clean deploy -pl emily-spring-boot-datasource
echo '#########emily-spring-boot-datasource打包完成...'
mvn clean deploy -pl emily-spring-boot-redis
echo '#########emily-spring-boot-redis打包完成...'
mvn clean deploy -pl emily-spring-boot-rabbitmq
echo '#########emily-spring-boot-rabbitmq打包完成...'