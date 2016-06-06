#!/bin/bash
if [ ! -d corpixmgr ]; then
  mkdir corpixmgr
  if [ $? -ne 0 ] ; then
    echo "couldn't create corpixmgr directory"
    exit
  fi
fi
if [ ! -d corpixmgr/WEB-INF ]; then
  mkdir corpixmgr/WEB-INF
  if [ $? -ne 0 ] ; then
    echo "couldn't create corpixmgr/WEB-INF directory"
    exit
  fi
fi
if [ ! -d corpixmgr/WEB-INF/lib ]; then
  mkdir corpixmgr/WEB-INF/lib
  if [ $? -ne 0 ] ; then
    echo "couldn't create corpixmgr/WEB-INF/lib directory"
    exit
  fi
fi
rm -f corpixmgr/WEB-INF/lib/*.jar
cp dist/CorpixMgr.jar corpixmgr/WEB-INF/lib/
cp web.xml corpixmgr/WEB-INF/
jar cf corpixmgr.war -C corpixmgr WEB-INF 
echo "NB: you MUST copy the contents of tomcat-bin to \$tomcat_home/bin"
