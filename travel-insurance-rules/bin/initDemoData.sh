#!/bin/bash

#================== TO DO ==============================
# 1. create script to update Jenkinsfile in Gogs with updated project name and URLs.
# 2. keytool will prompt for password, how to overcome this?

#================== Global Variables  ==================

PROJECTNAME=""
DECISIONCENTRAL_PROJECTNAME=""
GOGSUSER=""
GOGSPASSWORD=""
GOGSEMAIL=""
USERNAME=""
PASSWORD=""
MASTER_NODE_URL=""
LOGOUT_WHEN_DONE="false"
DOMAIN_NAME=""
JENKINS_USERNAME=""
JENKINS_TOKEN=""
PROJ_NAME_PREFIX="gck-"

#================== Functions ==================

function printCmdUsage(){
    echo
    echo "Command Usage: ./initDemoData.sh -url <OCP Master URL> -u <username> -p <password> -n <project_name> -gu <gogs_username> -gp <gogs_password> -ju <jenkins_username> -jt <jenkins_token> -d <domain_name>"
    echo
    echo "-url                Required. OCP Master URL."
    echo "-u                  Required. OCP Username."
    echo "-p                  Required. OCP Password"
    echo "-n                  Required. Project name where the gogs and jenkins container located"
    echo "-gu                 Required. Gogs username to initialize the repository for the demo. "
    echo "-gp                 Required. Gogs password. "
    echo "-e                  Required. Email id for Gogs user."
    echo "-ju                 Required. Jenkins username. This is in the format of something like this: demouser-admin-edit-view"
    echo "-jt                 Required. Jenkins token."
    echo "-d                  Required. The OCP domain name. e.g. apps.ocp.demo.com" 
    echo "-np                 Optional. Default: $PROJ_NAME_PREFIX. Project name prefix is required to update the Jenkinfiles settings."
    echo "-nc                 Required. Decision Central project name."
    echo "-logout             Optional. Default: $LOGOUT_WHEN_DONE. Set to true to logout oc connection after the completion."
    echo
}

function printUsage(){
    echo
    echo "This command initialize all the required settings and sample data for the demo."
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
    #echo "================================ Additional Manual Steps Required ================================"
    #echo
}

function printVariables(){
    echo
    echo "The following parameters will be used to create the demo:"
    echo "Project Name: $PROJECTNAME"
    echo "Username: $USERNAME"
    echo "Password: *******"
    echo "Master Node URL: $MASTER_NODE_URL"
    echo "Gogs username: $GOGSUSER"
    echo "Gogs password: ******** "
    echo "Gogs user email id: $GOGSEMAIL"
    echo "Jenkins username: $JENKINS_USERNAME"
    echo "Jenkins token: $JENKINS_TOKEN"
    echo "OCP Domain Name: $DOMAIN_NAME"
    echo "Project Name Prefix: $PROJ_NAME_PREFIX"
    echo "Decision Central Project Name: $DECISIONCENTRAL_PROJECTNAME"
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
      elif [ "$1" == "-gp" ]; then
        shift
        GOGSPASSWORD="$1"
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
      elif [ "$1" == "-n" ]; then
        shift
        PROJECTNAME="$1"      
      elif [ "$1" == "-gu" ]; then
        shift
        GOGSUSER="$1"      
      elif [ "$1" == "-ju" ]; then
        shift
        JENKINS_USERNAME="$1"
      elif [ "$1" == "-jt" ]; then
        shift
        JENKINS_TOKEN="$1"  
      elif [ "$1" == "-d" ]; then
        shift
        DOMAIN_NAME="$1"      
      elif [ "$1" == "-np" ]; then
        shift
        PROJ_NAME_PREFIX="$1"    
      elif [ "$1" == "-nd" ]; then
        shift
        NEXUS_URL="$1"
      elif [ "$1" == "-e" ]; then
        shift
        GOGSEMAIL="$1"  
      elif [ "$1" == "-nc" ]; then
        shift
        DECISIONCENTRAL_PROJECTNAME="$1"    
      else
        echo "Unknown argument: $1"
        printCmdUsage
        exit 0
      fi
      shift
    done

    if [ "$MASTER_NODE_URL" = "" ]; then
        echo "Missing -url argument. Master node URL is required."
        exit 0
    elif [ "$USERNAME" = "" ]; then
        echo "Missing -u argument. OCP username is required."
        exit 0    
    elif [ "$PASSWORD" = "" ]; then
        echo "Missing -p argument. OCP password is required."
        exit 0        
    elif [ "$PROJECTNAME" = "" ]; then
        echo "Missing -n argument. Project name is required."
        exit 0     
    elif [ "$DECISIONCENTRAL_PROJECTNAME" = "" ]; then
        echo "Missing -dc argument. Decision Central project name is required."
        exit 0         
    elif [ "$GOGSUSER" = "" ]; then
        echo "Missing -gu argument. Gogs username is required."
        exit 0            
    elif [ "$GOGSPASSWORD" = "" ]; then
        echo "Missing -gp argument. Gogs password is required."
        exit 0        
    elif [ "$GOGSEMAIL" = "" ]; then
        echo "Missing -e argument. Gogs user email id is required."
        exit 0            
    elif [ "$DOMAIN_NAME" = "" ]; then
        echo "Missing -d argument. OCP Domain name is required."
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

#================== Populating Sample Source Codes in Gogs ==================

echo
GOGS_POD_READY="false"
echo
echo "---> Checking if gogs POD is ready..."
echo
GOGS_POD_NAME="$(oc get pods --no-headers -o custom-columns=NAME:.metadata.name -n $PROJECTNAME | grep gogs-[^postgresql] )"
while [ "$GOGS_POD_READY" = "false" ]
do
   sleep 5
   GOGS_POD_READY="$(oc get pod ${GOGS_POD_NAME} -o custom-columns=Ready:status.containerStatuses[0].ready --no-headers -n $PROJECTNAME)"
   echo "POD: $GOGS_POD_NAME, ready: $GOGS_POD_READY ..."
done
echo
echo "---> Cloning sample source code from github: https://github.com/chengkuangan/travel-insurance-rules.git"
echo
git clone --bare https://github.com/chengkuangan/travel-insurance-rules.git &
wait $!
cd travel-insurance-rules.git
GOGS_ROUTE_NAME="$(oc get routes --no-headers  -o custom-columns=Name:metadata.name -n $PROJECTNAME | grep gogs)"
if [ "$GOGS_ROUTE_NAME" != "" ]; then
    GOGS_HOSTNAME="$(oc get route ${GOGS_ROUTE_NAME} --no-headers  -o custom-columns=domain:spec.host  -n $PROJECTNAME)"
    echo
    echo "---> Gogs Hostname: ${GOGS_HOSTNAME}"
    echo
    if [ "$GOGS_HOSTNAME" != "" ]; then
        echo
        echo "---> Creating gogs token ..."
        echo
        curl  -X POST -H "content-type: application/json" -d '{"name":"sample-token","sha1":"8a4fc41b4868aecdd623b10cb1b64a36c6ee51f3"}' 'http://'"${GOGSUSER}"':'"${GOGSPASSWORD}"'@'"${GOGS_HOSTNAME}"'/api/v1/users/${GOGSUSER}/tokens'
        echo
        echo
        echo "---> Creating repository ..."
        echo
        curl -H "Content-Type: application/json" -d '{"name": "travel-insurance-rules", "description": "Travel Insurance Rules Demo", "private": false}' -X POST 'http://'"${GOGSUSER}"':'"${GOGSPASSWORD}"'@'"${GOGS_HOSTNAME}"'/api/v1/user/repos?token=8a4fc41b4868aecdd623b10cb1b64a36c6ee51f3'
        echo
        echo
        echo "---> Push bare codes as mirror to gogs ..."
        echo
        git push --mirror 'http://'"${GOGSUSER}"':'"${GOGSPASSWORD}"'@'"${GOGS_HOSTNAME}"'/'"${GOGSUSER}"'/travel-insurance-rules.git'
        cd ..
        rm -rf travel-insurance-rules.git
        echo
        echo
        echo "---> Clone the git to modify the Jenkinsfile ..."
        echo
        git clone http://${GOGS_HOSTNAME}/${GOGSUSER}/travel-insurance-rules.git 
        cd travel-insurance-rules
        sed -i -e "s/def projectNamePrefix = \"\"/def projectNamePrefix = \"${PROJ_NAME_PREFIX}\"/g" ./Jenkinsfile
        sed -i -e "s/http:\/\/nexus3.demo1-tools.svc.cluster.local/nexus3.${PROJECTNAME}.svc.cluster.local/g" ./Jenkinsfile
        sed -i -e "s/http:\/\/nexus3.demo1-tools.svc.cluster.local/nexus3.${PROJECTNAME}.svc.cluster.local/g" ./openshift-nexus-settings.xml
        echo "---> Push bare codes as mirror to gogs ..."
        git add .
        git commit -m "Updated Jenkinsfile"
        git push 'http://'"${GOGSUSER}"':'"${GOGSPASSWORD}"'@'"${GOGS_HOSTNAME}"'/${GOGSUSER}/travel-insurance-rules.git'
        cd ..
        rm -rf travel-insurance-rules
        echo "---> Generating ssh keys ..."
        echo -e 'y/n' | ssh-keygen -t rsa -C "$GOGSEMAIL" -N "" -f /tmp/id_rsa
        ID_RSA_PUB=$(</tmp/id_rsa.pub)
        curl -v -X POST -H "content-type: application/json" -d "{'title':'rhdm','key':'${ID_RSA_PUB}'}" 'http://'"${GOGSUSER}"':'"${GOGSPASSWORD}"'@'"${GOGS_HOSTNAME}"'/api/v1/admin/users/demo3/keys'
    else
        echo
        echo "---> Gogs hostname is not valid... hostname: ${GOGS_HOSTNAME}"
        echo
    fi 
else
    echo
    echo "---> Gogs route name is not valid... name: ${GOGS_ROUTE_NAME}"
    echo
fi


#================== Patching Decision Central ==================

echo
echo "---> Checking if Decision Central POD is ready..."
echo
DC_POD_NAME="$(oc get pods --no-headers -o custom-columns=NAME:.metadata.name -n $DECISIONCENTRAL_PROJECTNAME | grep rhdmcentr )"

while [ "$DC_POD_READY" = "false" ]
do
   sleep 5
   DC_POD_READY="$(oc get pod ${DC_POD_NAME} -o custom-columns=Ready:status.containerStatuses[0].ready --no-headers -n $DECISIONCENTRAL_PROJECTNAME)"
   echo "POD: $DC_POD_NAME, ready: $DC_POD_READY ..."
done 

echo "---> Populating Decision Central with proper ssh config and keys..."
GOGS_SSH_HOSTNAME="gogs-ssh.$PROJECTNAME.svc.cluster.local"
cp ../templates/config /tmp/config
sed -i -e "s/gogs-ssh.mydm-tools.svc.cluster.local/${GOGS_SSH_HOSTNAME}/g" /tmp/config
sed -i -e "s/User demo2/User ${GOGSUSER}/g" /tmp/config
oc cp /tmp/config $DECISIONCENTRAL_PROJECTNAME/$DC_POD_NAME:/home/jboss/.ssh/
oc exec $DC_POD_NAME chmod 644 /home/jboss/.ssh/config -n $DECISIONCENTRAL_PROJECTNAME
oc cp /tmp/id_rsa $DECISIONCENTRAL_PROJECTNAME/$DC_POD_NAME:/home/jboss/.ssh/
oc exec $DC_POD_NAME chmod 600 /home/jboss/.ssh/id_rsa -n $DECISIONCENTRAL_PROJECTNAME
oc exec $DC_POD_NAME touch /home/jboss/.ssh/known_hosts -n $DECISIONCENTRAL_PROJECTNAME
oc exec $DC_POD_NAME chmod 644 /home/jboss/.ssh/known_hosts -n $DECISIONCENTRAL_PROJECTNAME

oc exec $DC_POD_NAME mkdir /opt/eap/standalone/data/kie/hooks -n $DECISIONCENTRAL_PROJECTNAME
oc cp ../templates/post-commit $DECISIONCENTRAL_PROJECTNAME/$DC_POD_NAME:/opt/eap/standalone/data/kie/hooks/
oc exec $DC_POD_NAME chmod 755 /opt/eap/standalone/data/kie/hooks/post-commit -n $DECISIONCENTRAL_PROJECTNAME

#================== Creating Jobs in Jenkins ==================

echo
echo "---> Downloading the jenkins-cli.jar from the Jenkins Server ..."
echo
curl -k https://jenkins-${PROJECTNAME}.${DOMAIN_NAME}/jnlpJars/jenkins-cli.jar --output /tmp/jenkins-cli.jar
echo
echo "---> Create a working copy of Jenkins Job template xml file ..."
echo
cp ../templates/jenkins-job.xml /tmp/jenkins-job-work.xml
echo
echo "---> Update the jenkins template file with the actual demo environment settings ..."
echo
sed -i -e "s/https:\/\/github.com\/chengkuangan\/travel-insurance-rules.git/http:\/\/gogs:3000\/${GOGSUSER}\/travel-insurance-rules.git/g" /tmp/jenkins-job-work.xml
echo
echo "---> Create Jenkins job definition ..."
echo
java -jar /tmp/jenkins-cli.jar -s https://jenkins-${PROJECTNAME}.${DOMAIN_NAME}/ -noCertificateCheck -auth ${JENKINS_USERNAME}:${JENKINS_TOKEN}  create-job travel-insurance-rules < /tmp/jenkins-job-work.xml

#================== Configure Red Hat Repo in Nexus ==================
# ---- temporary fix. The orinal approach using post create seems does not work anymore.

echo "---> Checking if the Nexus POD is ready ..."
echo
NEXUS_POD_NAME="$(oc get pods --no-headers -o custom-columns=NAME:.metadata.name -n $PROJECTNAME | grep nexus3 )"
while [ "$NEXUS_POD_READY" = "false" ]
do
   sleep 5
   NEXUS_POD_READY="$(oc get pod ${NEXUS_POD_NAME} -o custom-columns=Ready:status.containerStatuses[0].ready --no-headers -n $PROJECTNAME)"
   echo "POD: $NEXUS_POD_NAME, ready: $NEXUS_POD_READY ..."
done
echo
NEXUS_ROUTE_NAME="$(oc get routes --no-headers  -o custom-columns=Name:metadata.name -n $PROJECTNAME | grep nexus3)"
if [ "$NEXUS_ROUTE_NAME" != "" ]; then
    NEXUS_HOSTNAME="$(oc get route ${NEXUS_ROUTE_NAME} --no-headers  -o custom-columns=domain:spec.host  -n $PROJECTNAME)"
    echo
    echo "---> Nexus Hostname: ${NEXUS_HOSTNAME}"
    echo
    if [ "$NEXUS_HOSTNAME" != "" ]; then
        echo
        echo "---> Creating Red Hat Repo in Nexus ..."
        echo
        curl -o /tmp/nexus-functions -s https://raw.githubusercontent.com/chengkuangan/scripts/master/configure_nexus3_repo.sh; source /tmp/nexus-functions admin admin123 http://${NEXUS_HOSTNAME}
        echo
    else
        echo
        echo "---> Nexus hostname is not valid... hostname: ${NEXUS_HOSTNAME}"
        echo
    fi 
else
    echo
    echo "---> Nexus route name is not valid... name: ${NEXUS_ROUTE_NAME}"
    echo
fi
echo

#================== Other Settings ==================

if [ "$LOGOUT_WHEN_DONE" = "true" ]; then
    oc logout
fi

printAdditionalRemarks

echo
echo "==============================================================="
echo "By now, the sample demo data should be ready...."
echo "==============================================================="
echo

######################################################################################################
####################################### It ENDS  here ################################################
######################################################################################################
