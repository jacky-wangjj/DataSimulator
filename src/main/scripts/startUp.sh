#!/usr/bin/env bash
#set -x

#export JAVA_HOME=/opt/leap-jdk8
#export JRE_HOME=$JAVA_HOME/jre
#export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
java -version

function prompt()
{
    cat << EOF
=============================================
    please enter the correct parameters:
        1.DataSimulator
        2.Server
        3.Client
        4.Topic
        5.ParamProducer
        6.ParamConsumer
==============================================
EOF
}

SERVICE=$1

if [[ "$SERVICE" = "DataSimulator" ]];then
    java -classpath DataSimulator-1.0.0-release.jar simulator.DataSimulator
elif [[ "$SERVICE" = "Server" ]];then
    java -classpath DataSimulator-1.0.0-release.jar network.Server
elif [[ "$SERVICE" = "Client" ]];then
    java -classpath DataSimulator-1.0.0-release.jar network.Client
elif [[ "$SERVICE" = "Topic" ]];then
    java -classpath DataSimulator-1.0.0-release.jar kafka.TopicUtils
elif [[ "$SERVICE" = "ParamProducer" ]];then
    java -classpath DataSimulator-1.0.0-release.jar kafka.ParamProducer
elif [[ "$SERVICE" = "ParamConsumer" ]];then
    java -classpath DataSimulator-1.0.0-release.jar kafka.ParamConsumer
else
    echo -e "\033[31m`prompt`\n\033[0m"
fi