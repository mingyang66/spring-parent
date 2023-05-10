mvn versions:set -DnewVersion=4.3.0.4
echo '修改版本号'
mvn versions:commit
echo '提交修改'
mvn clean deploy -pl emily-spring-boot-common -am
echo '#########emily-spring-boot-common打包完成...'
mvn clean deploy -pl emily-spring-boot-parent
echo '#########emily-spring-boot-parent打包完成...'
mvn clean deploy -pl emily-spring-boot-logback
echo '#########emily-spring-boot-logback打包完成...'
#mvn clean deploy -pl emily-spring-boot-core
echo '#########emily-spring-boot-core打包完成...'
#mvn clean deploy -pl emily-spring-boot-starter
echo '#########emily-spring-boot-starter打包完成...'
#mvn clean deploy -pl emily-spring-cloud-starter
echo '#########emily-spring-cloud-starter打包完成...'
#mvn clean deploy -pl emily-spring-boot-datasource
echo '#########emily-spring-boot-datasource打包完成...'
#mvn clean deploy -pl emily-spring-boot-redis
echo '#########emily-spring-boot-redis打包完成...'
#mvn clean deploy -pl emily-spring-boot-rabbitmq
echo '#########emily-spring-boot-rabbitmq打包完成...'