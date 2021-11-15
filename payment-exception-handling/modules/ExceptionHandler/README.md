# Exception Handler

Provides exception handling for exceptions raised by [Credit Check](../LimitCheck) and [Fraud Check](../FraudCheck)

This is implemeted as Kogito workflow service with human task to approve and reject the exceptions.

## Sample input CloudEvents formatetd data

```json
{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "Handle_exceptionMessageDataEvent_9",
  "time": "2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]",
  "data": {
    "id": "1-1231231-123123-12132",
    "recipientAccountNo": "4455151512112",
    "sourceAccountNo": "13669874444",
    "amount": 23123.3,
    "status": "FAILED",
    "responseMessages": "test data"
  }
}
```
