mvn versions:set -DnewVersion=2.4.1
echo '修改版本号'
mvnw versions:commit
echo '提交修改'
mvn clean install -pl emily-spring-boot-starter -am
echo '打包完成'