#!/bin/bash

sudo systemctl stop tomcat

sudo rm -rf /opt/tomcat/webapps/*

# cleanup log files
sudo rm -rf /opt/tomcat/logs/catalina*
sudo rm -rf /opt/tomcat/logs/*.log
sudo rm -rf /opt/tomcat/logs/*.txt
