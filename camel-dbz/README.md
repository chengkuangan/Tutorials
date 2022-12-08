# Apache Camel with Debezium PostgresSQL Connector

This sample project demonstrates capturing new `order` from PostgresSQL database using [Camel Debezium PostgresSQL Connector](https://camel.apache.org/camel-quarkus/2.14.x/reference/extensions/debezium-postgres.html) and perform data enrichment using Camel Quarkus components. The output of the result is saved into a file with JSON formatted data.

The [Simulator](/simulator/README.md) query `custid` from `PostgresSQL`'s `braindose.customer` table randomly and also reads SKU from [sku.csv](/simulator/src/main/resources/sku.csv) file randomly and simulate a new Order creation in the `braindose.order` and `braindose.customerorder` tables.

The `PostgresSQL` is populated with [initdb.sh](/db/initdb.sh) after the `PostgresSQL` database is initiated.

The [Camel](/camel/README.md) project provide the [Camel Debezium PostgresSQL Connector](https://camel.apache.org/camel-quarkus/2.14.x/reference/extensions/debezium-postgres.html) implementation. New commited records are captured from the `braindose.orders` table.

The result of the Camel processing is output at `$OUTPUT_DIR/$OUTPUT_FILENAME`, in this demo the complete path is `/tmp/dbzdemo/dbz-camel-order.output`. Watch the output using `tail`
```
tail -f /tmp/dbzdemo/dbz-camel-order.output
```

## Running the Demo

Run `docker compose` at the project folder to bring up the demo environment.

```
docker compose up --build
```

## References

Find out more explanation on this sample implementation at [Debezium Change Data Capture without Apache Kafka Cluster](https://braindose.blog/2022/12/08/debezium-cdc-connector-camel/)