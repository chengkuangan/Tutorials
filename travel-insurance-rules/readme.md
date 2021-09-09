# CI/CD for Red Hat Decision Manager on Openshift

The demo deploys KIE Server and Decision Central on DEV environment. 
Changes at Decision Central will be comitted into Gogs and then invokes the Jenkins job to build the 
kjar. Stores the kjar into Nexus, and then deploy a KIE Server into SIT Environment.

To setup the demo, clone this project to local directory. Navigate to the bin folder, and run the following scripts:

- Provision the required Openshift environments and containers for this demo.

./init.sh

- Initilize the required demo data

./initDemoData.sh 

You may refer to the following article for more explanation

https://developers.redhat.com/blog/2019/07/22/enabling-ci-cd-for-red-hat-decision-manager-on-openshift/

## Note:

1. Change the script to executable if it is not.
2. run ./init.sh -h and ./initDemoData.sh -h for more help on how to use the scritps.


