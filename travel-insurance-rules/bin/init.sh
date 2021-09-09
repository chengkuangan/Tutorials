#!/bin/bash

#================== TO DO ==============================
# 1. keytool will prompt for password, how to overcome this?
# 2. Configure the git repo for Decision Central


#================== Global Variables  ==================

PROJ_NAME_PREFIX='gck-'
PROJ_TOOLS_NAME="tools"
PROJ_TOOLS_DESC="Tools"
PROJ_DM_DEV_NAME="rhdm-dev"
PROJ_DM_DEV_DESC="RH DM DEV"
PROJ_DM_SIT_NAME="rhdm-sit"
PROJ_DM_SIT_DESC="RH DM SIT"
USERNAME=""
PASSWORD=""
MASTER_NODE_URL=""
LOGOUT_WHEN_DONE="false"
DM_IMAGE_STREAM_TAG="1.1"
KIESERVER_KEYSTORE_PASSWORD="mykeystorepass"
# DCENTER_KEYSTORE_PASSWORD="mykeystorepass"

#================== Functions ==================

function printCmdUsage(){
    echo
    echo "Command Usage: ./init.sh -url <OCP Master URL> -u <username> -p <password>"
    echo
    echo "-url                Required. OCP Master URL."
    echo "-u                  Required. OCP Username."
    echo "-p                  Required. OCP Password"
    echo "-nf                 Optional. Default: $PROJ_NAME_PREFIX. To prefix a project name to create unique project name in shared environment."
    echo "-tn                 Optional. Default: ${PROJ_NAME_PREFIX}$PROJ_TOOLS_NAME. OCP Project name to group CI/CD containers such as gogs, jenkins, etc..."
    echo "-td                 Optional. Default: $PROJ_TOOLS_DESC. OCP Project description for ${PROJ_NAME_PREFIX}$PROJ_TOOLS_NAME"
    echo "-dsn                Optional. Default: ${PROJ_NAME_PREFIX}$PROJ_DM_SIT_NAME. OCP project name for RH Decision Manager in SIT Environment"
    echo "-dsd                Optional. Default: $PROJ_DM_SIT_DESC. OCP Project description for ${PROJ_NAME_PREFIX}$PROJ_DM_SIT_NAME"
    echo "-ddn                Optional. Default: ${PROJ_NAME_PREFIX}$PROJ_DM_DEV_NAME. OCP project name for RH Decision Manager in DEV Environment"
    echo "-ddd                Optional. Default: $PROJ_DM_DEV_DESC. OCP Project description for ${PROJ_NAME_PREFIX}$PROJ_DM_DEV_NAME"
    echo "-iv                 Optional. Defalut: $DM_IMAGE_STREAM_TAG. Decision Manager and Decision Central image stream version to deploy."
    echo "-logout             Optional. Default: $LOGOUT_WHEN_DONE. Set to true to logout oc connection after the completion."
    echo
}

function printUsage(){
    echo
    echo "This command will deploy a working demo for Red Hat Decision Manager, completed with CI/CD process."
    echo
    printCmdUsage
    echo
    printAdditionalRemarks
    echo
    printImportantNoteBeforeExecute
    echo
}

function printImportantNoteBeforeExecute(){
    echo
}

function printAdditionalRemarks(){
    echo
    echo "================================ Post-Deployment Steps ================================"
    echo
    echo "Please perform the following steps to complete the demo setup:"
    echo
    echo "Gogs Sample Source Codes"
    echo "1. After the gogs POD is ready. Access to the console and register a new user."
    echo 
    echo "Jenkins Pre-Req Configuration"
    echo "1. Go to the Jenkins console and login with the username and password used to sign on OpenShift."
    echo "2. On the right upper site of the top menu, click beside the username to bring up the drop-down menu. Choose Configure on "
    echo "   the drop-down menu."
    echo "3. Add a new Token under the API Token section. Keep note of this token, this token will be used through out the setup process. Click Save to proceed." 
    echo 
    echo "Configure and loading demo data into the demo environment."
    echo "1. Run the provided initDemoData.sh to configure Gogs and Jenkins with the username and token created in the previous steps."
    echo "   This script will create all the necessary sample codes and projects in Jenkins and Gogs. It also populate the Nexus with necessary repos."
    echo
}

function printVariables(){
    echo
    echo "The following parameters will be used to create the demo:"
    echo "Project Name Prefix: $PROJ_NAME_PREFIX"
    echo "Tools Project Name: $PROJ_TOOLS_NAME"
    echo "Tools Project Description: $PROJ_TOOLS_DESC"
    echo "RH DM SIT Project Name: $PROJ_DM_SIT_NAME"
    echo "RH DM SIT Project Description: $PROJ_DM_SIT_DESC"
    echo "RH DM DEV Project Name: $PROJ_DM_DEV_NAME"
    echo "RH DM DEV Project Description: $PROJ_DM_DEV_DESC"
    echo "Username: $USERNAME"
    echo "Password: *******"
    echo "Master Node URL: $MASTER_NODE_URL"
    echo "Decision Manager Image Stream version: $DM_IMAGE_STREAM_TAG"
    echo "Logout oc after completion: $LOGOUT_WHEN_DONE"
    echo
}

function processArguments(){

    if [ $# -eq 0 ]; then
        printCmdUsage
        exit 0
    fi

    while (( "$#" )); do
      if [ "$1" == "-h" ]; then
        printUsage
        exit 0
      elif [ "$1" == "-nf" ]; then
        shift
        PROJ_NAME_PREFIX="$1"
      elif [ "$1" == "-u" ]; then
        shift
        USERNAME="$1"
      elif [ "$1" == "-p" ]; then
        shift
        PASSWORD="$1"
      elif [ "$1" == "-url" ]; then
        shift
        MASTER_NODE_URL="$1"
      elif [ "$1" == "-logout" ]; then
        shift
        LOGOUT_WHEN_DONE="$1"    
      elif [ "$1" == "-tn" ]; then
        shift
        PROJ_TOOLS_NAME="$1"      
      elif [ "$1" == "-dsn" ]; then
        shift
        PROJ_DM_SIT_NAME="$1"      
      elif [ "$1" == "-dsd" ]; then
        shift
        PROJ_DM_SIT_DESC="$1"        
      elif [ "$1" == "-ddn" ]; then
        shift
        PROJ_DM_DEV_NAME="$1"      
      elif [ "$1" == "-ddd" ]; then
        shift
        PROJ_DM_DEV_DESC="$1"          
      elif [ "$1" == "-td" ]; then
        shift
        PROJ_TOOLS_DESC="$1"      
      elif [ "$1" == "-iv" ]; then
        shift
        DM_IMAGE_STREAM_TAG="$1"  
      else
        echo "Unknown argument: $1"
        printCmdUsage
        exit 0
      fi
      shift
    done

    PROJ_TOOLS_NAME=${PROJ_NAME_PREFIX}${PROJ_TOOLS_NAME}
    PROJ_DM_DEV_NAME=${PROJ_NAME_PREFIX}${PROJ_DM_DEV_NAME}
    PROJ_DM_SIT_NAME=${PROJ_NAME_PREFIX}${PROJ_DM_SIT_NAME}

    if [ "$MASTER_NODE_URL" = "" ]; then
        echo "Missing -url argument. Master node URL is required."
        exit 0
    elif [ "$USERNAME" = "" ]; then
        echo "Missing -u argument. OCP username is required."
        exit 0    
    elif [ "$PASSWORD" = "" ]; then
        echo "Missing -p argument. OCP password is required."
        exit 0        
    fi

}

######################################################################################################
####################################### It starts from here ##########################################
######################################################################################################

#================== Process Command Line Arguments ==================

processArguments $@
printVariables
printImportantNoteBeforeExecute
echo
echo "Press ENTER (OR Ctrl-C to cancel) to proceed..."
read bc

oc login -u $USERNAME -p $PASSWORD $MASTER_NODE_URL

#================== Check if image streams are available ==================

echo
echo "---> Checking if rhdm73-decisioncentral-openshift:$DM_IMAGE_STREAM_TAG imagestream is available..."
IS_IMAGE_AVAILABLE="$(oc get imagestreamtag -n openshift | grep rhdm73-decisioncentral-openshift:$DM_IMAGE_STREAM_TAG)"
echo
if [ "$IS_IMAGE_AVAILABLE" = "" ]; then
  echo "---> Image 'rhdm73-decisioncentral-openshift:$DM_IMAGE_STREAM_TAG' is not available in the OCP. You can..."
  echo "     Run the following command to find out whether there is another version available. If yes, you may change the version in the rhdm73-autoring.yaml from "
  echo "     version $DM_IMAGE_STREAM_TAG to the available version, by changing this parameter (DM_IMAGE_STREAM_TAG) in this file at the top."
  echo "            oc get imagestreamtag -n openshift | grep rhdm73-decisioncentral-openshift:$DM_IMAGE_STREAM_TAG"
  echo
  echo "     OR you can following the guide here to activate registry service account:"
  echo "     https://access.redhat.com/documentation/en-us/red_hat_decision_manager/7.3/html/deploying_a_red_hat_decision_manager_authoring_or_managed_server_environment_on_red_hat_openshift_container_platform/dm-openshift-prepare-con#imagestreams-file-install-proc "
  exit 0
fi
echo
echo "---> Checking if rhdm73-kieserver-openshift:$DM_IMAGE_STREAM_TAG imagestream is available..."
IS_IMAGE_AVAILABLE="$(oc get imagestreamtag -n openshift | grep rhdm73-kieserver-openshift:$DM_IMAGE_STREAM_TAG)"
echo
if [ "$IS_IMAGE_AVAILABLE" = "" ]; then
  echo "---> Image 'rhdm73-kieserver-openshift:$DM_IMAGE_STREAM_TAG' is not available in the OCP. You can..."
  echo "     Run the following command to find out whether there is another version available. If yes, you may change the version in the rhdm73-autoring.yaml from "
  echo "     version DM_IMAGE_STREAM_TAG to the available version, by changing this parameter (DM_IMAGE_STREAM_TAG) in this file at the top"
  echo "            oc get imagestreamtag -n openshift | grep rhdm73-decisioncentral-openshift:$DM_IMAGE_STREAM_TAG"
  echo
  echo "     OR you can following the guide here to activate registry service account:"
  echo "     https://access.redhat.com/documentation/en-us/red_hat_decision_manager/7.3/html/deploying_a_red_hat_decision_manager_authoring_or_managed_server_environment_on_red_hat_openshift_container_platform/dm-openshift-prepare-con#imagestreams-file-install-proc "
  exit 0
fi 

#================== Delete Projects if Found ==================

#================== Create projects required with neccessary permissions ==================

echo
echo "---> Creating all required projects now..."
echo

oc new-project $PROJ_TOOLS_NAME --display-name="$PROJ_TOOLS_DESC"
oc new-project $PROJ_DM_SIT_NAME --display-name="$PROJ_DM_SIT_DESC"
oc new-project $PROJ_DM_DEV_NAME --display-name="$PROJ_DM_DEV_DESC"
    
#================== Deploy Gogs ==================


echo
echo "---> Provisioning gogs now..."
echo
oc new-app -f https://raw.githubusercontent.com/chengkuangan/templates/master/gogs-persistent-template.yaml -p SKIP_TLS_VERIFY=true -n $PROJ_TOOLS_NAME 


#================== Deploy Nexus3 ==================

echo
echo "---> Provisioning nexus3 now..."
echo
oc new-app -f https://raw.githubusercontent.com/chengkuangan/templates/master/nexus3-persistent-templates.yaml -n $PROJ_TOOLS_NAME


#================== Deploy Jenkins ==================

echo
echo "---> Provisioning Jenkins now..."
echo
oc new-app jenkins-persistent -n $PROJ_TOOLS_NAME

echo
echo "---> Adding all necessary users and system accounts permissions for Jenkins..."
echo

oc create role RoleBindingRbacCreate --verb=create --resource=rolebindings.rbac.authorization.k8s.io -n $PROJ_DM_SIT_NAME
oc create role RoleBindingCreate --verb=create --resource=rolebindings.authorization.openshift.io -n $PROJ_DM_SIT_NAME
oc policy add-role-to-user edit system:serviceaccount:$PROJ_TOOLS_NAME:jenkins -n $PROJ_DM_SIT_NAME
oc adm policy add-role-to-user RoleBindingRbacCreate system:serviceaccount:$PROJ_TOOLS_NAME:jenkins --role-namespace=$PROJ_DM_SIT_NAME -n $PROJ_DM_SIT_NAME
oc adm policy add-role-to-user RoleBindingCreate system:serviceaccount:$PROJ_TOOLS_NAME:jenkins --role-namespace=$PROJ_DM_SIT_NAME -n $PROJ_DM_SIT_NAME


#================== Deploy Decision Central and Decision Servers into Dev Environment ==================

echo
echo "---> Generating keystore.jks ..."
echo
#keytool -genkeypair -alias jboss -keyalg RSA -keystore ./dev_keystore.jks -storepass mykeystorepasss --dname 'CN=demo1,OU=Demo,O=ocp.demo.com,L=KL,S=KL,C=MY'
keytool -genkeypair -alias jboss -keyalg RSA -keystore /tmp/keystore.jks -storepass $KIESERVER_KEYSTORE_PASSWORD --dname 'CN=demo1,OU=Demo,O=ocp.demo.com,L=KL,S=KL,C=MY'
echo
echo "---> Creating kieserver-app-secret..."
echo
#oc create secret generic kieserver-app-secret --from-file=./dev_keystore.jks -n $PROJ_DM_DEV_NAME
#oc create secret generic decisioncentral-app-secret --from-file=./dev_keystore.jks -n $PROJ_DM_DEV_NAME
oc create secret generic kieserver-app-secret --from-file=/tmp/keystore.jks -n $PROJ_DM_DEV_NAME
oc create secret generic decisioncentral-app-secret --from-file=/tmp/keystore.jks -n $PROJ_DM_DEV_NAME
rm -f /tmp/keystore.jks
echo
echo "---> Deploying Decision Central and Decision Server into DEV $PROJ_DM_DEV_NAME..."
echo
oc new-app -f https://raw.githubusercontent.com/chengkuangan/travel-insurance-rules/master/templates/rhdm73-authoring.yaml -p DECISION_CENTRAL_HTTPS_SECRET=decisioncentral-app-secret -p KIE_SERVER_HTTPS_SECRET=kieserver-app-secret -p DECISION_CENTRAL_HTTPS_PASSWORD=$KIESERVER_KEYSTORE_PASSWORD -p KIE_SERVER_HTTPS_PASSWORD=$KIESERVER_KEYSTORE_PASSWORD -p APPLICATION_NAME=dmanager -p IMAGE_STREAM_TAG=$DM_IMAGE_STREAM_TAG -p GIT_HOOKS_DIR=/opt/eap/standalone/data/kie/hooks -n $PROJ_DM_DEV_NAME

# --- Patching Decision Central



#================== Other Settings ==================

if [ "$LOGOUT_WHEN_DONE" = "true" ]; then
    oc logout
fi

printAdditionalRemarks

echo
echo "==============================================================="
echo "If you see this, it means the process is completed successfully."
echo "==============================================================="
echo

######################################################################################################
####################################### It ENDS  here ################################################
######################################################################################################
