# Audit Log Aggregator Service

Using the Kafka Streams API to provides simulation for audit trails aggregation and cleansing on multiple audit trails entries from different microservices.

## To do local test

```
kcat -t casa.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" casa-audit.json && \
kcat -t casa.core.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" core-audit.json && \
kcat -t exception.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" exception-audit.json && \
kcat -t validation.audit.events -P -b localhost:9093 -k "1-20211102-060341780-38926" validation-audit.json && \
kcat -t casa.audit.events -P -b localhost:9093 -k "1-20211102-060341780-12345" casa-audit.json && \
kcat -t casa.core.audit.events -P -b localhost:9093 -k "1-20211102-060341780-12345" core-audit.json
```
Sample data:
- [casa-audit.json](src/main/resources/sample/casa-audit.json)
- [core-audit.json](src/main/resources/sample/core-audit.json)
- [exception-audit.json](src/main/resources/sample/exception-audit.json)
- [validation-audit.json](src/main/resources/sample/validation-audit.json)