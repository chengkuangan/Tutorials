package blog.braindose.opay.exception;

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
import java.util.Date;
import blog.braindose.opay.model.Status;

public class ExceptionCamelRoutes extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(ExceptionCamelRoutes.class);

    @Override
    public void configure() throws Exception {
        
        restConfiguration().bindingMode(RestBindingMode.json);

        from("kafka:{{exception.response.topic}}?brokers={{kafka.bootstrap.servers}}&groupId=exception-camel-service")
        .log("${body}")
        .unmarshal().json(JsonLibrary.Jackson, EventDataModel.class)
        .log("${body.id}")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                EventDataModel payload = (EventDataModel) in.getBody(EventDataModel.class);
                LOGGER.debug("payload = " + payload);
                LOGGER.debug("payload = " + (payload != null ? payload.getId() : "null"));
                exchange.setProperty("transactionId", payload.getId());
                exchange.setProperty("exceptionApproved", payload.isExceptionApproved());
                //in.setBody(payload);
            }
        })
        .choice()
            .when(simple("${exchangeProperty.exceptionApproved} == false"))
                .multicast().parallelProcessing()
                    .to("direct:sendToAudit", "direct:sendToResponse")
                    //.to("direct:sendToAudit")
//                .end()
                .endChoice()
            .otherwise()
                .multicast().parallelProcessing().to("direct:sendToReinstate", "direct:sendToAudit")
        ;

        from("direct:sendToReinstate")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                CasaEventData eventData = (CasaEventData) in.getBody();
                Date _now = new Date();
                eventData.setExceptionReason(null);
                eventData.setCreatedTimestamp(_now);
                eventData.setEventSources(ExceptionCamelRoutes.class.getName());
                eventData.setEventTimestamp(_now);
                eventData.setStatus(Status.SUBMITTED);
                eventData.setCoreProcessedTimestamp(null);
                eventData.setAuditTimestamp(null);
                eventData.setResponseReceivedTimestamp(null);
                eventData.setResponseMessages(null);

            }
        })
        .setHeader(KafkaConstants.KEY, exchangeProperty("transactionId"))
        .marshal().json(JsonLibrary.Jackson)
        .to("kafka:{{casa.new.topic}}?brokers={{kafka.bootstrap.servers}}")
        ;

        from("direct:sendToAudit")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                CasaEventData eventData = (CasaEventData) in.getBody();
                eventData.setEventSources(ExceptionCamelRoutes.class.getName());
                eventData.setAuditTimestamp(new Date());
            }
        })
        .setHeader(KafkaConstants.KEY, exchangeProperty("transactionId"))
        .marshal().json(JsonLibrary.Jackson)
        .to("kafka:{{exception.audit.topic}}?brokers={{kafka.bootstrap.servers}}")
        ;

        from("direct:sendToResponse")
        .process(new Processor(){
            public void process(Exchange exchange) {
                Message in = exchange.getIn();
                CasaEventData eventData = (CasaEventData) in.getBody();
                eventData.setAuditTimestamp(new Date());
            }
        })
        .setHeader(KafkaConstants.KEY, exchangeProperty("transactionId"))
        .marshal().json(JsonLibrary.Jackson)
        .to("kafka:{{casa.response.topic}}?brokers={{kafka.bootstrap.servers}}")
        ;
    }

}
