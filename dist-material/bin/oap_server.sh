#!/usr/bin/env bash
PRG="$0"
PRGDIR=`dirname "$PRG"`
project_name="skywalking-oap"
cd  $PRGDIR
pwd
if [[ $1 == "start" || $1 == "restart" ]]; then
    ID=`ps -ef | grep "${project_name}.jar" | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
        echo $ID
        for id in $ID
        do
        kill -9 $id
        echo "killed $id"
        done
        nohup java  -jar ../oap-libs/${project_name}.jar  --spring.config.location=../oap-libs/application.yml >/dev/null 2>&1 &
        echo "启动成功"

elif [ $1 == "stop" ]; then
    ID=`ps -ef | grep "${project_name}.jar" | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
        echo $ID
        for id in $ID
        do
        kill -9 $id
        echo "killed $id"
        done
        echo "停止成功"

elif [ $1 == "status" ]; then
 ps -ef|grep ${project_name}.jar
else
    echo "请输入参数start|status|stop|restart"
fi
