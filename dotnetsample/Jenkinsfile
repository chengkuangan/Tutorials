#!groovy

// Important
// Remember to ensure that the Project version information is on top of the pom.xml file because
// the getVersionFromPom will attempt to read the version information that it encounter at the
// first occurance.

def DEV_PROJECTNAME = "demo-dev"
def UAT_PROJECTNAME = "demo-uat"
def BUILDCONFIGNAME="sampledotnet"
def IMAGE_NAME="sampledotnet:latest"
def UATIMAGENAME = "sampledotnet:UATReady-1.0.0"

node('dotnet-22') {

  stage('Clone') {
    // sh "git clone http://gogs-pbb-cicd.apps.ocp.demo.com/poc/sampledotnet.git"
    checkout scm
    // checkout($class: 'TeamFoundationServerScm', localPath: 'D:\Build-Code-Scm', projectPath: '$/RootDirectory/SubFolder', serverUrl: '[http://TEST.TEST.com:8080/TEST/TEST)
    // checkout([$class: 'TeamFoundationServerScm', credentialsConfigurer: [$class: 'AutomaticCredentialsConfigurer'], projectPath: '$/ProjectName', serverUrl: 'CollectionURL', useOverwrite: true, useUpdate: true, workspaceName: 'Hudson-${JOB_NAME}'])}}
  }

  stage('Restore') {
    sh "dotnet restore Test.csproj --configfile nuget.config --force --verbosity d"
    // dir('app') { // -- if using git clone, the codes are cloned into <project_folder>/app
        //sh "dotnet restore ../sampledotnet/Test.csproj --configfile ../sampledotnet/nuget.config --force --verbosity d"
    //}
  }

  stage('Publish') {
    sh "dotnet publish Test.csproj --no-restore  -c Release /p:MicrosoftNETPlatformLibrary=Microsoft.NETCore.App"
    // dir('app') { // -- if using git clone, the codes are cloned into <project_folder>/app
        // sh "dotnet publish ../sampledotnet/Test.csproj --no-restore  -c Release /p:MicrosoftNETPlatformLibrary=Microsoft.NETCore.App"
    //}
  }

  stage('Build Image') {
    sh "oc -n $DEV_PROJECTNAME start-build $BUILDCONFIGNAME --from-dir=./bin/Release/netcoreapp2.2/rhel.7-x64/publish --follow"
    sh "oc -n $DEV_PROJECTNAME tag $DEV_PROJECTNAME/$IMAGE_NAME $UAT_PROJECTNAME/$UATIMAGENAME"
  }
  
}


