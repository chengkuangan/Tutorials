package blog.braindose.opay.audit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.Topology;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.Materialized;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;

import blog.braindose.opay.obxevent.CasaEventData;

import org.jboss.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.time.Duration;
import java.time.Instant;

import blog.braindose.opay.audit.model.Casa;
import blog.braindose.opay.audit.model.AuditData;
import blog.braindose.opay.audit.model.AuditEntry;

import org.eclipse.microprofile.config.inject.ConfigProperty;

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
    @ConfigProperty(name = "kafkatopic.exceptionaudit")
    String KAFKA_TOPIC_EXCEPTION_AUDIT;
    @ConfigProperty(name = "kafkatopic.validationaudit")
    String KAFKA_TOPIC_VALIDATION_AUDIT;

    @Produces
    public Topology buildTopology() {

        LOGGER.debug("KAFKA_TOPIC_CASA_AUDIT = " + KAFKA_TOPIC_CASA_AUDIT);
        LOGGER.debug("KAFKA_TOPIC_CASA_CORE_AUDIT = " + KAFKA_TOPIC_CASA_CORE_AUDIT);
        LOGGER.debug("KAFKA_TOPIC_PAYMENT_AUDIT = " + KAFKA_TOPIC_PAYMENT_AUDIT);
        LOGGER.debug("KAFKA_STREAMS_JOINWINDOW_DURATION = " + KAFKA_STREAMS_JOINWINDOW_DURATION);
        LOGGER.debug("KAFKA_TOPIC_EXCEPTION_AUDIT = " + KAFKA_TOPIC_EXCEPTION_AUDIT);
        LOGGER.debug("KAFKA_TOPIC_VALIDATION_AUDIT = " + KAFKA_TOPIC_VALIDATION_AUDIT);

        StreamsBuilder builder = new StreamsBuilder();
        ObjectMapperSerde<CasaEventData> casaEventDataSerde = new ObjectMapperSerde<>(CasaEventData.class);
        
        ObjectMapperSerde<AuditData> auditDataSerde = new ObjectMapperSerde<>(AuditData.class);

        List<String> topics2 = new ArrayList<>();
        //topics.add(KAFKA_TOPIC_CASA_AUDIT);
        //topics.add(KAFKA_TOPIC_CASA_CORE_AUDIT);
        topics2.add(KAFKA_TOPIC_EXCEPTION_AUDIT);
        topics2.add(KAFKA_TOPIC_VALIDATION_AUDIT);

        List<String> topics1 = new ArrayList<>();
        topics1.add(KAFKA_TOPIC_CASA_AUDIT);
        topics1.add(KAFKA_TOPIC_CASA_CORE_AUDIT);

        // ---- START: Temporary to workaround the reported problem: https://issues.redhat.com/browse/DBZ-4068 
        KStream<String, CasaEventData>  auditEventStream1 = builder.stream(
            topics1,
            Consumed.with(Serdes.String(), Serdes.String()))
            .peek(
                (k,v) -> {
                    LOGGER.debug("auditEventStream1 -> all -> key = " + k + ", value = " + v);
                }
            )
            .map(
                (key, value) -> {
                    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

                    boolean needEscaped = value.startsWith("\"");
                    String data = value;
                    CasaEventData eventData = null;
                    try{
                        if (needEscaped){
                            data = mapper.readValue(value, String.class);
                        }
                        eventData = mapper.readValue(data, CasaEventData.class);
                    }catch (JsonProcessingException e) {
                        LOGGER.error("Data format is not in proper JSON format.", e);
                    } 
                    //key = key.startsWith("\"") ? key = key.substring(1, key.length() -1) : key;
                    return KeyValue.pair(key, eventData);     
                }
            )
            .peek(
                (key, value) -> {
                    LOGGER.debug("auditEventStream1 -> key = " + key + ", value.id = " + value.getId());
                }
            )
            ;
        
        KStream<String, CasaEventData>  auditEventStream2 = builder.stream(
            topics2,
            Consumed.with(Serdes.String(), casaEventDataSerde))
            /*
            .map(
                (key, value) -> {
                    key = !key.startsWith("\"") ? "\"" + key + "\"" : key;
                    return KeyValue.pair(key, value);     
                }
            )*/
            .peek(
                (k,v) -> {
                    LOGGER.debug("auditEventStream2 -> key = " + k + ", value = " + v.toJsonString());
                }
            )
            ;
        
        auditEventStream1
        .merge(auditEventStream2)
        .groupBy(
            (k, v) -> v.getId(),
            Grouped.with(Serdes.String(), casaEventDataSerde)
        )
        .aggregate(
                AuditData<Casa>::new,
                (key, value, aggregate) -> {
                    Casa casa = new Casa(value.getRecipientAccountNo(), value.getSourceAccountNo(), value.getAmount(), value.getRecipientReference());
                    aggregate.setPayload(casa);
                    return aggregate.add(value);
                },
                Materialized.with(Serdes.String(), auditDataSerde)
            )
            //.suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(KAFKA_STREAMS_JOINWINDOW_DURATION), Suppressed.BufferConfig.unbounded()))
            //.suppress(Suppressed.untilTimeLimit(Duration.ofMinutes(KAFKA_STREAMS_JOINWINDOW_DURATION), Suppressed.BufferConfig.maxRecords(4)))
            .toStream()
            .map(
                //TODO: Need to raise a support ticket
                (k, v) -> {     // this properly related to DBZ-4068 bug. Kafka connect failed to process if the key is a string and not enclosed with double quotes
                    return KeyValue.pair(
                        (k.startsWith("\"") ? k : "\"" + k + "\""), v.toJsonString()
                    );
                }
            )
            .peek(
                (k,v) -> {
                    LOGGER.debug("auditStream -> aggregated -> key = " + k + ", value size = " + v);
                }
            )
            .to(KAFKA_TOPIC_PAYMENT_AUDIT, Produced.with(Serdes.String(), Serdes.String()))
        ;

        // ---- END: Temporary to workaround the reported problem: https://issues.redhat.com/browse/DBZ-4068 

        // --- BEGIN: Enhanced version
        /*
        List<String> topics = new ArrayList<>();
        topics.add(KAFKA_TOPIC_CASA_AUDIT);
        topics.add(KAFKA_TOPIC_CASA_CORE_AUDIT);
        topics.add(KAFKA_TOPIC_EXCEPTION_AUDIT);
        topics.add(KAFKA_TOPIC_VALIDATION_AUDIT);
        builder.stream(
            topics,
            Consumed.with(Serdes.String(), casaEventDataSerde))
            .peek(
                (k,v) -> {
                    LOGGER.debug("Casa and Core auditStream -> all -> key = " + k + ", value = " + v.toJsonString());
                }
            )
            .groupByKey()
            .aggregate(
                AuditData<Casa>::new,
                (key, value, aggregate) -> {
                    Casa casa = new Casa(value.getRecipientAccountNo(), value.getSourceAccountNo(), value.getAmount(), value.getRecipientReference());
                    aggregate.setPayload(casa);
                    return aggregate.add(value);
                },
                Materialized.with(Serdes.String(), auditDataSerde)
            )
            .mapValues(
                (v) -> {
                    return v.toJsonString();
                }
            )
            .toStream()
            .peek(
                (k,v) -> {
                    LOGGER.debug("auditStream -> aggregated -> key = " + k + ", value size = " + v);
                }
            )
            .to(KAFKA_TOPIC_PAYMENT_AUDIT, Produced.with(Serdes.String(), Serdes.String()));
            ;
        */
        // --- END: Enhanced version

        return builder.build();
    }

}
