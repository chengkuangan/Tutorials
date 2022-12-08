# Camel with Debezium PostgresSQL Connector Demo

Demonstrates capturing new order from PostgresSQL database using [Camel Debezium PostgresSQL Connector](https://camel.apache.org/camel-quarkus/2.14.x/reference/extensions/debezium-postgres.html) and perform data enrichment using Camel components. Output the result into a file with JSON formatted data.

The [simulator](/simulator/README.md) query `custid` from `PostgresSQL`'s `braindose.customer` table randomly and also reads SKU from [sku.csv](/simulator/src/main/resources/sku.csv) file randomly and simulate a new Order creation in the `braindose.order` and `braindose.customerorder` tables.

The `PostgresSQL` is populated with [initdb.sh](/db/initdb.sh) after the `PostgresSQL` database is initiated.

The [camel](/camel/README.md) project provide the [Camel Debezium PostgresSQL Connector](https://camel.apache.org/camel-quarkus/2.14.x/reference/extensions/debezium-postgres.html) implementation. New commited records are captured from the `braindose.orders` table.

The result of the Camel processing is output at `$OUTPUT_DIR/$OUTPUT_FILENAME`, in this demo the complete path is `/tmp/dbzdemo/dbz-camel-order.output`. Watch the output using `tail`
```
tail -f /tmp/dbzdemo/dbz-camel-order.output
```

## Running the Demo

Run `docker compose` to bring up the demo environment.

```
docker compose up --build
```