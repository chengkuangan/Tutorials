# LimitCheck 

Provides Kogito DMN implementation to perform fraud check for a specific transaction from Apache Kafka Streams.

The CloudEvent format input example:

```
{
  "specversion": "1.0",
  "id": "a89b61a2-5644-487a-8a86-144855c5dce8",
  "source": "opay-casa",
  "type": "DecisionRequest",
  "subject": "Check The Transaction Fraud",
  "kogitodmnmodelname": "checkfraud",
  "kogitodmnmodelnamespace": "https://kiegroup.org/dmn/_E3BEAA45-9170-4D05-8F9A-0F80BFD8EDDB",
  "data": {
    "Transaction": {
      "transactionId": "1-2312312-12332-000",
      "amount": 1000,
      "originCountry": "MY",
      "destCountry": "MY",
      "transactionType": "CASA",
      "currency": "MYR"
    }
  }
}
```

Example of result message in Kafka topic:

```
{
  "specversion": "1.0",
  "id": "91847bcc-bbe3-49a5-ba01-6b0d6776d42a",
  "source": "checkfraud",
  "type": "DecisionResponse",
  "subject": "Check The Transaction Fraud",
  "kogitodmnmodelnamespace": "https://kiegroup.org/dmn/_E3BEAA45-9170-4D05-8F9A-0F80BFD8EDDB",
  "kogitodmnmodelname": "checkfraud",
  "data": {
    "Transaction": {
      "transactionType": "CASA",
      "destCountry": "MY",
      "amount": 1000,
      "originCountry": "MY",
      "currency": "MYR",
      "transactionId": "1-2312312-12332-000"
    },
    "now": "function now(  )",
    "CheckFraud": {
      "reason": null,
      "transactionId": "1-2312312-12332-000",
      "status": true,
      "timestamp": "2021-10-01 12:22:10.874"
    }
  }
}

```
