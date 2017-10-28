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
    if [ -z "${DB_PORT_5432_TCP_ADDR}" ];
    then
        printf "Env db address (DB_PORT_5432_TCP_ADDR) is not set."
        sed -i "s/DB_HOST_ADDRESS/localhost/g" config.yaml
    else
        sed -i "s/DB_HOST_ADDRESS/${DB_PORT_5432_TCP_ADDR}/g" config.yaml
    fi

    ######################################
    ### SET DB_PORT
    ######################################
    if [ -z "${DB_PORT_5432_TCP_PORT}" ];
    then
        printf "Env db address (DB_PORT_5432_TCP_ADDR) is not set."
        sed -i "s/DB_HOST_PORT/5432/g" config.yaml
    else
        sed -i "s/DB_HOST_PORT/${DB_PORT_5432_TCP_PORT}/g" config.yaml
    fi

    ######################################
    ### SET DB_NAME
    ######################################
    if [ -z "${DB_NAME}" ];
    then
        printf "Env db address (DB_PORT_5432_TCP_ADDR) is not set."
    else
        sed -i "s/DB_NAME/${DB_NAME}/g" config.yaml
    fi

    ######################################
    ### SET DB_USERNAME
    ######################################
    if [ -z "${DB_ENV_POSTGRES_USER}" ];
    then
        printf "Env db username (DB_ENV_POSTGRES_USER) is not set. Using 'postgres'."
        sed -i "s/DB_USERNAME/postgres/g" config.yaml
    else
        sed -i "s/DB_USERNAME/${DB_ENV_POSTGRES_USER}/g" config.yaml
    fi

    ######################################
    ### SET DB_PASSWORD
    ######################################
    if [ -z "${DB_ENV_POSTGRES_PASSWORD}" ];
    then
        printf "Env db password DB_ENV_POSTGRES_PASSWORD is not set. Using 'root'."
        sed -i "s/DB_PASSWORD/root/g" config.yaml
    else
        sed -i "s/DB_PASSWORD/${DB_ENV_POSTGRES_PASSWORD}/g" config.yaml
    fi

    ######################################
    ### OVERWRITE config.yaml IN JAR
    ######################################
    printf 'Replacing config.yaml file in KumuluzEE jar with modified one.\n'
    jar uf ${JAR} config.yaml
fi

######################################
### SET RSOBOOK USER API PORT
######################################
if [[ -z "${API_USER_ENV_PORT}" ]];
then
    printf 'You need to pass user api port.'
    exit 1
else
    API_USER_PORT="${API_USER_ENV_PORT}"
fi


######################################
### SET RSOBOOK USER API HOST ADDRESS
######################################
if [[ -z "${API_USER_PORT_5001_TCP_ADDR}" ]];
then
    printf 'You need to pass user api host address.'
    exit 1
else
    API_USER_HOST="${API_USER_PORT_5001_TCP_ADDR}"
fi



########################################
### RUN JAR
########################################
if [[ -z "${JAR}" ]];
then
    printf 'You need to pass jar name.'
    exit 1
else
    PROP="-Dapi.user.host=${API_USER_HOST}:${API_USER_PORT}"
    printf "Starting KumuluzEE with props ${PROP} \n"
    java "$PROP" -jar ${JAR}
fi