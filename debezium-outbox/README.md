# Debezium Outbox Pattern Example

This is project to demonstrate how to implement Outbox Pattern using Debezium


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