package blog.braindose.opay.customer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.Exchange;
import blog.braindose.opay.model.customer.Customer;
import blog.braindose.opay.model.customer.Account;
import blog.braindose.opay.model.core.Casa;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Message;
import blog.braindose.opay.model.audit.Audit;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
//import org.apache.commons.lang3.time.DateUtils;


public class Routes extends RouteBuilder {

    //Map<String, Double> dailyTotalAmout = new HashMap<>(); 

    @Override
    public void configure() throws Exception {
        restConfiguration().bindingMode(RestBindingMode.json);
        rest("/cust").get("/{accountNo}").route()
        //.log("accNo = ${header.accountNo}")
        .setBody().simple("${header.accountNo}")
        .to("direct:getCustomer")
        .to("direct:getAccount")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                Casa casa = in.getBody(Casa.class);
                Customer cust = (Customer) exchange.getProperty("customer");
                Account acc = cust.getAccounts().get(0);
                acc.setBalance(casa.getBalance());
                acc.setCreatedTimestamp(casa.getCreatedDate());
                acc.setUpdatedTimestamp(casa.getUpdatedDate());
                acc.setDailyTotalAmount(casa.getDailyTotalAmount());
                acc.setDtaUpdatedTimestamp(casa.getDtaUpdatedTimestamp());
                in.setBody(cust);
            }
        })
        ;


        from("direct:getCustomer")
        .removeHeader("CamelHttpPath")
        .removeHeader("CamelHttpUri")
        .setHeader(Exchange.HTTP_PATH, simple("${body}"))
        //.circuitBreaker()
            //.faultToleranceConfiguration().timeoutEnabled(true).timeoutDuration(1000).end()
            .to("{{custprofile.get.endpoint}}")
            .unmarshal().json(JsonLibrary.Jackson, Customer.class)
            .setProperty("customer", simple("${body}"))
            //.log("exchangeProperty.customer = ${exchangeProperty.customer.firstName}")
            //.log("customer = ${body}")
            //.log("firstName = ${body.firstName}")
            //.log("account = ${body.accounts.size}")
            //.log("accountNo = ${body.accounts[0].accountNo}")
            .setBody().simple("${body.accounts[0].accountNo}")
        //.end()
        ;

        from("direct:getAccount")
        .removeHeader("CamelHttpPath")
        .removeHeader("CamelHttpUri")
        //.log("body = ${body}")
        .setHeader(Exchange.HTTP_PATH, simple("${body}"))
        //.circuitBreaker()
            //.faultToleranceConfiguration().timeoutEnabled(true).timeoutDuration(1000).end()
            .to("{{core.casa.get.endpoint}}")
            .unmarshal().json(JsonLibrary.Jackson, Casa.class)
        //.onFallback()
        //    .transform().constant("Fallback messages")
        //.end()
        ;

        /*

        from("kafka:{{audit.topic}}?brokers={{kafka.bootstrap.server}}&groupId=customer-camel-service")
        .unmarshal().json(JsonLibrary.Jackson, Audit.class)
        .filter(simple("${body.lastStatus} == 'COMPLETED'"))
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                Audit audit = in.getBody(Audit.class);
                String sourceAccountNo =  audit.getPayload().getSourceAccountNo();
                System.out.println("sourceAccountNo = " + sourceAccountNo);
                System.out.println("dailyTotalAmout.size() = " + dailyTotalAmout.size());
                double total = dailyTotalAmout.getOrDefault(sourceAccountNo, new Double(0)).doubleValue();
                System.out.println("total = " + total);
                if (DateUtils.isSameDay(new Date(), audit.getEventTimestamp())){
                    total += audit.getPayload().getAmount();
                }
                dailyTotalAmout.put(sourceAccountNo, total);
                System.out.println("dailyTotalAmout = " + dailyTotalAmout.get(sourceAccountNo));
            }
        })
        //.log("kafka message = ${body.payload.amount}")
        ;
        */
    }

}
