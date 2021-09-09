#!/bin/bash

PACKAGE_PATH=""
NEXUS_USER=""
NEXUS_PASSWORD=""
NEXUS_URL=""

#find . -type f -exec curl --user user:pass --ftp-create-dirs -T {} https://PATH_TO_NEXUS/{} \;
#curl -u admin:admin123 -X PUT -v -include -F package=@src/test/resources/SONATYPE.TEST.1.0.nupkg http://localhost:8081/repository/nuget-hosted/

function printCmdUsage(){
    echo
    echo "Command Usage: importPackages2Nexus.sh -url <nexus_server> -u <username> -p <password> -a <packages_path> [options]"
    echo "-h                         Print the help information for this command."
    echo "-url                       Nexus server URL pointing to the repository to import the packages."
    echo "-u                         Username to login to Nexus"
    echo "-p                         Password to login to Nexus"
    echo "-a                         Local packages path"
    echo
    echo "[options]"
    echo
}

function printUsage(){
    echo
    echo "This command read packages from local directory recursively and import into the Nexus server"
    printCmdUsage
    echo
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
      elif [ "$1" == "-url" ]; then
        shift
        NEXUS_URL="$1"
      elif [ "$1" == "-u" ]; then
        shift
        NEXUS_USER="$1"  
      elif [ "$1" == "-p" ]; then
        shift
        NEXUS_PASSWORD="$1"  
      elif [ "$1" == "-a" ]; then
        shift
        PACKAGE_PATH="$1"  
      else
        echo "Unknown argument: $1"
        printCmdUsage
        exit 0
      fi
      shift
    done
}

function uploadPackages(){

    # find . -type f -exec curl -u admin:admin123 -X PUT -v -include -F package=@{} http://nexus3-pbb-cicd.apps.ocp.demo.com/repository/nuget-hosted/ \;
    find $PACKAGE_PATH -type f -exec curl -u $NEXUS_USER:$NEXUS_PASSWORD -X PUT -v -include -F package=@{} $NEXUS_URL \;

}