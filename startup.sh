if [ -f ~/tomcat-8.0.28.zip ] ; then
    echo tomcat-8.0.28 prepared
else
    echo downloading tomcat8.0.21...
    wget http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.28/bin/apache-tomcat-8.0.28.zip
    echo tomcat has been downloaded
    mv apache-tomcat-8.0.28.zip ~/tomcat-8.0.28.zip
    echo copy finished
    unzip -d ~/tomcat-8.0.28 ~/tomcat-8.0.28.zip
    echo unzip finished
fi
mvn clean install
echo clean install finished
rm -rf ~/tomcat-8.0.28/apache-tomcat-8.0.28/webapps/superword-1.0
echo old webapps directory deleted
cp target/superword-1.0.war ~/tomcat-8.0.28/apache-tomcat-8.0.28/webapps/
echo copy war finished
rm -rf ~/tomcat-8.0.28/apache-tomcat-8.0.28/logs
echo old logs directory deleted
mkdir ~/tomcat-8.0.28/apache-tomcat-8.0.28/logs
echo mkdir logs directory
chmod +x ~/tomcat-8.0.28/apache-tomcat-8.0.28/bin/*.sh
~/tomcat-8.0.28/apache-tomcat-8.0.28/bin/catalina.sh start
echo apdplat has been started
tail -f ~/tomcat-8.0.28/apache-tomcat-8.0.28/logs/*