#FROM registry.redhat.io/amq7/amq-streams-kafka-23:1.3.0
FROM registry.redhat.io/amq7/amq-streams-kafka-24-rhel7
USER root:root
COPY ./mongo-plugins/ /opt/kafka/plugins/
USER kafka:kafka