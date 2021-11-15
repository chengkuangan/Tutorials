# Casa Service

Based on the JSON data consumed via the REST API interface, this module produce the CASA messages into a Kafka topic for next keen of processing.

It also consumes the response messages from [Exception Handling](../ExceptionHandler) and [Core services](../CoreService), and update the respective response status into it's own database (`casa-postgres`) 

Using the [Outbox pattern based on Debezium](https://debezium.io/blog/2019/02/19/reliable-microservices-data-exchange-with-the-outbox-pattern/) to demonstrate mircoservice transactions handling to maintain data integrity among multiple microservices. As part of the demonstration, it also showcase together with other microservice how audit trails can be implemented using Outbox Pattern with Debezium.

