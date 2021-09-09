package blog.braindose.opay.casa;

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

import blog.braindose.opay.exception.RecordNotFoundException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
//import blog.braindose.opay.util.Utils;
import blog.braindose.opay.obxevent.CasaEventData;
import blog.braindose.opay.obxevent.AggregateTypes;
import blog.braindose.opay.obxevent.CasaAuditEvent;
import blog.braindose.opay.obxevent.CasaFailedEvent;
import blog.braindose.opay.obxevent.EventTypes;

/**
 * Consume Casa response messages from core service processing.
 * The messages are consumed from Kafka
 */
@ApplicationScoped
public class ConsumeCasaResponse {
    
    private static final Logger LOGGER = Logger.getLogger(ConsumeCasaResponse.class);

    // Need to use CDI to inject the object, otherwise transaction proxy will not work.
    @Inject DBOperations dbo;

    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Incoming("casa-response")
    @Retry(delay = 10, maxRetries = 3)
    @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    @Blocking
    @Transactional
    public void consume(ConsumerRecord<String, String> response) {
        boolean failed = false;
        
        String key = response.key();
        //String payload = Utils.cleanJSONString(response.value());
        String payload = response.value();
        
        LOGGER.debug("Kafka Message Key : " + key);
        LOGGER.debug("Kafka Payload : " + payload);
        LOGGER.debug("Kafka Headers ... ");

        response.headers().forEach(h -> {
            LOGGER.debug(h.key() + " = " + new String(h.value()));
        });
        
        ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        CasaEventData casaEventData = null;
        try{
            String data = mapper.readValue(payload, String.class);
            casaEventData = mapper.readValue(data, CasaEventData.class);
            //casaEventData = mapper.readValue(payload, CasaEventData.class);
            casaEventData.setMessageId(new String(response.headers().lastHeader("id").value()));
            casaEventData.setEventSources(ConsumeCasaResponse.class.getName());
            Instant processedTS = dbo.updateResponse(casaEventData);
            casaEventData.setEventTimestamp(processedTS);
            casaEventData.setResponseReceivedTimestamp(processedTS);
        }
        catch (JsonProcessingException e){      // populate necessary event data for auditing and failed event
            failed = true;
            if (casaEventData != null){ 
                casaEventData.setEventTimestamp(Instant.now());
                casaEventData.setResponseReceivedTimestamp(casaEventData.getEventTimestamp());
            }
            LOGGER.error("Payload format is not in proper JSON format.", e);
        }
        catch(RecordNotFoundException e){       // populate necessary event data for auditing and failed event
            failed = true;
            if (casaEventData != null){ 
                casaEventData.setEventTimestamp(Instant.now());
                casaEventData.setResponseReceivedTimestamp(casaEventData.getEventTimestamp());
            }
            LOGGER.error(e);
        }
        finally{        // never throws exception out from this method, so that audit event and failed event can be captured.
            LOGGER.debug("In the try-catch finally stanza ...");
            if (casaEventData != null) { 
                LOGGER.debug("Sending auditing and response messages to Kafka...");
                casaEventData.setEventTimestamp(Instant.now());
                casaEventData.setResponseReceivedTimestamp(casaEventData.getEventTimestamp());
                event.fire(new CasaAuditEvent(casaEventData, EventTypes.PAYMENT, AggregateTypes.AUDIT_CASA));
                if (failed) event.fire(new CasaFailedEvent(casaEventData));
            }
            
        }
        
    }

}
