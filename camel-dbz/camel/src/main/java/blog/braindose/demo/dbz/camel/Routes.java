package blog.braindose.demo.dbz.camel;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.kafka.connect.data.Struct;

import blog.braindose.demo.dbz.camel.model.CustomerOrder;

import java.util.List;
import java.util.Map;

public class Routes extends RouteBuilder {

       String DBZ_SETTINGS = "debezium-postgres:dbz-camel?offsetStorageFileName={{dbz.offset.file}}" +
                     "&databaseDbname={{db.name}}" +
                     "&databaseHostname={{db.host}}" +
                     "&databasePort={{db.port}}" +
                     "&databaseUser={{db.user}}" +
                     "&databasePassword={{db.password}}" +
                     "&databaseServerName={{dbz.dbservername}}" +
                     "&databaseHistoryFileFilename={{dbz.dbhistoryfile}}" +
                     "&schemaIncludeList={{dbz.schemaincludelist}}" +
                     "&tableIncludeList={{dbz.tableincludelist}}";

       @Override
       public void configure() throws Exception {

              from(DBZ_SETTINGS)
                            .log("Event received from Debezium : ${body}")
                            .process(
                                          exchange -> {
                                                 Message in = exchange.getIn();
                                                 final Struct body = in.getBody(Struct.class);
                                                 CustomerOrder order = new CustomerOrder(
                                                               body.getString("orderid"),
                                                               body.getString("orderdate"),
                                                               body.getString("sku"),
                                                               body.getString("description"),
                                                               body.getFloat64("amount"));
                                                 in.setBody(order);

                                          })
                            .log("Captured order: ${body}")
                            .setProperty("orderDetail", simple("${body}"))
                            .setProperty("orderId", simple("${body.orderId}"))
                            .to("sql:classpath:sql/customer.sql")
                            .log("SQL Query from customer table: ${body}")
                            .process(
                                          exchange -> {
                                                 CustomerOrder order = (CustomerOrder) exchange
                                                               .getProperty("orderDetail");
                                                 Message in = exchange.getIn();
                                                 String custname = (String) ((Map) ((List) in.getBody()).get(0))
                                                               .get("name");
                                                 Integer custId = (Integer) ((Map) ((List) in.getBody()).get(0))
                                                               .get("custId");
                                                 order.setCustName(custname);
                                                 order.setCustId(custId);
                                                 in.setBody(order);

                                          })
                            .log("Enriched Order: ${body}")
                            .marshal().json()
                            .log("Transform Order Object to Json: ${body}")
                            .to("file:{{output.dir}}?fileName={{output.filename}}&appendChars=\n&fileExist=Append")
                            .log("JSON data saved into file.");

       }
}
