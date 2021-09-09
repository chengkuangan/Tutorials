#!/bin/bash

DOWNLOADURL=""
CONTINUATIONTOKEN=""
REQUEST_URL=""
PACKAGE_PATH=""
CREDENTIAL="admin:admin123"

# curl -u admin:admin123 -X GET 'http://nexus3-poc-tools.apps.ocp.demo.com/service/rest/v1/assets?repository=nuget.org-proxy'

function ContinousDownload(){
    GetContinousToken
    DownloadPackages
}

function DownloadPackages(){
    PrepareURL
    for i in 0 1 2 3 4 5 6 7 8 9
        do
            DOWNLOADURL=$(curl -u $CREDENTIAL -X GET "$REQUEST_URL" | python -c "import sys, json; print json.load(sys.stdin)['items'][$i]['downloadUrl']")
            PACKAGE_PATH=$(curl -u $CREDENTIAL -X GET "$REQUEST_URL" | python -c "import sys, json; print json.load(sys.stdin)['items'][$i]['path']")
            echo
            echo $DOWNLOADURL
            echo
            PACKAGE_PATH="$(sed s/[\/]/./g <<<$PACKAGE_PATH)" 
            echo $PACKAGE_PATH
            curl "$DOWNLOADURL" -o /media/sf_Downloads/PBB\ POC/nuget-packages-new/$PACKAGE_PATH.nupkg
            echo "Downloaded $DOWNLOADURL. File: /media/sf_Downloads/PBB\ POC/nuget-packages-new/$PACKAGE_PATH.nupkg ... "  >> /media/sf_Downloads/PBB\ POC/nuget-packages-new/download.log
        done
    GetContinousToken
    DownloadPackages    
}

function GetContinousToken(){
    echo
    echo "Previous Continuation Token = $CONTINUATIONTOKEN"
    echo
    PrepareURL
    echo "curl -u $CREDENTIAL -X GET \"$REQUEST_URL\" | python -c \"import sys, json; print json.load(sys.stdin)['continuationToken']\""
    CONTINUATIONTOKEN=$(curl -u $CREDENTIAL -X GET "$REQUEST_URL" | python -c "import sys, json; print json.load(sys.stdin)['continuationToken']")    
    echo
    echo "Current Continuation Token = $CONTINUATIONTOKEN"
    echo
}

function PrepareURL(){
    PARAMS=""
    if [[ -n "$CONTINUATIONTOKEN" ]]; then
        PARAMS="&continuationToken=$CONTINUATIONTOKEN"
    fi
    REQUEST_URL="http://nexus3-poc-tools.apps.ocp.demo.com/service/rest/v1/assets?repository=nuget.org-proxy$PARAMS"
    echo "REQUEST_URL = $REQUEST_URL"
}

ContinousDownload