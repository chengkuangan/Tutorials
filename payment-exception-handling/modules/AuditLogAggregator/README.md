# Audit Log Aggregator Service

Using the Kafka Streams API to provides simulation for audit trails aggregation and cleansing on multiple audit trails entries from different microservices. This module produces consolidated audit trails into Kafka topic. The MongoDB Kafka connector will consume these messages and streams into the `audit-mongodb`.

## To run the apps locally

1. You need to have Apache Kafka running. You can use the devservice RedPanda Kafka service by setting the following in [application.properties](src/main/resources/application.properties)

```properties
%dev.quarkus.kafka.devservices.enabled=true

# Disable the Kafka bootstrap-servers config, this will cause the Redpanda to be deployed together with quarkus.kafka.devservices.enabled is set to true
# %dev.quarkus.kafka-streams.bootstrap-servers=localhost:9093
```

> Note: Redpanda seems not really stable for Kafka streams. If you experience problem, please use the Apache Kafka instead.

2. At the root directory of this project:

```
./mvnw quarkus:dev
```
## To do local test

1. Install the [kcat](https://github.com/edenhill/kcat)

2. Change directory to `src/main/resources/sample` and run the following commands:

```
kcat -t casa.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" casa-audit.json && \
kcat -t casa.core.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" core-audit.json && \
kcat -t exception.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" exception-audit.json && \
kcat -t validation.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" validation-audit.json && \
kcat -t casa.audit.events -P -b localhost:9093 -k "1-20211102-060341780-12345" casa-audit.json && \
kcat -t casa.core.audit.events -P -b localhost:9093 -k "1-20211102-060341780-12345" core-audit.json
```
3. Sample data:
- [casa-audit.json](src/main/resources/sample/casa-audit.json)
- [core-audit.json](src/main/resources/sample/core-audit.json)
- [exception-audit.json](src/main/resources/sample/exception-audit.json)
- [validation-audit.json](src/main/resources/sample/validation-audit.json)