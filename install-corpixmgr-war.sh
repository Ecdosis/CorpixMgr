#!/bin/bash
service tomcat7 stop
cp corpixmgr.war /var/lib/tomcat7/webapps/
rm -rf /var/lib/tomcat7/webapps/corpixmgr
rm -rf /var/lib/tomcat7/work/Catalina/localhost/
service tomcat7 start
