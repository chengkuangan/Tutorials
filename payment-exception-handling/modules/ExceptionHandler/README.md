# Exception Handler

## Sample Input Cloud Event Data

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

In one liner:

```json

{"specversion": "0.3", "id": "21627e26-31eb-43e7-8343-92a696fd96b1", "source": "", "type": "Handle_exceptionMessageDataEvent_9", "time": "2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]", "data": {"id": "1-1231231-123123-12132","recipientAccountNo": "4455151512112","sourceAccountNo": "13669874444", "amount" : 23123.30, "status" : "FAILED", "responseMessages": "test data"}}

```

```json
{"specversion":"1.0","id":"1-20211011-022409526-26534","source":"blog.braindose.opay.validation.ValidationHandler","type":"Handle_exceptionMessageDataEvent_8","data": {"id":"1-20211011-022409526-26534","recipientAccountNo":"1-234567-4321-9876","sourceAccountNo":"1-987654-1234-4569","amount":1.00000002556E9,"createdTimestamp":"2021-10-11 02:24:09.527Z","recipientReference":"Payment for lunch","paymentType":"CASA","status":"REJECTED","responseMessages":"Transaction amount exceeded central bank regulated limit, Exceeded daily transfer limit, ","eventSources":"blog.braindose.opay.validation.ValidationHandler","eventTimestamp":"2021-10-11 02:24:24.971Z"}}

{"specversion":"1.0","id":"1-20211011-023815623-49531","source":"blog.braindose.opay.validation.ValidationHandler","type":"Handle_exceptionMessageDataEvent_8","data": {"id":"1-20211011-023815623-49531","recipientAccountNo":"1-234567-4321-9876","sourceAccountNo":"1-987654-1234-4569","amount":1.2111111556E8,"createdTimestamp":"2021-10-11 02:38:15.623Z","recipientReference":"Payment for begs","paymentType":"CASA","status":"REJECTED","responseMessages":"Transaction amount exceeded central bank regulated limit, Exceeded daily transfer limit, ","eventSources":"blog.braindose.opay.validation.ValidationHandler","eventTimestamp":"2021-10-11 02:38:26.495Z"}}
```