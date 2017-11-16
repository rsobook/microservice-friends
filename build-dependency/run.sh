#!/bin/bash
cd /usr/local/bin/kumuluzee/

########################################
### RUN JAR
########################################
if [[ -z "${JAR}" ]];
then
    printf 'You need to pass jar name.'
    exit 1
else
    printf 'Starting KumuluzEE .. \n'
    java -jar ${JAR}
fi