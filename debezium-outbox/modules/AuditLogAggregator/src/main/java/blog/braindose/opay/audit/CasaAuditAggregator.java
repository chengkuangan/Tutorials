package blog.braindose.opay.audit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.Topology;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.Materialized;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;

import blog.braindose.opay.obxevent.CasaEventData;
import blog.braindose.opay.model.Status;

import org.jboss.logging.Logger;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.Duration;
import java.time.Instant;

import blog.braindose.opay.audit.model.Casa;
import blog.braindose.opay.audit.model.AuditData;
import blog.braindose.opay.audit.model.AuditEntry;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.apache.kafka.streams.kstream.GlobalKTable;

/**
 * Aggregates the audit trail from 
 */
@ApplicationScoped
public class CasaAuditAggregator {

    private static final Logger LOGGER = Logger.getLogger(CasaAuditAggregator.class);

    @ConfigProperty(name = "kafkatopic.casaaudit")
    String KAFKA_TOPIC_CASA_AUDIT;// = "casa.audit.events";
    @ConfigProperty(name = "kafkatopic.casacoreaudit")
    String KAFKA_TOPIC_CASA_CORE_AUDIT;// = "casa.core.audit.events";
    @ConfigProperty(name = "kafkatopic.paymentaudit")
    String KAFKA_TOPIC_PAYMENT_AUDIT;// = "payment.audit.events";
    @ConfigProperty(name = "kafka.streams.joinwindow.duration")
    int KAFKA_STREAMS_JOINWINDOW_DURATION;
    final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Produces
    public Topology buildTopology() {

        LOGGER.debug("KAFKA_TOPIC_CASA_AUDIT = " + KAFKA_TOPIC_CASA_AUDIT);
        LOGGER.debug("KAFKA_TOPIC_CASA_CORE_AUDIT = " + KAFKA_TOPIC_CASA_CORE_AUDIT);
        LOGGER.debug("KAFKA_TOPIC_PAYMENT_AUDIT = " + KAFKA_TOPIC_PAYMENT_AUDIT);
        LOGGER.debug("KAFKA_STREAMS_JOINWINDOW_DURATION = " + KAFKA_STREAMS_JOINWINDOW_DURATION);

        StreamsBuilder builder = new StreamsBuilder();
        ObjectMapperSerde<AuditData> auditDataSerde = new ObjectMapperSerde<>(AuditData.class);
    
        KStream<String, String> coreAuditStream = builder.stream(KAFKA_TOPIC_CASA_CORE_AUDIT,
                Consumed.with(Serdes.String(), Serdes.String()));

        builder.stream(
            KAFKA_TOPIC_CASA_AUDIT,
            Consumed.with(Serdes.String(), Serdes.String()))        // Serialized JSON string from JsonNode creates extra double quotes, causing it is not possible to use Jackson to deserialize into Java object
            .join(
                coreAuditStream,
                (casaAudit, coreAudit) -> {
                    // Multiple audit entries since Casa service received the response from core service.
                    List<AuditEntry> auditEntries = new ArrayList<>();
                    try {
                        LOGGER.debug("Processing casa audit trail...");
                        CasaEventData casaAuditObj = createCasaEventData(casaAudit);
                        auditEntries.add(createAuditEntry(casaAuditObj));
                        
                        LOGGER.debug("Processing core audit trail for casa transaction ...");

                        auditEntries.add(createAuditEntry(createCasaEventData(coreAudit)));
                        
                        AuditData<Casa> auditData = new AuditData<>(
                            casaAuditObj.getId(), 
                            auditEntries, 
                            new Casa(casaAuditObj.getRecipientAccountNo(), casaAuditObj.getSourceAccountNo(), casaAuditObj.getAmount(), casaAuditObj.getRecipientReference()), 
                            Instant.now().toString(), 
                            casaAuditObj.getStatus().toString());

                        String jsonInString = mapper.writeValueAsString(auditData);
                        LOGGER.debug("Joined result = " + jsonInString);
                        return jsonInString;
                        
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Problem parsing Kafka message into JSON.");
                        throw new RuntimeException("Problem parsing Kafka message into JSON", e);
                    }
                },
                JoinWindows.of(Duration.ofMinutes(KAFKA_STREAMS_JOINWINDOW_DURATION)),
                Joined.with(Serdes.String(), Serdes.String(), Serdes.String())
            )
            .groupByKey()
            .reduce(            // deduplication of audit trail ... 
                (value1, value2) -> AuditData.reduce(value1, value2),
                Materialized.with(Serdes.String(), Serdes.String())
            )
            .toStream()
            //.print(org.apache.kafka.streams.kstream.Printed.toSysOut())
            .to(KAFKA_TOPIC_PAYMENT_AUDIT, Produced.with(Serdes.String(), Serdes.String()))
        ;
        return builder.build();
    }

    /**
     * Create AuditEntry from the CasaEventData
     * @param casaEventData CasaEventData
     * @return AuditEntry 
     */
    private AuditEntry createAuditEntry(CasaEventData casaEventData) throws JsonProcessingException{
        return new AuditEntry(casaEventData.getEventSources(), casaEventData.getActualTimestamp().toString(), casaEventData.getResponseMessages(), casaEventData.getStatus().toString());
    }

    /**
     * Create CasaEventData from Kafka audit trail message
     * @param message JSON formatted audit trail 
     * @return CasaEventData
     */
    private CasaEventData createCasaEventData(String message) throws JsonProcessingException{
        LOGGER.debug("audit message = " + message);
        String data = mapper.readValue(message, String.class);        // there are additional double quotes in Kafka message caused by JsonNode serilization
        return mapper.readValue(data, CasaEventData.class);
    }

}
