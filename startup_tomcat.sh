#!/usr/bin/env bash
if [ -f pom.xml ] ; then
    echo superword source code prepared
else
    wget https://github.com/ysc/superword/archive/master.zip
    echo superword source code downloaded
    unzip master.zip
    cd superword-master
    echo unzip finished
fi
if [ -f ~/apache-tomcat-8.0.29.zip ] ; then
    echo tomcat-8.0.29 prepared
    pid=$(jps | grep Bootstrap | awk -F ' ' '{print $1}')
    kill -9 $pid
    echo tomcat stopped
else
    echo downloading tomcat8.0.29...
    wget http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.29/bin/apache-tomcat-8.0.29.zip
    echo tomcat downloaded
    mv apache-tomcat-8.0.29.zip ~/apache-tomcat-8.0.29.zip
    echo copy finished
    unzip -d ~/ ~/apache-tomcat-8.0.29.zip
    echo unzip finished
    chmod +x ~/apache-tomcat-8.0.29/bin/*.sh
    mkdir ~/apache-tomcat-8.0.29/conf/Catalina/
    mkdir ~/apache-tomcat-8.0.29/conf/Catalina/localhost/
    echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" > ~/apache-tomcat-8.0.29/conf/Catalina/localhost/superword.xml
    echo "<Context antiJARLocking=\"true\" docBase=\"$(pwd)/target/superword-1.0\" path=\"/superword\"/>" >> ~/apache-tomcat-8.0.29/conf/Catalina/localhost/superword.xml
    wget http://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
    echo maven downloaded
    mv apache-maven-3.3.9-bin.tar.gz ~/apache-maven-3.3.9.tar.gz
    echo copy finished
    tar -xzvf ~/apache-maven-3.3.9.tar.gz -C ~/
    echo unzip finished
    chmod +x ~/apache-maven-3.3.9/bin/*
fi
~/apache-maven-3.3.9/bin/mvn package
echo package finished
rm -rf ~/apache-tomcat-8.0.29/logs
echo old logs directory deleted
mkdir ~/apache-tomcat-8.0.29/logs
echo mkdir logs directory
~/apache-tomcat-8.0.29/bin/catalina.sh start
echo superword started
tail -f ~/apache-tomcat-8.0.29/logs/*