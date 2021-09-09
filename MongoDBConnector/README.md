# MongoDB Kafka Connector

1. Create the necessary Dockerfile as per the following. There is an already created Dockerfile in this project. Please modify the content to reflect the correct information if they are different from the following example.
```
FROM registry.redhat.io/amq7/amq-streams-kafka-24-rhel7
USER root:root
COPY ./mongo-plugins/ /opt/kafka/plugins/
USER kafka:kafka
```

2. Create a folder named `mongo-plugins` under the project root folder.

2. Download the MongoDB Kafka Connect zip file from from 
[MongoDB website](https://www.mongodb.com/kafka-connector). Unzip the zip file and copy the .jar file from the unzip `lib` folder to the `mongo-plugins` folder.

3. Build the docker image from the project root folder using the following command:
```
docker build --tag amq-streams-kafka-connect-24:latest .
```

Please refer to the following for details guide - [How to Create A MongoDB Kafka Connect Container Image for OpenShift?](https://braindose.blog/2020/06/11/how-create-mongodb-kafka-connect-container-openshift/)