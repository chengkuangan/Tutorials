# LimitCheck 

Provides Kogito DMN implementation to perform credit limit check for a specific transaction from Apache Kafka Streams.

The CloudEvent format input example:

```
{
  "specversion": "1.0",
  "id": "a89b61a2-5644-487a-8a86-144855c5dce8",
  "source": "opay-casa",
  "type": "DecisionRequest",
  "subject": "Check The Credit Limit",
  "kogitodmnmodelname": "checklimit",
  "kogitodmnmodelnamespace": "https://kiegroup.org/dmn/_1D3A94BF-BCD5-470A-B22A-38B9E60ED24E",
  "data": {"CreditDetail":{"amount": 10,"transactionId":"1-1290-123123-000111","dailyLimit": 100,"balance": 1500,"dailyTotalAmount": 95}}
}
```

Example of result message in Kafka topic:

```
{
  "specversion": "1.0",
  "id": "519b30ab-91cf-411e-8752-2c21cba534c8",
  "source": "checklimit",
  "type": "DecisionResponse",
  "subject": "Check The Credit Limit",
  "kogitodmnmodelnamespace": "https://kiegroup.org/dmn/_1D3A94BF-BCD5-470A-B22A-38B9E60ED24E",
  "kogitodmnmodelname": "checklimit",
  "data": {
    "CreditDetail": {
      "amount": 10,
      "balance": 1500,
      "dailyLimit": 100,
      "dailyTotalAmount": 95,
      "transactionId": "1-1290-123123-000111"
    },
    "now": "function now(  )",
    "CheckLimit": {
      "reason": "Exceeded daily transfer limit",
      "transactionId": "1-1290-123123-000111",
      "status": false,
      "timestamp": "2021-10-01 10:56:29.798"
    }
  }
}

```
