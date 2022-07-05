#!/bin/bash

oc scale --replicas=1 deployments/amq-streams-cluster-operator-v2.0.1-1 -n openshift-operators
echo "Waiting for 30 seconds ... "
sleep 30
oc scale --replicas=3 statefulsets/kafka-cluster-zookeeper -n opay-strimzi
oc scale --replicas=3 statefulsets/kafka-cluster-kafka -n opay-strimzi
echo "Waiting for 30 seconds ... "
sleep 30
oc scale --replicas=1 deployments/audit-mongodb -n opay-apps
oc scale --replicas=1 deployments/casa-postgres -n opay-apps
oc scale --replicas=1 deployments/core-postgres -n opay-apps
oc scale --replicas=1 deployments/customer-postgres -n opay-apps
echo "Waiting for 120 seconds ... "
sleep 120
oc scale --replicas=1 deployments/kafka-cluster-entity-operator -n opay-strimzi
oc scale --replicas=1 deployments/opay-connect-cluster-connect -n opay-apps
oc scale --replicas=1 deployments/data-index -n opay-kogito
oc scale --replicas=1 deployments/infinispan -n opay-kogito
oc scale --replicas=1 deployments/keycloak -n opay-kogito
oc scale --replicas=1 deployments/management-console -n opay-kogito
oc scale --replicas=1 deployments/task-console -n opay-kogito
oc scale --replicas=1 deploymentConfigs/audit-log-aggregator -n opay-apps
oc scale --replicas=1 deploymentConfigs/audit-ui -n opay-apps
oc scale --replicas=1 deploymentConfigs/casa-service -n opay-apps
oc scale --replicas=1 deploymentConfigs/checkfraud-service -n opay-apps
oc scale --replicas=1 deploymentConfigs/checklimit-service -n opay-apps
oc scale --replicas=1 deploymentConfigs/core-service -n opay-apps
oc scale --replicas=1 deploymentConfigs/customer-camel-service -n opay-apps
oc scale --replicas=1 deploymentConfigs/customer-profile -n opay-apps
oc scale --replicas=1 deploymentConfigs/exception-camel-service -n opay-apps
oc scale --replicas=1 deploymentConfigs/exception-handler -n opay-apps
oc scale --replicas=1 deploymentConfigs/validation-camel-service -n opay-apps
oc scale --replicas=1 deploymentConfigs/validation-handler -n opay-apps