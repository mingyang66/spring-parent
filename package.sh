mvn versions:set -DnewVersion=2.4.7
echo '修改版本号'
mvn versions:commit
echo '提交修改'
mvn clean install -pl emily-spring-boot-starter -am
echo '#########emily-spring-boot-starter打包完成...'
sleep 2
mvn clean install -pl emily-spring-cloud-starter -am
echo '#########emily-spring-cloud-starter打包完成...'
sleep 2
mvn clean install -pl emily-spring-boot-datasource -am
echo '#########emily-spring-boot-datasource打包完成...'