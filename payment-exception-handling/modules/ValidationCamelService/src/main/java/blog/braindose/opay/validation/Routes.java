package blog.braindose.opay.validation;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.Processor;
import org.apache.camel.Message;
import blog.braindose.opay.obxevent.CasaEventData;
import blog.braindose.opay.model.customer.Customer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import blog.braindose.opay.model.validation.CreditDetail;
import blog.braindose.opay.model.validation.Transaction;
import org.apache.camel.component.kafka.KafkaConstants;
import blog.braindose.opay.cloudevents.KogitoEventModel;

public class Routes extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(Routes.class);

    @Override
    public void configure() throws Exception {
        
        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        restConfiguration().bindingMode(RestBindingMode.json);

        from("kafka:{{casa.new.topic}}?brokers={{kafka.bootstrap.servers}}&groupId=cfa-camel-service")
        .log("${body}")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                String payload = in.getBody(String.class);
                boolean needEscaped = payload.startsWith("\"");
                String data = payload;
                try{
                    if (needEscaped){
                        data = mapper.readValue(payload, String.class);
                    }
                    CasaEventData casaEventData = mapper.readValue(data, CasaEventData.class);
                    exchange.setProperty("transactionId", casaEventData.getId());
                    exchange.setProperty("amount", casaEventData.getAmount());
                    exchange.setProperty("exception", casaEventData.isExceptionApproved());
                    in.setBody(casaEventData.getSourceAccountNo());
                }
                catch(JsonProcessingException e){
                    LOGGER.error("Problem deserialize the CasaEventData JSON message.", e);
                    throw new RuntimeException(e);
                }
            }
        })
        //.log("sourceAccountNo = ${body}")
        .to("direct:getCustomerByAccountNo")
        //.to("direct:sendCheckLimitCloudEvents")
        .multicast().parallelProcessing().to("direct:sendCheckLimitCloudEvents", "direct:sendCheckFraudCloudEvents")
        ;

        from("direct:getCustomerByAccountNo")
        .removeHeaders("*")     // remove all kafka headers
        .setHeader(Exchange.CONTENT_ENCODING, simple("gzip, deflate, br"))
        .setHeader("Accept", simple("*/*"))
        .setHeader(Exchange.HTTP_METHOD, simple("GET"))
        .setHeader(Exchange.HTTP_PATH, simple("${body}"))
        //.circuitBreaker()
        //    .faultToleranceConfiguration().timeoutEnabled(true).timeoutDuration(5000).end()
            .to("{{cust.camel.service}}")
            .unmarshal().json(JsonLibrary.Jackson, Customer.class)
        //.end()
        ;

        from("direct:sendCheckLimitCloudEvents")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                Customer cust = in.getBody(Customer.class);
                CreditDetail cd = new CreditDetail(
                    (double)exchange.getProperty("amount"), 
                    (String) exchange.getProperty("transactionId"),
                    cust.getAccounts().get(0).getDailyLimit(),
                    cust.getAccounts().get(0).getBalance(),
                    cust.getAccounts().get(0).getDailyTotalAmount(),
                    (boolean)exchange.getProperty("exception")
                    );
                KogitoEventModel<CreditDetail> model = new KogitoEventModel<>(
                    (String)exchange.getProperty("transactionId"), 
                    "http://cfa-camel-service", 
                    "CheckLimitDecisionRequest", 
                    "Opay Limit Checking", 
                    "checklimit", 
                    "https://kiegroup.org/dmn/_1D3A94BF-BCD5-470A-B22A-38B9E60ED24E", 
                    cd);
                in.setBody(model.toJsonString());
            }
        })
        .setHeader(KafkaConstants.KEY, exchangeProperty("transactionId"))
        .to("kafka:{{check.limit.topic}}?brokers={{kafka.bootstrap.servers}}")
        ;

        from("direct:sendCheckFraudCloudEvents")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                Customer cust = in.getBody(Customer.class);
                Transaction t = new Transaction(
                    (String) exchange.getProperty("transactionId"), 
                    (double)exchange.getProperty("amount"), 
                    cust.getAddress().get(0).getCountry(), 
                    cust.getAddress().get(0).getCountry(), 
                    cust.getAccounts().get(0).getAccountType(),
                    (boolean)exchange.getProperty("exception"));
                KogitoEventModel<Transaction> model = new KogitoEventModel<>(
                    (String)exchange.getProperty("transactionId"), 
                    "http://cfa-camel-service", 
                    "CheckFraudDecisionRequest", 
                    "Opay Fraud Checking", 
                    "checkfraud", 
                    "https://kiegroup.org/dmn/_E3BEAA45-9170-4D05-8F9A-0F80BFD8EDDB", 
                    t);
                in.setBody(model.toJsonString());
            }
        })
        .setHeader(KafkaConstants.KEY, exchangeProperty("transactionId"))
        .to("kafka:{{check.fraud.topic}}?brokers={{kafka.bootstrap.servers}}")
        ;
    }

}
