# Casa Service

Provides simulation for Casa service for demonstration.

Comes with the following features:

- Performs credit transaction requests
- Audit trails

It requires a DB service to store transactional data. Currently it is configured to use PostgreSQL.

Using the [Outbox pattern based on Debezium](https://debezium.io/blog/2019/02/19/reliable-microservices-data-exchange-with-the-outbox-pattern/) to demonstrate mircoservice transactions handling to maintain data integrity among multiple microservices. As part of the demonstration, it also showcase together with other microservice how audit trails can be implemented using Outbox Pattern with Debezium.

