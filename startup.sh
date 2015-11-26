#!/usr/bin/env bash
if [ -f ~/tomcat-8.0.29.zip ] ; then
    echo tomcat-8.0.29 prepared
    pid=$(jps | grep Bootstrap | awk -F ' ' '{print $1}')
    kill -9 $pid
    echo tomcat stoped
else
    echo downloading tomcat8.0.29...
    wget http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.29/bin/apache-tomcat-8.0.29.zip
    echo tomcat downloaded
    mv apache-tomcat-8.0.29.zip ~/tomcat-8.0.29.zip
    echo copy finished
    unzip -d ~/tomcat-8.0.29 ~/tomcat-8.0.29.zip
    echo unzip finished
    chmod +x ~/tomcat-8.0.29/apache-tomcat-8.0.29/bin/*.sh
    mkdir ~/tomcat-8.0.29/apache-tomcat-8.0.29/conf/Catalina/
    mkdir ~/tomcat-8.0.29/apache-tomcat-8.0.29/conf/Catalina/localhost/
    echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" > ~/tomcat-8.0.29/apache-tomcat-8.0.29/conf/Catalina/localhost/superword.xml
    echo "<Context antiJARLocking=\"true\" docBase=\"$(pwd)/target/superword-1.0\" path=\"/superword\"/>" >> ~/tomcat-8.0.29/apache-tomcat-8.0.29/conf/Catalina/localhost/superword.xml
fi
mvn package
echo package finished
rm -rf ~/tomcat-8.0.29/apache-tomcat-8.0.29/logs
echo old logs directory deleted
mkdir ~/tomcat-8.0.29/apache-tomcat-8.0.29/logs
echo mkdir logs directory
~/tomcat-8.0.29/apache-tomcat-8.0.29/bin/catalina.sh start
echo superword started
tail -f ~/tomcat-8.0.29/apache-tomcat-8.0.29/logs/*