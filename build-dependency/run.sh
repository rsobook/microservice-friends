#!/bin/bash
cd /usr/local/bin/kumuluzee/


######################################
### MODIFY config.yaml
######################################
if [ -f config.yaml ]
then
    ######################################
    ### SET DB_HOST_ADDRESS
    ######################################
    if [ -z "${DB_HOST}" ];
    then
        printf "Env db address (DB_HOST) is not set."
        sed -i "s/DB_HOST_ADDRESS/localhost/g" config.yaml
    else
        sed -i "s/DB_HOST_ADDRESS/${DB_HOST}/g" config.yaml
    fi

    ######################################
    ### SET DB_PORT
    ######################################
    if [ -z "${DB_PORT}" ];
    then
        printf "Env db address (DB_PORT) is not set."
        sed -i "s/DB_HOST_PORT/5432/g" config.yaml
    else
        sed -i "s/DB_HOST_PORT/${DB_PORT}/g" config.yaml
    fi

    ######################################
    ### SET DB_NAME
    ######################################
    if [ -z "${DB_NAME}" ];
    then
        printf "Env db address (DB_NAME) is not set."
    else
        sed -i "s/DB_NAME/${DB_NAME}/g" config.yaml
    fi

    ######################################
    ### SET DB_USERNAME
    ######################################
    if [ -z "${DB_USERNAME}" ];
    then
        printf "Env db username (DB_USERNAME) is not set. Using 'postgres'."
        sed -i "s/DB_USERNAME/postgres/g" config.yaml
    else
        sed -i "s/DB_USERNAME/${DB_USERNAME}/g" config.yaml
    fi

    ######################################
    ### SET DB_PASSWORD
    ######################################
    if [ -z "${DB_PASSWORD}" ];
    then
        printf "Env db password DB_PASSWORD is not set. Using 'root'."
        sed -i "s/DB_PASSWORD/root/g" config.yaml
    else
        sed -i "s/DB_PASSWORD/${DB_PASSWORD}/g" config.yaml
    fi

    ######################################
    ### OVERWRITE config.yaml IN JAR
    ######################################
    printf 'Replacing config.yaml file in KumuluzEE jar with modified one.\n'
    jar uf ${JAR} config.yaml
fi

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