# Debezium Outbox Pattern Example

OPAy stands for Open Payment. This is project to demonstrate how to use open source techology to implement microservice based architecture for Payment System


### To Build this project

Install the Common and CommonOutbox into local maven repository:

```
mvn clean install -pl Commmon
```
```
mvn clean install -pl CommmonOutbox
```

Run the following mvn command in the modules directory:

```
mvn clean package
``` 

To build specific project:
```
mvn clean package -pl CasaService
```