#! /bin/bash
centerName=$1
date=$2
echo "参数centername为: " ${centerName} " --- 参数date为: " ${date}
java -jar bigdata-flume-monitor-jar-with-dependencies.jar $centerName $date
echo 'flume监控运行结束'