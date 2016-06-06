#!/bin/bash
service tomcat7 stop
cp corpixmgr.war /var/lib/tomcat6/webapps/
rm -rf /var/lib/tomcat6/webapps/corpixmgr
rm -rf /var/lib/tomcat6/work/Catalina/localhost/
service tomcat7 start
