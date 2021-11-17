#!/bin/bash

########################################################################################################################
### 
### Installation script for Event-Based Payment Demo
### Contributed By: CK Gan (chgan@redhat.com)
### 
########################################################################################################################

## --- command to encode secret value
# echo -n 'casa' | openssl base64
#CASA_MONGODB_PASSWORD_SECRET=head -c 32 /dev/urandom | base64  # random encoded password

APPS_NAMESPACE="opay-apps"
APPS_PROJECT_DISPLAYNAME="OPay - An Open Payment Platform"
ISTIO_SYSTEM_NAMESPACE="opay-istio-system"
RHSSO_NAMESPACE="opay-rhsso"
KOGITO_NAMESPACE="opay-kogito"
KAFKA_NAMESPACE="opay-strimzi"
TEMPLATES_DIR="../templates/ocp"
CONTAINER_NAME=""
MODULE_PARAMETERS=""
RHSSO_TOKEN=""

LAST_CREATED_POD_NAME=""

KAFKA_CLUSTER_NAME="kafka-cluster"
KAFKA_BOOTSTRAP_SERVER=$KAFKA_CLUSTER_NAME-kafka-bootstrap.$KAFKA_NAMESPACE.svc:9092
KAFKA_BOOTSTRAP_SERVER_TLS=$KAFKA_CLUSTER_NAME-kafka-bootstrap.$KAFKA_NAMESPACE.svc:9093

APP_DOMAIN_NAME=`oc get ingresses.config.openshift.io cluster -o json 2>/dev/null | jq -r .spec.domain` 
[ ! "${APP_DOMAIN_NAME}" ] && APP_DOMAIN_NAME="apps.ocpcluster1.gemsdemolab.com"  # default value if ingresses does not exist


OC_USER=""

SSO_APPNAME="sso"
SSO_ADMIN_USERNAME="admin"
SSO_ADMIN_PASSWORD="password"
RHSSO_URL=${SSO_APPNAME}.${RHSSO_NAMESPACE}.svc:8080
RHSSO_SECURED_URL=${SSO_APPNAME}.${RHSSO_NAMESPACE}.svc:8443
RHSSO_PUBLIC_URL=${SSO_APPNAME}-${RHSSO_NAMESPACE}.${APP_DOMAIN_NAME}
RHSSO_SECURED_PUBLIC_URL=${SSO_APPNAME}-${RHSSO_NAMESPACE}.${APP_DOMAIN_NAME}

PROCEED_INSTALL="no"
MODULE_NAME=""

ERROR_MESSAGES=""



# Command prompt text colours
RED='\033[1;31m'
NC='\033[0m' # No Colour
GREEN='\033[1;32m'
BLUE='\033[1;34m'
PURPLE='\033[1;35m'
YELLOW='\033[1;33m'

############################################################################################################################################
# START: Common Functions
############################################################################################################################################

# ----- read user inputs for installation parameters
INPUT_VALUE=""
function readInput(){
    echo
    printHeader "Please provides the following parameter values. (Enter q to quit)"
    echo
    while [ "$INPUT_VALUE" != "q" ]
    do  
        printf "Namespace [$APPS_NAMESPACE]:"
        read INPUT_VALUE
        if [ "$INPUT_VALUE" != "" ] && [ "$INPUT_VALUE" != "q" ]; then
            APPS_NAMESPACE="$INPUT_VALUE"
        fi
        isQuit
        
        printf "Strimzi Namespace [$KAFKA_NAMESPACE]:"
        read INPUT_VALUE
        if [ "$INPUT_VALUE" != "" ] && [ "$INPUT_VALUE" != "q" ]; then
            KAFKA_NAMESPACE="$INPUT_VALUE"
        fi
        isQuit

        printf "Kogito Namespace [$KOGITO_NAMESPACE]:"
        read INPUT_VALUE
        if [ "$INPUT_VALUE" != "" ] && [ "$INPUT_VALUE" != "q" ]; then
            KOGITO_NAMESPACE="$INPUT_VALUE"
        fi
        isQuit
        
        printf "Apps Domain Name [${APP_DOMAIN_NAME}]:"
        read INPUT_VALUE
        if [ "$INPUT_VALUE" != "" ] && [ "$INPUT_VALUE" != "q" ]; then
            APP_DOMAIN_NAME=$INPUT_VALUE
        fi
        isQuit

        INPUT_VALUE="q"
    done
}

function isQuit(){
    if [ "$INPUT_VALUE" = "q" ]; then
        removeTempDirs
        exit 0
    fi
}


function printCmdUsage(){
    echo 
    echo "This is the Payment Gateway demo installer."
    echo
    echo "Command usage: ./deployDemo.sh <options>"
    echo 
    echo "-h            Show this help."
    echo "-i            Install the demo."
    echo "-m <module>   Install by module. "
    echo "              Possible module name: apps, sampleData, kafkaConnect, routes."
    echo 
}

function printHelp(){
    printCmdUsage
    printHeader "The following is a quick list of the installer requirements:"
    echo
    #echo "    * The required OpenShift projects are created."
    #echo "    * keytool is installed on your system."
    #echo "    * openssl is installed on your system."
    echo "    * jq is installed on your system."
    echo "    * An Openshift user with cluster-admin role."
    echo "    * The following Operators are installed:"
    echo "      - Red Hat AMQ Streams"
    echo
    #printHeader "Refer to the following website for the complete and updated guide ..."
    #echo
    #printLink "https://github.com/chengkuangan/pgwdemo"
    echo
}

function printResult(){
    echo 
    echo "=============================================================================================================="
    echo 
    printTitle "OPAY DEMO INSTALLATION COMPLETED !!!"
    echo
    #echo "Additional Information:"
    #echo
    #echo "keycloak credential: admin/admin"
    #echo
    echo "=============================================================================================================="
    echo
}

function init(){
    
    set echo off
    OC_USER="$(oc whoami)"
    set echo on
    
    if [ $? -ne 0 ] || [ "$OC_USER" = "" ]; then
        echo
        printWarning "Please login to Openshift before proceed ..."
        echo
        exit 0
    fi
    echo
    printHeader "--> Creating temporary directory ../tmp"
    mkdir ../tmp

    createDotEnv

    printHeader "--> Create OpenShift required projects if not already created"

    oc new-project $KOGITO_NAMESPACE
    oc new-project $APPS_NAMESPACE
    #oc new-project $ISTIO_SYSTEM_NAMESPACE
    #oc new-project ${RHSSO_NAMESPACE}
    oc new-project $KAFKA_NAMESPACE


}

function loadDotEnv(){

  if [ ! -f .env ]; then
    echo
    printError ".env file is missing. The existing environmental variables settings are not available. Stop deploying ... "
    echo
  else
    source .env
  fi
  KAFKA_BOOTSTRAP_SERVER=$KAFKA_CLUSTER_NAME-kafka-bootstrap.$KAFKA_NAMESPACE.svc:9092
  KAFKA_BOOTSTRAP_SERVER_TLS=$KAFKA_CLUSTER_NAME-kafka-bootstrap.$KAFKA_NAMESPACE.svc:9093
  APP_DOMAIN_NAME=`oc get ingresses.config.openshift.io cluster -o json 2>/dev/null | jq -r .spec.domain` 
  [ ! "${APP_DOMAIN_NAME}" ] && APP_DOMAIN_NAME="apps.ocpcluster1.gemsdemolab.com"  # default value if ingresses does not exist
  
  RHSSO_URL=${SSO_APPNAME}.${RHSSO_NAMESPACE}.svc:8080
  RHSSO_SECURED_URL=${SSO_APPNAME}.${RHSSO_NAMESPACE}.svc:8443
  RHSSO_PUBLIC_URL=${SSO_APPNAME}-${RHSSO_NAMESPACE}.${APP_DOMAIN_NAME}
  RHSSO_SECURED_PUBLIC_URL=${SSO_APPNAME}-${RHSSO_NAMESPACE}.${APP_DOMAIN_NAME}

}

function createDotEnv(){

  rm -f .env
  touch .env
  
  echo "APPS_NAMESPACE=\"${APPS_NAMESPACE}\"" >> .env
  echo "APPS_PROJECT_DISPLAYNAME=\"${APPS_PROJECT_DISPLAYNAME}\"" >> .env
  echo "KOGITO_NAMESPACE=\"${KOGITO_NAMESPACE}\"" >> .env
  echo "KAFKA_NAMESPACE=\"${KAFKA_NAMESPACE}\"" >> .env
  
}

function printTitle(){
    HEADER=$1
    echo -e "${RED}$HEADER${NC}"
}

function printHeader(){
    HEADER=$1
    echo -e "${YELLOW}$HEADER${NC}"
}

function printLink(){
    LINK=$1
    echo -e "${GREEN}$LINK${NC}"
}

function printCommand(){
    COMMAND=$1
    echo -e "${GREEN}$COMMAND${NC}"
}

function printWarning(){
    WARNING=$1
    echo -e "${RED}$WARNING${NC}"
}

function printError(){
    ERROR=$1
    echo -e "${RED}$ERROR${NC}"
}

function printVariables(){
    echo 
    printHeader "The following is the environmental variables configured ..."
    echo
    echo "APP_DOMAIN_NAME = ${APP_DOMAIN_NAME}"
    echo "APPS_NAMESPACE = $APPS_NAMESPACE"
    echo "APPS_PROJECT_DISPLAYNAME = $APPS_PROJECT_DISPLAYNAME"
    echo "KOGITO_NAMESPACE = ${KOGITO_NAMESPACE}"
    echo "KAFKA_NAMESPACE = $KAFKA_NAMESPACE"
    echo "KAFKA_CLUSTER_NAME = $KAFKA_CLUSTER_NAME"
    echo

    #printWarning "Please increase the limit range configured at OpenShift if there is any. Remove the limit range if possible. Deployment will failed if some of the components cannot request resources more than allowed."
    
    #echo

    #printWarning "Please wait and ensure that all required Operators are ready in the projects before proceed!"

    #echo
}

function preRequisitionCheck(){
    
    echo 
    printHeader "--> Checking on pre-requisitions ..."
    echo
    
    # checking whether jq command tool is installed.
    hash jq
    
    if [ $? -ne 0 ]; then
        echo
        printWarning "You will required jq command line JSON processor ... "
        echo
        echo "Please download and install the command line tool from here ... https://stedolan.github.io/jq/"
        echo
        removeTempDirs
        exit 0
    fi

    oc get sub --all-namespaces -o custom-columns=NAME:.metadata.name | grep 'amq-streams'
    if [ $? -ne 0 ]; then
        echo
        printWarning "Please ensure you have installed the following Operators ... "
        echo
        echo "   AMQ Streams"
        echo
        removeTempDirs
        exit 0
    fi
}

function showConfirmToProceed(){
    echo
    printWarning "Press ENTER (OR Ctrl-C to cancel) to proceed..."
    read bc
}

function processArguments(){

    if [ $# -eq 0 ]; then
        printCmdUsage
        exit 0
    fi

    while (( "$#" )); do
      if [ "$1" == "-h" ]; then
        printHelp
        exit 0
      # Proceed to install
      elif [ "$1" == "-i" ]; then
        PROCEED_INSTALL="yes"
        shift
      elif [ "$1" == "-m" ]; then
        shift
        MODULE_NAME="$1"
        if [ "${MODULE_NAME}" != "apps" ] && [ "${MODULE_NAME}" != "sampleData" ] && [ "${MODULE_NAME}" != "kafkaConnect" ] && [ "${MODULE_NAME}" != "routes" ]; then
          echo
          echo "Unknown module name: ${MODULE_NAME}"
          printCmdUsage
          exit 0
        fi
      else
        echo "Unknown argument: $1"
        printCmdUsage
        exit 0
      fi
      shift
    done
}

# ----- Remove all tmp content after completed.
function removeTempDirs(){
    echo
    printHeader "--> Removing ../tmp directory ... "
    echo
    rm -rf ../tmp
}

#**
# Check if the POD is created
# $1 - namespace
# $2 - app name
###
function waitForPodCreated(){
    
    echo "Waiting for pod to be created ..."

    APPNAME=$2
    NAMESPACE=$1

    echo "NAMESPACE = ${NAMESPACE}, APPNAME = ${APPNAME}"
    
    #LAST_CREATED_POD_NAME="$(oc get pods --no-headers -o custom-columns=NAME:.metadata.name -n $NAMESPACE | grep $APPNAME-.* | grep -v $APPNAME-.*-build | grep -v $APPNAME-.*-deploy | grep -v $APPNAME-.*-.*-.*)"
    LAST_CREATED_POD_NAME=$(getPodName "${APPNAME}" "${NAMESPACE}")
    TRY_COUNT=30

    # oc get pods --no-headers -o custom-columns=NAME:.metadata.name -n rhsso | grep sso.[a-z0-9].[^deploy]
    echo "Waiting for POD to to be created ... POD Name: $LAST_CREATED_POD_NAME"
    while [ "$LAST_CREATED_POD_NAME" = "" ]
        do
            sleep 10
            #LAST_CREATED_POD_NAME="$(oc get pods --no-headers -o custom-columns=NAME:.metadata.name -n $NAMESPACE | grep $APPNAME-.* | grep -v $APPNAME-.*-build | grep -v $APPNAME-.*-deploy | grep -v $APPNAME-.*-.*-.*)"
            LAST_CREATED_POD_NAME=$(getPodName "${APPNAME}" "${NAMESPACE}")
            echo "$TRY_COUNT... Waiting for POD to to be created ... POD Name: $LAST_CREATED_POD_NAME"
            (( TRY_COUNT-- ))
            if [ "$TRY_COUNT" -le 0 ]; then
                printError "Exhausted waiting ... The POD $POD_NAME seems not created after so long..."
                showConfirmToProceed
                break
            fi
        done
}

#**
# Check if the POD is ready
# $1 - namespace
# $2 - pod name
###
function waitForPodReady(){
    echo "Waiting for pod to be ready ..."
    
    if [ -z "$2" ]; then
        POD_NAME=$LAST_CREATED_POD_NAME
    else
        POD_NAME=$2
    fi

    TRY_COUNT=30
    
    NAMESPACE=$1
    #POD_READY="$(oc get pod $POD_NAME -o custom-columns=Ready:status.containerStatuses[0].ready --no-headers -n $NAMESPACE)"
    POD_READY=$(getPodReadiness "$POD_NAME" "$NAMESPACE")
    echo "POD: $POD_NAME, ready: $POD_READY"
    while [ "$POD_READY" != "true" ] 
        do
            sleep 10
            #POD_READY="$(oc get pod $POD_NAME -o custom-columns=Ready:status.containerStatuses[0].ready --no-headers -n $NAMESPACE)"
            POD_READY=$(getPodReadiness "$POD_NAME" "$NAMESPACE")
            echo "$TRY_COUNT... POD: $POD_NAME, ready: $POD_READY"
            (( TRY_COUNT-- ))
            if [ "$TRY_COUNT" -le 0 ]; then
                printError "Exhausted waiting ... The POD $POD_NAME seems got problem to get into READY stage."
                showConfirmToProceed
                break
            fi
        done
    sleep 5
}

################################################
## Check is the POD is ready
## Parameters:
##    $1 - Resource Name (a.k.a container name)
##    $2 - namespaces
## Return: true if the pod is ready. Empty string if not ready
################################################
function getPodReadiness(){
  local _STATUS="$(oc get pod $1 -o custom-columns=Ready:status.containerStatuses[0].ready --no-headers -n $2)"
  echo "${_STATUS}"
}

################################################
## Get the podname for the resource specified
## Parameters:
##    $1 - Resource Name (a.k.a container name)
##    $2 - namespaces
## Return: pod runtime name. Empty string if the pod has not created yet.
################################################
function getPodName(){
  local _POD_NAME="$(oc get pods --no-headers -o custom-columns=NAME:.metadata.name -n $2 | grep $1-.* | grep -v $1-.*-build | grep -v $1-.*-deploy | grep -v $1-.*-.*-.*)"
  echo "${_POD_NAME}"
}

################################################
## Check is the POD is ready
## Parameters:
##    $1 - Resource Name (a.k.a container name)
##    $2 - namespaces
## Return 0 - ready, 1 - not ready
################################################
function isPodReady(){
  #echo "$1 $2"
  _N=$(getPodName $1 $2)
  #echo "_N = $_N"
  if [ "${_N}" == "" ]; then
    #echo "return 1"
    return 1;
  else
    _S=$(getPodReadiness $_N $2)
    #echo "_S = $_S"
    if [ "${_S}" != "true" ]; then
      #echo "return 1"
      return 1;
    else
      #echo "return 0"
      return 0;
    fi 
  fi
}

##
# Import or load sql file into the Postgres 
# Usage:
# populatePSQLData <namespace> <container-name> <sqlfile-path> <podname> <database-name> <user>
##
function populatePSQLData(){
    NS=$1
    CONTAINER=$2
    SQLFILE=$3
    PODNAME=$4
    DB=$5
    USER=$6
   
    oc -n ${NS} -c ${CONTAINER} cp ${SQLFILE} ${PODNAME}:/tmp/import.sql
    oc -n ${NS} exec $PODNAME -c ${CONTAINER}  -- psql -h localhost -d ${DB} -U ${USER} -f /tmp/import.sql
}

function catchAppsDeploymentError(){
    echo
    printError "Error: $1"
    echo
    echo "If the error caused by \"java.io.IOException: Pipe closed\", it is a known issue with Quarkus Openshift plugin."
    echo "Refer the reported issued here: https://github.com/quarkusio/quarkus/issues/16968. It is recommended to use S2I to deploy Quarkus for the moment. "
    echo
    echo "Since this a demo so it is not practical to create git repo for each of the apps module, in order to deploy using S2I approach. Thus, I opt to continue to deploy using the Quarkus plugin. "
    echo "If you receives the above error, please proceed to continue the deployment manually by running the following commands in the sequence given. "
    echo
    echo "1. Deploy the application modules using the following command. Previously successfull installed modules will be skipped. Run this command multiple times until all apps modules are successfully deployed."
    echo
    echo "./deployOpay.sh -m apps"
    echo
    echo "2. Import the sample data"
    echo
    echo "./deployOpay.sh -m sampleData"
    echo
    echo "3. Deploy and configure Kafka connect and connetors"
    echo
    echo "./deployOpay.sh -m kafkaConnect"
    echo
    echo "4. Expose the Openshift routes for the demo apps"
    echo
    echo "./deployOpay.sh -m routes"
    echo
    echo "Other than above, please raise any problem or feedback via the GitHub"
    echo
    exit 1
}


############################################################################################################################################
# END: Common Functions
############################################################################################################################################

############################################################################################################################################
# START: Deployment Functions
############################################################################################################################################

function deployKafka(){
    echo
    printHeader "--> Installing Strimzi ... "
    echo
    mkdir -p ../tmp/kafka
    cp ${TEMPLATES_DIR}/kafka/kafka-persistent.yaml ../tmp/kafka/kafka-persistent.yaml
    sed -i -e "s/opay/$KAFKA_NAMESPACE/" ../tmp/kafka/kafka-persistent.yaml
    echo 
    printHeader "--> Deploying AMQ Streams (Strimzi) Cluster now ... Using ../tmp/kafka/kafka-persistent.yaml"
    oc apply -f ../tmp/kafka/kafka-persistent.yaml -n $KAFKA_NAMESPACE
    echo
}


##
# Deploy application module using Quarkus Openshift extension
# Usage: 
# deployModule <module-name> <namespace>
# 
##
function deployModule(){

    MN=$1
    NS=$2

    echo 
    printHeader "--> Deploying ${MN} ..."
    echo
    
    echo
    echo "Building and deploying ${MN} ... "
    echo
    
    oc project ${NS}

    cd ../tmp/modules/${MN}

    populateContainerAttributes ${MN}
    
    echo
    echo "Container name: ${CONTAINER_NAME} "
    echo

    echo "./mvnw clean package -DskipTests -Dquarkus.container-image.group=${NS} -Dquarkus.kubernetes-client.trust-certs=true -Dquarkus.kubernetes.deploy=true -Dquarkus.openshift.name=${CONTAINER_NAME}  -Dquarkus.openshift.labels.app=${CONTAINER_NAME} -Dquarkus.openshift.labels.app-group=${CONTAINER_NAME} -Dquarkus.kubernetes.namespace=$APPS_NAMESPACE ${MODULE_PARAMETERS}"
    
    ./mvnw clean package -DskipTests \
    -Dquarkus.container-image.group=${NS} \
    -Dquarkus.kubernetes-client.trust-certs=true -Dquarkus.kubernetes.deploy=true \
    -Dquarkus.openshift.name=${CONTAINER_NAME}  \
    -Dquarkus.openshift.labels.app=${CONTAINER_NAME} \
    -Dquarkus.openshift.labels.app-group=${CONTAINER_NAME} \
    -Dquarkus.openshift.namespace=$APPS_NAMESPACE \
     ${MODULE_PARAMETERS}

    #oc patch dc ${CONTAINER_NAME} -p '"spec": {"template": { "metadata": { "annotations": { "sidecar.istio.io/inject": "true" }, "labels": { "version": "v1" } } } }' -n $APPS_NAMESPACE

    #./mvnw clean package -Dquarkus.container-image.build=true \
    #-Dquarkus.kubernetes-client.trust-certs=true -Dquarkus.kubernetes.deploy=true

    cd ../../../bin
}

##
# Populate respective container attributes and parameters
##
function populateContainerAttributes(){
    case "$1" in
      "CasaService")
        CONTAINER_NAME="casa-service"

        MODULE_PARAMETERS=" -Dquarkus.kubernetes-config.enabled=true -Dquarkus.kubernetes-config.secrets.enabled=true -Dquarkus.kubernetes-config.secrets=casa "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.kubernetes-config.config-maps=casa "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.datasource.username=\${postgres.user} -Dquarkus.datasource.password=\${postgres.password} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.datasource.jdbc.url=jdbc:postgresql://\${postgres.server.host}/\${postgres.database}?currentSchema=payment "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.KAFKA_SERVER.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=80 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        
        ;;

      "CoreService")
        CONTAINER_NAME="core-service"
        MODULE_PARAMETERS=" -Dquarkus.kubernetes-config.enabled=true -Dquarkus.kubernetes-config.secrets.enabled=true -Dquarkus.kubernetes-config.secrets=core "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.kubernetes-config.config-maps=core "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.datasource.username=\${postgres.user} -Dquarkus.datasource.password=\${postgres.password} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.datasource.jdbc.url=jdbc:postgresql://\${postgres.server.host}/\${postgres.database}?currentSchema=core "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.KAFKA_SERVER.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "

        ;;

      "CustomerCamelService")
        CONTAINER_NAME="customer-camel-service"
        MODULE_PARAMETERS=" -Dquarkus.openshift.env-vars.CUST_PROFILE_GET_ENDPOINT.value=http://customer-profile.${APPS_NAMESPACE}.svc:8080/cust/acc "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CORE_CASA_GET_ENDPOINT.value=http://core-service.${APPS_NAMESPACE}.svc:8080/core/casa "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "

        ;;

      "CustomerProfile")
        CONTAINER_NAME="customer-profile"

        MODULE_PARAMETERS=" -Dquarkus.kubernetes-config.enabled=true -Dquarkus.kubernetes-config.secrets.enabled=true -Dquarkus.kubernetes-config.secrets=customer "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.kubernetes-config.config-maps=customer "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.datasource.username=\${postgres.user} -Dquarkus.datasource.password=\${postgres.password} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.datasource.jdbc.url=jdbc:postgresql://\${postgres.server.host}/\${postgres.database}?currentSchema=customer "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "

        ;;
      "FraudCheck")
        CONTAINER_NAME="checkfraud-service"
        MODULE_PARAMETERS=" -Dquarkus.openshift.env-vars.KAFKA_SERVER.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        ;;

      "LimitCheck")
        CONTAINER_NAME="checklimit-service"
        MODULE_PARAMETERS=" -Dquarkus.openshift.env-vars.KAFKA_SERVER.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        ;;

      "ValidationCamelService")
        CONTAINER_NAME="validation-camel-service"
        MODULE_PARAMETERS=" -Dquarkus.openshift.env-vars.CASA_NEW_TOPIC.value=casa.new.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CHECK_LIMIT_TOPIC.value=opay.checklimit "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CHECK_FRAUD_TOPIC.value=opay.checkfraud "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CUST_CAMEL_SERVICE.value=http://customer-camel-service.${APPS_NAMESPACE}.svc:8080/cust "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.KAFKA_BOOTSTRAP_SERVERS.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        ;;

      "ExceptionCamelService")
        CONTAINER_NAME="exception-camel-service"
        MODULE_PARAMETERS=" -Dquarkus.openshift.env-vars.CASA_NEW_TOPIC.value=casa.new.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CASA_NEW_EVENTS.value=casa.new.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.EXCEPTION_RESPONSE_EVENTS.value=exception.response.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.EXCEPTION_AUDIT_EVENTS.value=exception.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CASA_RESPONSE_EVENTS.value=casa.response.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CUST_CAMEL_SERVICE.value=http://customer-camel-service.${APPS_NAMESPACE}.svc:8080/cust "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.KAFKA_BOOTSTRAP_SERVERS.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        ;;
      
      "ExceptionHandler")
        CONTAINER_NAME="exception-handler"
        MODULE_PARAMETERS=" -Dmp.messaging.incoming.exceptions.topic=opay.exception.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dkogito.service.url=http://exception-handler.${APPS_NAMESPACE}.svc:8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.KAFKA_SERVER.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=80 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.DATA_INDEX_HOST.value=data-index.${KOGITO_NAMESPACE}.svc:8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.INFINISPAN_HOST.value=infinispan.${KOGITO_NAMESPACE}.svc:11222 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        ;;

      "ValidationHandler")
        CONTAINER_NAME="validation-handler"
        MODULE_PARAMETERS=" -Dquarkus.kafka-streams.application-server=localhost:8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.BOOTSTRAP_SERVERS.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CASA_NEW_EVENTS.value=casa.new.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.OPAY_CHECKLIMIT_RESPONSE.value=opay.checklimit.response "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.OPAY_CHECKFRAUD_RESPONSE.value=opay.checkfraud.response "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CASA_EVENTS.value=casa.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CASA_RESPONSE_EVENTS.value=casa.response.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.OPAY_EXCEPTION_EVENTS.value=opay.exception.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.VALIDATION_AUDIT_EVENTS.value=validation.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        
        ;;

        "AuditLogAggregator")
        CONTAINER_NAME="audit-log-aggregator"
        MODULE_PARAMETERS=" -Dquarkus.kafka-streams.application-server=localhost:8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.BOOTSTRAP_SERVERS.value=${KAFKA_BOOTSTRAP_SERVER} "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.KAFKA_STREAMS_TOPICS.value=casa.audit.events,casa.core.audit.events,exception.audit.events,validation.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.APPLICATION_SERVER.value=localhost:8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CASA_AUDIT_TOPIC.value=casa.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.CASA_CORE_AUDIT_TOPIC.value=casa.core.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.PAYMENT_AUDIT_TOPIC.value=payment.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.EXCEPTION_AUDIT_EVENTS.value=exception.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.env-vars.VALIDATION_AUDIT_EVENTS.value=validation.audit.events "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        
        ;;

        "AuditUI")
        CONTAINER_NAME="audit-ui"
        MODULE_PARAMETERS=" -Dquarkus.openshift.env-vars.MONGODB_CONNETION_STRING.value=mongodb://audit:audit@audit-mongodb:27017 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.container-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.host-port=8080 "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.openshift.ports.http.protocol=tcp "
        MODULE_PARAMETERS="${MODULE_PARAMETERS} -Dquarkus.log.category.\"blog.braindose\".level=DEBUG "
        
        ;;

      *)
        CONTAINER_NAME=""
        ;;
    esac
}

function getContainerName(){
  local _CONTAINERNAME=""
    case "$1" in
      "CasaService")
        _CONTAINERNAME="casa-service"
        ;;

      "CoreService")
        _CONTAINERNAME="core-service"
        ;;

      "CustomerCamelService")
        _CONTAINERNAME="customer-camel-service"
        ;;

      "CustomerProfile")
        _CONTAINERNAME="customer-profile"
        ;;
      "FraudCheck")
        _CONTAINERNAME="checkfraud-service"
        ;;

      "LimitCheck")
        _CONTAINERNAME="checklimit-service"
        ;;

      "ValidationCamelService")
        _CONTAINERNAME="validation-camel-service"
        ;;

      "ExceptionCamelService")
        _CONTAINERNAME="exception-camel-service"
        ;;
      
      "ExceptionHandler")
        _CONTAINERNAME="exception-handler"
        ;;

      "ValidationHandler")
        _CONTAINERNAME="validation-handler"
        ;;

      "AuditLogAggregator")
        _CONTAINERNAME="audit-log-aggregator"
        ;;

      "AuditUI")
        _CONTAINERNAME="audit-ui"
        ;;
      *)
        _CONTAINERNAME=""
        ;;
    esac
    echo "${_CONTAINERNAME}"
}



##
# Deploy postgres database using template.
# Usage:
# deployPostgres <app-name> <database-name> <database-user> <database-password> <namespace>
# 
##
function deployPostgres(){

    NAME="$1-postgres"

    echo 
    printHeader "--> Deploying ${NAME} Service ..."
    echo
    
    USERNAME=`echo -n $3 | openssl base64`
    PASSWORD=`echo -n $4 | openssl base64`

    echo "USERNAME = ${USERNAME}, PASSWORD = ${PASSWORD} ... "

    oc new-app -f ${TEMPLATES_DIR}/db/postgresql-templates.yaml \
    -p APPLICATION_NAME=$1 \
    -p POSTGRES_DATABASE=$2 \
    -p POSTGRES_USER=${USERNAME} \
    -p POSTGRES_PASSWORD=${PASSWORD} \
    -n $5

}

function deployMongoDB(){

    NAME="$1-mongo"

    echo 
    printHeader "--> Deploying ${NAME} Service ..."
    echo
    
    USERNAME=`echo -n $3 | openssl base64`
    PASSWORD=`echo -n $4 | openssl base64`

    echo "USERNAME = ${USERNAME}, PASSWORD = ${PASSWORD} ... "

    oc new-app -f ${TEMPLATES_DIR}/db/mongodb-templates.yaml \
    -p APPLICATION_NAME=$1 \
    -p MONGODB_DATABASE=$2 \
    -p MONGODB_USER=${USERNAME} \
    -p MONGODB_PASSWORD=${PASSWORD} \
    -n $5

}

function deployKafkaConnect(){
    echo
    printHeader "--> Deploy Kafka Connect ..."
    echo
    
    mkdir -p ../tmp/kconnect
    
    echo "Creating Secret for Strimzi CA Cert ..."
    cp ${TEMPLATES_DIR}/kafkaconnect/strimzi-cert-secret.yaml ../tmp/kconnect/strimzi-cert-secret.yaml
    STRIMZI_CACERT=$(oc get secret kafka-cluster-cluster-ca-cert -o "jsonpath={.data['ca\.crt']}" -n $KAFKA_NAMESPACE)
    sed -i -e "s/STRIMZI_CA_CRT/$STRIMZI_CACERT/" ../tmp/kconnect/strimzi-cert-secret.yaml
    oc apply -f ../tmp/kconnect/strimzi-cert-secret.yaml -n $APPS_NAMESPACE

    echo "Deploying Kafka Connect container ..."
    cp ${TEMPLATES_DIR}/kafkaconnect/kafka-connect.yaml ../tmp/kconnect/kafka-connect.yaml
    # Kafka connect must use TLS connection to Kafka server
    sed -i -e "s/kafka-cluster-kafka-bootstrap:9093/$KAFKA_BOOTSTRAP_SERVER_TLS/" ../tmp/kconnect/kafka-connect.yaml
    oc apply -f ../tmp/kconnect/kafka-connect.yaml -n $APPS_NAMESPACE

}

function registerKafkaConnectors(){
    echo
    printHeader "--> Register Kafka Connectors ... "
    echo
    waitForPodCreated ${APPS_NAMESPACE} opay-connect-cluster-connect
    waitForPodReady ${APPS_NAMESPACE}
    oc cp ../config/kafka-connector/register-debezium-casa-postgres.json ${LAST_CREATED_POD_NAME}:/tmp/ -n $APPS_NAMESPACE
    oc cp ../config/kafka-connector/register-debezium-core-response-postgres.json ${LAST_CREATED_POD_NAME}:/tmp/ -n $APPS_NAMESPACE
    oc cp ../config/kafka-connector/register-payment-audit-mongodb.json ${LAST_CREATED_POD_NAME}:/tmp/ -n $APPS_NAMESPACE
    echo
    echo "--> Register Debezium Connector for Casa Postgres ... "
    echo 
    MESSAGE=$(oc -n ${APPS_NAMESPACE} exec ${LAST_CREATED_POD_NAME} -- curl -d @/tmp/register-debezium-casa-postgres.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors)
    if [[ $MESSAGE == *"error_code"* ]] ; then
        echo
        printError "Error configuring Kafka Connect. Please check the log for more details. Please verify the content is correct in config/kafka-connector/register-debezium-casa-postgres.json"
    fi
    echo
    echo "--> Register Debezium Connector for Core Postgres ... "
    echo 
    MESSAGE=$(oc -n ${APPS_NAMESPACE} exec ${LAST_CREATED_POD_NAME} -- curl -d @/tmp/register-debezium-core-response-postgres.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors)
    if [[ $MESSAGE == *"error_code"* ]] ; then
        echo
        printError "Error configuring Kafka Connect. Please check the log for more details. Please verify the content is correct in config/kafka-connector/register-debezium-core-response-postgres.json"
    fi
    echo

    echo
    echo "--> Register Kafka Connector for Mongodb ... "
    echo 
    MESSAGE=$(oc -n ${APPS_NAMESPACE} exec ${LAST_CREATED_POD_NAME} -- curl -d @/tmp/register-payment-audit-mongodb.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors)
    if [[ $MESSAGE == *"error_code"* ]] ; then
        echo
        printError "Error configuring Kafka Connect. Please check the log for more details. Please verify the content is correct in config/kafka-connector/register-debezium-core-response-postgres.json"
    fi
    echo
}

function deployKogitoCoreServices(){

    echo
    printHeader "--> Deploying Kogito core services ... "
    echo

    echo
    echo "--> Deploying infinispan ... "
    echo

    oc create -f ${TEMPLATES_DIR}/kogito/infinispan.yaml -n ${KOGITO_NAMESPACE}

    echo
    echo "--> Deploying data-index ... "
    echo

    mkdir -p ../tmp/kogito

    cp ${TEMPLATES_DIR}/kogito/data-index.yaml ../tmp/kogito/data-index.yaml

    sed -i -e "s/infinispan:11222/infinispan.${KOGITO_NAMESPACE}.svc:11222/" ../tmp/kogito/data-index.yaml
    sed -i -e "s/kafka:9092/${KAFKA_BOOTSTRAP_SERVER}/" ../tmp/kogito/data-index.yaml

    oc create -f ../tmp/kogito/data-index.yaml -n ${KOGITO_NAMESPACE}

    oc expose svc data-index -n ${KOGITO_NAMESPACE}
    
    cd ../tmp/modules

    mvn clean package -pl ExceptionHandler

    cd ../../bin

    waitForPodCreated ${KOGITO_NAMESPACE} data-index
    waitForPodReady ${KOGITO_NAMESPACE}

    echo
    echo "--> Copying ../tmp/modules/ExceptionHandler/target/classes/META-INF/resources/persistence/protobuf/*.proto to  ${LAST_CREATED_POD_NAME}:/home/kogito/data/protobufs/ "
    echo

    oc cp ../tmp/modules/ExceptionHandler/target/classes/META-INF/resources/persistence/protobuf/*.proto ${LAST_CREATED_POD_NAME}:/home/kogito/data/protobufs/ -n ${KOGITO_NAMESPACE}

    echo
    echo "--> Deploying Kogito Keycloak ... "
    echo
    
    cp ${TEMPLATES_DIR}/kogito/keycloak.yaml ../tmp/kogito/keycloak.yaml

    sed -i -e "s/localhost:8380/task-console-${KOGITO_NAMESPACE}.${APP_DOMAIN_NAME}/g" ../tmp/kogito/keycloak.yaml
    sed -i -e "s/localhost:8280/management-console-${KOGITO_NAMESPACE}.${APP_DOMAIN_NAME}/g" ../tmp/kogito/keycloak.yaml

    oc create -f ../tmp/kogito/keycloak.yaml -n ${KOGITO_NAMESPACE}

    oc expose svc keycloak -n ${KOGITO_NAMESPACE}

    echo
    echo "--> Deploying Kogito Management Console ... "
    echo

    cp ${TEMPLATES_DIR}/kogito/management-console.yaml ../tmp/kogito/management-console.yaml

    sed -i -e "s/dataindex:8080/data-index-${KOGITO_NAMESPACE}.${APP_DOMAIN_NAME}/" ../tmp/kogito/management-console.yaml
    sed -i -e "s/keycloak:8080/keycloak-${KOGITO_NAMESPACE}.${APP_DOMAIN_NAME}/g" ../tmp/kogito/management-console.yaml

    oc create -f ../tmp/kogito/management-console.yaml -n ${KOGITO_NAMESPACE}
    
    waitForPodCreated ${KOGITO_NAMESPACE} management-console
    waitForPodReady ${KOGITO_NAMESPACE}

    echo
    echo "--> Copying ./modules/ExceptionHandler/target/classes/META-INF/processSVG/*.svg to  ${LAST_CREATED_POD_NAME}:/home/kogito/data/svg/ "
    echo
    
    oc cp ../tmp/modules/ExceptionHandler/target/classes/META-INF/processSVG/*.svg ${LAST_CREATED_POD_NAME}:/home/kogito/data/svg/ -n ${KOGITO_NAMESPACE}

    oc expose svc management-console -n ${KOGITO_NAMESPACE}

    echo
    echo "--> Deploying Task Console ... "
    echo

    cp ${TEMPLATES_DIR}/kogito/task-console.yaml ../tmp/kogito/task-console.yaml

    sed -i -e "s/dataindex:8080/data-index-${KOGITO_NAMESPACE}.${APP_DOMAIN_NAME}/" ../tmp/kogito/task-console.yaml
    sed -i -e "s/keycloak:8080/keycloak-${KOGITO_NAMESPACE}.${APP_DOMAIN_NAME}/g" ../tmp/kogito/task-console.yaml

    oc create -f ../tmp/kogito/task-console.yaml -n ${KOGITO_NAMESPACE}
    
    oc expose svc task-console -n ${KOGITO_NAMESPACE}
}

function deployAllModules(){
    
    echo
    printHeader "--> Deploying application modules ... "
    echo

    CN=$(getContainerName "CoreService")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule CoreService ${APPS_NAMESPACE} # || catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "CustomerProfile")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule CustomerProfile ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "CustomerCamelService")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule CustomerCamelService ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "CasaService")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule CasaService ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "FraudCheck")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule FraudCheck ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "LimitCheck")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule LimitCheck ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "ValidationCamelService")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule ValidationCamelService ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "ValidationHandler")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule ValidationHandler ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "ExceptionCamelService")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule ExceptionCamelService ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "ExceptionHandler")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule ExceptionHandler ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "AuditLogAggregator")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule AuditLogAggregator ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"
    
    CN=$(getContainerName "AuditUI")
    isPodReady "$CN" "${APPS_NAMESPACE}" || deployModule AuditUI ${APPS_NAMESPACE} #|| catchAppsDeploymentError "Apps deployment failed!"

    #deployModule CoreService ${APPS_NAMESPACE} 
    #deployModule CustomerProfile ${APPS_NAMESPACE}
    #deployModule CustomerCamelService ${APPS_NAMESPACE}
    #deployModule CasaService ${APPS_NAMESPACE}
    #deployModule FraudCheck ${APPS_NAMESPACE}
    #deployModule LimitCheck ${APPS_NAMESPACE}
    #deployModule ValidationCamelService ${APPS_NAMESPACE}
    #deployModule ValidationHandler ${APPS_NAMESPACE}
    #deployModule ExceptionCamelService ${APPS_NAMESPACE}
    #deployModule AuditLogAggregator ${APPS_NAMESPACE}
    #deployKogitoCoreServices
    #deployModule ExceptionHandler ${APPS_NAMESPACE}
    #deployModule AuditLogAggregator ${APPS_NAMESPACE}
    #deployModule AuditUI ${APPS_NAMESPACE}

}

function deloyDatabases(){
  
  echo
  printHeader "--> Deploying databases ... "
  echo

  ## --- Deploy postgres databases
  ## usage: deployPostgres <app-name> <database-name> <database-user> <database-password> <namespace>
  deployPostgres core core core core ${APPS_NAMESPACE}
  deployPostgres customer customer customer customer ${APPS_NAMESPACE}
  deployPostgres casa casa casa casa ${APPS_NAMESPACE}
  deployMongoDB audit audit audit audit ${APPS_NAMESPACE}
  
  waitForPodCreated ${APPS_NAMESPACE} audit-mongodb
  waitForPodReady ${APPS_NAMESPACE}

  oc -n ${APPS_NAMESPACE} exec ${LAST_CREATED_POD_NAME} -it -- mongo --host 127.0.0.1 --port 27017 --quiet admin --eval "db = db.getSiblingDB('audit'); db.collection.createIndex({'id': 1}, { unique: true })"

}

function populateDatabases(){
  
  echo
  printHeader "--> Importing sample data ... "
  echo

  # Usage: populatePSQLData <namespace> <container-name> <sqlfile-path> <podname> <database-name> <user>
  waitForPodCreated ${APPS_NAMESPACE} core-postgres
  populatePSQLData $APPS_NAMESPACE core-postgres ../modules/CoreService/src/main/resources/import.sql ${LAST_CREATED_POD_NAME} core core

  waitForPodCreated ${APPS_NAMESPACE} customer-postgres
  populatePSQLData $APPS_NAMESPACE customer-postgres ../modules/CustomerProfile/src/main/resources/import.sql ${LAST_CREATED_POD_NAME} customer customer

}

function exposeRoutes(){
  
  echo
  printHeader "--> Exposing routes ... "
  echo

  #oc expose svc casa-service --path='/casa/all' -n ${APPS_NAMESPACE} 
  oc expose svc casa-service -n ${APPS_NAMESPACE} 
  oc expose svc exception-handler -n ${APPS_NAMESPACE}
  oc expose svc audit-ui -n ${APPS_NAMESPACE}

}

function customDeployment(){
  set -e
  if [ "${MODULE_NAME}" == "apps" ]; then
    echo
    printHeader "This will deploy application modules only."
    echo
    loadDotEnv
    printVariables
    showConfirmToProceed
    compileCommon
    deployAllModules
    exit 0
  fi

  if [ "${MODULE_NAME}" == "sampleData" ]; then
    echo
    printHeader "This will import sample data into the databases."
    echo
    loadDotEnv
    printVariables
    showConfirmToProceed
    populateDatabases
    exit 0
  fi

  if [ "${MODULE_NAME}" == "kafkaConnect" ]; then
    echo
    printHeader "This will deploy Kafka connect and configure the connetors."
    echo
    loadDotEnv
    printVariables
    showConfirmToProceed
    deployKafkaConnect
    registerKafkaConnectors
    exit 0
  fi

  if [ "${MODULE_NAME}" == "routes" ]; then
    echo
    printHeader "This will expose the Openshift routes for the demo apps."
    echo
    loadDotEnv
    printVariables
    showConfirmToProceed
    exposeRoutes
    exit 0
  fi
  set +e
}

function compileCommon(){

    echo
    printHeader "--> Compiling and Installing common modules ..."
    echo

    ## --- Copy over modules source codes into tmp directory
    rm -rf ../tmp/modules/*
    mkdir -p ../tmp/modules/
    cp -r  ../modules/* ../tmp/modules/

    ## --- Build and install the common and commonoutbox dependencies
    cd ../tmp/modules/
    mvn clean install -pl Common && mvn clean install -pl CommonOutbox
    cd ../../bin
}


############################################################################################################################################
# END: Deployment Functions
############################################################################################################################################


############################################################################################################################################
### The process START here ...
############################################################################################################################################
PROCESS_START=$SECONDS

processArguments $@

# proceed with custom deployment if any
customDeployment

readInput
preRequisitionCheck
printVariables

if [ "$PROCEED_INSTALL" != "yes" ]; then
  removeTempDirs
  exit 0
fi

showConfirmToProceed

## --- initialing environment
init

set -e

## --- deploy kafka
deployKafka

## --- Deploy databases
deloyDatabases

## --- Copy source directory and compile common modules
compileCommon

## --- Deploy Kogito Core Services
deployKogitoCoreServices

## --- adhoc to make sure the topic is created before the AuditLogAggregator is running.
oc -n ${KAFKA_NAMESPACE} exec kafka-cluster-kafka-0 -it -- /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic exception.audit.events &

## --- Deploy all apps modules
deployAllModules

## --- populate databases 
populateDatabases

## ----- Kafka Connect and Connectors
deployKafkaConnect
registerKafkaConnectors

## ----- Expose route
exposeRoutes

#removeTempDirs
printResult

PROCESS_DURATION=$(( SECONDS - PROCESS_START ))
echo
printTitle "TOTAL time took to deploy: $(($PROCESS_DURATION/60)) minutes"
echo

############################################################################################################################################
### The process END here ...
############################################################################################################################################
