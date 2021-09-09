package blog.braindose.opay.core.casa;

import javax.transaction.Transactional;
import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import java.time.Instant;
import javax.enterprise.context.ApplicationScoped;

import io.debezium.outbox.quarkus.ExportedEvent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import blog.braindose.opay.core.AccountBalanceNotEnoughException;
import blog.braindose.opay.core.AccountNotFoundException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import blog.braindose.opay.model.Status;
//import blog.braindose.opay.util.Utils;
import blog.braindose.opay.obxevent.CasaEventData;
import blog.braindose.opay.obxevent.CasaAuditEvent;
import blog.braindose.opay.obxevent.CasaFailedEvent;
import blog.braindose.opay.obxevent.EventTypes;
import blog.braindose.opay.obxevent.AggregateTypes;

/*
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.nio.charset.Charset;
import org.apache.kafka.common.header.Header;
import java.io.IOException;
*/

/**
 * Provide implementation to consume messages for Casa transaction sent by
 * origination. Consume the Casa transaction messages from Kafka and update the
 * corresponding account balance in Casa database.
 */
@ApplicationScoped
public class ConsumeCasa {

    @Inject
    Event<ExportedEvent<?, ?>> event;

    private static final Logger LOGGER = Logger.getLogger(ConsumeCasa.class);
    
    /**
     * Need to use CDI in order for Transaction proxy to works such as
     * Transactional.TxType.REQUIRES_NEW
     */
    @Inject
    DBOperations dbo;

    /**
     * Consume the Casa event messages from Kafka and update the Casa account
     * balances accordingly.
     * 
     * Sent a response messages to the corresponding Kafka topic using Debezium
     * outbox pattern.
     * 
     * @param casa Kafka message containing the Casa transaction detail.
     */
    @Incoming("casa-in")
    @Retry(delay = 10, maxRetries = 3)
    @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    @Blocking
    @Transactional
    public void consume(ConsumerRecord<String, String> casa) {
        boolean failed = false;

        String key = casa.key();
        //String payload = Utils.cleanJSONString(casa.value());
        String payload = casa.value();
        CasaEventData casaEventData = null;

        LOGGER.debug("Kafka Message Key : " + key);
        LOGGER.debug("Kafka Payload : " + payload);
        LOGGER.debug("Kafka Headers ... ");

        casa.headers().forEach(h -> {
            LOGGER.debug(h.key() + " = " + new String(h.value()));
        });

        // requried for java.Time.Instant parsing
        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        try {
            String data = mapper.readValue(payload, String.class);
            casaEventData = mapper.readValue(data, CasaEventData.class);
            casaEventData.setMessageId(new String(casa.headers().lastHeader("id").value()));
            casaEventData.setEventSources(ConsumeCasa.class.getName());
            // ensure only one consistent timestamp is used.
            Instant processedTS = dbo.performBalanceAccount(casaEventData);
            casaEventData.setCoreProcessedTimestamp(processedTS);
            casaEventData.setEventTimestamp(processedTS);
            casaEventData.setStatus(Status.COMPLETED);
        } catch (JsonProcessingException e) {
            failed = true;
            if (casaEventData != null) { // in case of failure, set the necessay information for audit purposes
                casaEventData.setEventTimestamp(Instant.now());
                casaEventData.setCoreProcessedTimestamp(casaEventData.getEventTimestamp());
                casaEventData.setStatus(Status.FAILED);
                casaEventData.setResponseMessages("Payload format is not in proper JSON format.");
            }
            LOGGER.error("Payload format is not in proper JSON format.", e);
            // throw new RuntimeException("Payload format is not in proper JSON format.",
            // e);
        } catch (AccountBalanceNotEnoughException e) {
            failed = true;
            if (casaEventData != null) { // in case of failure, set the necessay information for audit purposes
                casaEventData.setEventTimestamp(Instant.now());
                casaEventData.setCoreProcessedTimestamp(casaEventData.getEventTimestamp());
                casaEventData.setStatus(Status.FAILED);
                casaEventData.setResponseMessages(e.getMessage());
            }
            LOGGER.error(e);
            // throw e;
        } catch (AccountNotFoundException e) {
            failed = true;
            if (casaEventData != null) { // in case of failure, set the necessay information for audit purposes
                casaEventData.setEventTimestamp(Instant.now());
                casaEventData.setCoreProcessedTimestamp(casaEventData.getEventTimestamp());
                casaEventData.setStatus(Status.FAILED);
                casaEventData.setResponseMessages(e.getMessage());
            }
            LOGGER.error(e);
            // throw e;
        } finally { // never throws exception out of this method. all Kafka messages are processed
                    // no matter succss or failed. The handling of the transaction is covered by the
                    // event messages in Kafka
            LOGGER.debug("In the try-catch finally stanza ...");
            if (casaEventData != null) {
                LOGGER.debug("Sending auditing and response messages to Kafka...");
                event.fire(new CasaResponseEvent(casaEventData));
                event.fire(new CasaAuditEvent(casaEventData, EventTypes.PAYMENT, AggregateTypes.AUDIT_CORE_CASA));
                if (failed)
                    event.fire(new CasaFailedEvent(casaEventData));
            }

        }
    }

    /*
    @Incoming("casa-in")
    @Retry(delay = 10, maxRetries = 3)
    // @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    @Blocking
    @Transactional
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<Void> onMessage(KafkaRecord<String, String> message) {
        return CompletableFuture.runAsync(() -> {

            // LOGGER.debug("Kafka message with key = {} arrived", message.getKey());

            // requried for java.Time.Instant parsing
            ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
            CasaEventData casaEventData = null;
            boolean failed = false;

            LOGGER.debug("Key = " + message.getKey());
            LOGGER.debug("Payload = " + message.getPayload());

            try {
                String data = mapper.readValue(message.getPayload(), String.class);
                casaEventData = mapper.readValue(data, CasaEventData.class);
                casaEventData.setMessageId(message.getKey());
                casaEventData.setEventSources(ConsumeCasa.class.getName());
                // ensure only one consistent timestamp is used.
                Instant processedTS = dbo.performBalanceAccount(casaEventData);
                casaEventData.setCoreProcessedTimestamp(processedTS);
                casaEventData.setEventTimestamp(processedTS);
                casaEventData.setStatus(Status.COMPLETED);
                message.ack();
            } catch (JsonProcessingException e) {
                failed = true;
                if (casaEventData != null) { // in case of failure, set the necessay information for audit purposes
                    casaEventData.setEventTimestamp(Instant.now());
                    casaEventData.setCoreProcessedTimestamp(casaEventData.getEventTimestamp());
                    casaEventData.setStatus(Status.FAILED);
                    casaEventData.setResponseMessages("Payload format is not in proper JSON format.");
                }
                LOGGER.error("Payload format is not in proper JSON format.", e);
                // throw new RuntimeException("Payload format is not in proper JSON format.",
                // e);
            } catch (AccountBalanceNotEnoughException e) {
                failed = true;
                if (casaEventData != null) { // in case of failure, set the necessay information for audit purposes
                    casaEventData.setEventTimestamp(Instant.now());
                    casaEventData.setCoreProcessedTimestamp(casaEventData.getEventTimestamp());
                    casaEventData.setStatus(Status.FAILED);
                    casaEventData.setResponseMessages(e.getMessage());
                }
                LOGGER.error(e);
                // throw e;
            } catch (AccountNotFoundException e) {
                failed = true;
                if (casaEventData != null) { // in case of failure, set the necessay information for audit purposes
                    casaEventData.setEventTimestamp(Instant.now());
                    casaEventData.setCoreProcessedTimestamp(casaEventData.getEventTimestamp());
                    casaEventData.setStatus(Status.FAILED);
                    casaEventData.setResponseMessages(e.getMessage());
                }
                LOGGER.error(e);
                // throw e;
            } finally { // never throws exception out of this method. all Kafka messages are processed
                        // no matter succss or failed. The handling of the transaction is covered by the
                        // event messages in Kafka
                LOGGER.debug("In the try-catch finally stanza ...");
                if (casaEventData != null) {
                    LOGGER.debug("Sending auditing and response messages to Kafka...");
                    event.fire(new CasaResponseEvent(casaEventData));
                    event.fire(new CasaAuditEvent(casaEventData, EventTypes.PAYMENT, AggregateTypes.AUDIT_CORE_CASA));
                    if (failed)
                        event.fire(new CasaFailedEvent(casaEventData));
                }

            }

        });
    }

    private String getHeaderAsString(KafkaRecord<?, ?> record, String name) {
        Header header = record.getHeaders().lastHeader(name);
        if (header == null) {
            throw new IllegalArgumentException("Expected record header '" + name + "' not present");
        }

        return new String(header.value(), Charset.forName("UTF-8"));
    }

    private JsonNode deserialize(String event) {
        JsonNode eventPayload;

        try {
            String unescaped = objectMapper.readValue(event, String.class);
            eventPayload = objectMapper.readTree(unescaped);
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't deserialize event", e);
        }

        return eventPayload.has("schema") ? eventPayload.get("payload") : eventPayload;
    }
    */

}
