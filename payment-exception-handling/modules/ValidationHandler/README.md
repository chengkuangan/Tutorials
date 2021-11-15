# Audit Log Aggregator Service

Using the Kafka Streams API to provides simulation for audit trails aggregation and cleansing on multiple audit trails entries from different microservices.

## Using this in armv7 like RPI

1. You need to build the rocksdb native library and java for armv7. Follow the following instruction
https://github.com/facebook/rocksdb/blob/main/java/RELEASE.md

2. The source code can be download from 
https://github.com/facebook/rocksdb.git

the current version used by Kafka Streams : https://github.com/facebook/rocksdb/releases/tag/v6.15.5
