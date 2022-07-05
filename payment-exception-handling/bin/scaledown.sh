#!/bin/bash

oc scale --replicas=0 deploymentConfigs/audit-log-aggregator -n opay-apps
oc scale --replicas=0 deploymentConfigs/audit-ui -n opay-apps
oc scale --replicas=0 deploymentConfigs/casa-service -n opay-apps
oc scale --replicas=0 deploymentConfigs/checkfraud-service -n opay-apps
oc scale --replicas=0 deploymentConfigs/checklimit-service -n opay-apps
oc scale --replicas=0 deploymentConfigs/core-service -n opay-apps
oc scale --replicas=0 deploymentConfigs/customer-camel-service -n opay-apps
oc scale --replicas=0 deploymentConfigs/customer-profile -n opay-apps
oc scale --replicas=0 deploymentConfigs/exception-camel-service -n opay-apps
oc scale --replicas=0 deploymentConfigs/exception-handler -n opay-apps
oc scale --replicas=0 deploymentConfigs/validation-camel-service -n opay-apps
oc scale --replicas=0 deploymentConfigs/validation-handler -n opay-apps
oc scale --replicas=0 deployments/audit-mongodb -n opay-apps
oc scale --replicas=0 deployments/casa-postgres -n opay-apps
oc scale --replicas=0 deployments/core-postgres -n opay-apps
oc scale --replicas=0 deployments/customer-postgres -n opay-apps
oc scale --replicas=0 deployments/data-index -n opay-kogito
oc scale --replicas=0 deployments/infinispan -n opay-kogito
oc scale --replicas=0 deployments/keycloak -n opay-kogito
oc scale --replicas=0 deployments/management-console -n opay-kogito
oc scale --replicas=0 deployments/task-console -n opay-kogito
oc scale --replicas=0 deployments/opay-connect-cluster-connect -n opay-apps
oc scale --replicas=0 deployments/kafka-cluster-entity-operator -n opay-strimzi
echo "Waiting for 60 seconds ... "
sleep 60
oc scale --replicas=0 statefulsets/kafka-cluster-kafka -n opay-strimzi
oc scale --replicas=0 statefulsets/kafka-cluster-zookeeper -n opay-strimzi
echo "Waiting for 120 seconds ... "
sleep 120
oc scale --replicas=0 deployments/amq-streams-cluster-operator-v2.0.1-1 -n openshift-operators
