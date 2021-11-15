package blog.braindose.opay.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.Topology;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.BranchedKStream;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.Joined;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;

//import blog.braindose.opay.obxevent.CasaEventData;
import blog.braindose.opay.cloudevents.KogitoEventModel;
import blog.braindose.opay.model.Status;

import org.jboss.logging.Logger;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.Duration;
import java.util.Date;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import blog.braindose.opay.validation.model.CheckResponse;
import blog.braindose.opay.validation.model.EventData;
import blog.braindose.opay.validation.model.ValidationResult;

/**
 * 
 */
@ApplicationScoped
public class ValidationHandler {

    private static final Logger LOGGER = Logger.getLogger(ValidationHandler.class);

    @ConfigProperty(name = "kafkatopic.casa.new.events")
    String KAFKA_TOPIC_CASA_NEW;
    @ConfigProperty(name = "kafkatopic.checklimit.response")
    String KAFKA_TOPIC_CHECKLIMIT_RESPONSE;
    @ConfigProperty(name = "kafkatopic.checkfraud.response")
    String KAFKA_TOPIC_CHECKFRAUD_RESPONSE;
    @ConfigProperty(name = "kafkatopic.casa.events")
    String KAFKA_TOPIC_CASA_EVENTS;
    @ConfigProperty(name = "kafkatopic.casa.response.events")
    String KAFKA_TOPIC_CASA_RESPONSE_EVENTS;
    @ConfigProperty(name = "kafkatopic.exception.events")
    String KAFKA_TOPIC_EXCEPTION_EVENTS;
    @ConfigProperty(name = "kogitoprocess.type")
    String KOGITO_PROCESS_TYPE;
    @ConfigProperty(name = "kafka.streams.joinwindow.duration")
    int KAFKA_STREAMS_JOINWINDOW_DURATION;
    @ConfigProperty(name = "kafkatopic.audit.events")
    String KAFKA_TOPIC_AUDIT_EVENTS;
    
    final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Produces
    public Topology buildTopology() {

        LOGGER.debug("KAFKA_TOPIC_CASA_NEW = " + KAFKA_TOPIC_CASA_NEW);
        LOGGER.debug("KAFKA_TOPIC_CHECKLIMIT_RESPONSE = " + KAFKA_TOPIC_CHECKLIMIT_RESPONSE);
        LOGGER.debug("KAFKA_TOPIC_CHECKFRAUD_RESPONSE = " + KAFKA_TOPIC_CHECKFRAUD_RESPONSE);
        LOGGER.debug("KAFKA_TOPIC_CASA_EVENTS = " + KAFKA_TOPIC_CASA_EVENTS);
        LOGGER.debug("KAFKA_TOPIC_EXCEPTION_EVENTS = " + KAFKA_TOPIC_EXCEPTION_EVENTS);
        LOGGER.debug("KAFKA_STREAMS_JOINWINDOW_DURATION = " + KAFKA_STREAMS_JOINWINDOW_DURATION);
        LOGGER.debug("KAFKA_TOPIC_AUDIT_EVENTS = " + KAFKA_TOPIC_AUDIT_EVENTS);
        

        StreamsBuilder builder = new StreamsBuilder();
        
        ObjectMapperSerde<CheckResponse> checkResponseSerde = new ObjectMapperSerde<>(CheckResponse.class);
        ObjectMapperSerde<ValidationResult> validationResultSerde = new ObjectMapperSerde<>(ValidationResult.class);
        ObjectMapperSerde<EventData> casaEventDataSerde = new ObjectMapperSerde<>(EventData.class);
        //ObjectMapperSerde<KogitoEventModel> kogitoEventModelSerde = new ObjectMapperSerde<>(KogitoEventModel.class);
        
        
        List<String> topics = new ArrayList<>();
        topics.add(KAFKA_TOPIC_CHECKLIMIT_RESPONSE);
        topics.add(KAFKA_TOPIC_CHECKFRAUD_RESPONSE);

        KStream<String, ValidationResult> validationStream =  builder.stream(
            topics,
            Consumed.with(Serdes.String(), checkResponseSerde))
            .peek(
                (k,v) -> {
                    LOGGER.debug("validationStream -> key = " + k + ", value = " + v.getTransactionId() + ", reason = " + v.getReason() + ", status = " + v.isStatus() + ", exceptionAllow = " + v.isAllowException());
                }
            )
            .groupBy( 
                (id, value) -> value.getTransactionId(),
                Grouped.with(Serdes.String(), checkResponseSerde)
            )
            .aggregate(
                ValidationResult::new,
                (key, value, aggregate) -> aggregate.addResponse(value),
                Materialized.with(Serdes.String(), validationResultSerde)
            )
            .toStream()
            ;
        
        KStream<String, EventData>  transactionEventStream = builder.stream(
            KAFKA_TOPIC_CASA_NEW, 
            // TODO: Reported issue at https://issues.redhat.com/browse/DBZ-4068
            // This cause the JSON string cannot be deserilized into Object directly and it has to be consumed as String
            Consumed.with(Serdes.String(), Serdes.String())
        )
        .map(
            (key, value) -> {
                
                LOGGER.debug("transactionEventStream -> key.class = " + key.getClass());
                LOGGER.debug("transactionEventStream -> key = " + key + ", value = " + value);
                
                ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

                // TODO: Reported issue at https://issues.redhat.com/browse/DBZ-4068
                // The message key generated by Debezium outbox extension contains extra double quotes.
                boolean needEscaped = value.startsWith("\"");
                String data = value;
                EventData eventData = null;
                try{
                    if (needEscaped){
                        data = mapper.readValue(value, String.class);
                    }
                    eventData = mapper.readValue(data, EventData.class);
                }catch (JsonProcessingException e) {
                    LOGGER.error("Casa Event Data format is not in proper JSON format.", e);
                } 
                key = key.substring(1, key.length() -1);
                return KeyValue.pair(key, eventData);     
            }
        )
        .peek(
            (key, value) -> {
                LOGGER.debug("transactionEventStream -> key = " + key + ", value.id = " + value.getId());
            }
        )
        ;
        
        KStream<String, EventData> joined = validationStream
            .peek(
                (key, value) -> {
                    LOGGER.debug("Joining ... validationStream -> key = " + key + ", value.status = " + value.isStatus());
                }
            )
            .join(
            transactionEventStream,
            (validationResult, eventData) -> {
                
                LOGGER.debug("casaEventJSON = " + eventData);
                LOGGER.debug("validationResult = " + validationResult);

                eventData.setEventSources(ValidationHandler.class.getName());
                eventData.setEventTimestamp(new Date());
                
                if (!validationResult.isStatus()){
                    eventData.setResponseMessages(validationResult.getReasons());
                    eventData.setStatus(Status.REJECTED);
                }
                else{
                    eventData.setStatus(Status.VALIDATED);
                }

                eventData.setAllowException(validationResult.isAllowException());

                return eventData;
            }
            ,
            JoinWindows.of(Duration.ofMinutes(KAFKA_STREAMS_JOINWINDOW_DURATION)),
            StreamJoined.with(Serdes.String(), validationResultSerde, casaEventDataSerde)
        )
        .peek(
            (key, value) -> {
                LOGGER.debug("Join Result -> key = " + key + ", value = " + value + ", value.getId() = " + value.getId() + ", status = " + value.getStatus());
            }
        );

        Map<String, KStream<String, EventData>> branches = joined.split(Named.as("split-"))
        //joined.split(Named.as("split-"))
        .branch(
            (key, value) -> {
                boolean s = value.getStatus().equals(Status.REJECTED);
                LOGGER.debug("split -> branch -> rejected: " + s);
                return s;
            },
            Branched.as("rejected")
            //Branched.withConsumer(ks -> ks.to(KAFKA_TOPIC_CASA_RESPONSE_EVENTS, Produced.with(Serdes.String(), casaEventDataSerde)), "rejected")
            
        )
        .defaultBranch(Branched.as("approved"))
        //.defaultBranch(Branched.withConsumer(ks -> ks.to(KAFKA_TOPIC_CASA_EVENTS, Produced.with(Serdes.String(), casaEventDataSerde)), "approved"))
        ;
        
        KStream<String, EventData> approved = branches.get("split-approved");
        approved.peek((key, value) -> {
                LOGGER.debug("split-approved -> key = " + key + ", value = " + value + ", value.getId() = " + value.getId() + ", status = " + value.getStatus());
            })
            .to(KAFKA_TOPIC_CASA_EVENTS, Produced.with(Serdes.String(), casaEventDataSerde));

        KStream<String, EventData> rejected = branches.get("split-rejected")
            .peek((key, value) -> {
                LOGGER.debug("split-rejected -> key = " + key + ", value = " + value + ", value.getId() = " + value.getId() + ", status = " + value.getStatus());
            })
        ;

        // send to audit
        approved
        .peek((key, value) -> {
            LOGGER.debug("split-approved -> sending audit -> key = " + key + ", value = " + value + ", value.getId() = " + value.getId() + ", status = " + value.getStatus());
        })
        .mapValues(new ValueMapper<EventData, EventData>(){
            public EventData apply(EventData value) {
                value.setEventSources(ValidationHandler.class.getName());
                value.setAuditTimestamp(new Date());
                return value;
            };
        })
        .to(KAFKA_TOPIC_AUDIT_EVENTS, Produced.with(Serdes.String(), casaEventDataSerde));

        // send to exception topic
        rejected.filter((k,v) -> {
            // only send if exception is allowed
            LOGGER.debug("Filter -> Exception -> key = " + k + ", exceptionAllow = " + v.isAllowException());
            return v.isAllowException();
        })
        .mapValues(new ValueMapper<EventData, String>(){
            public String apply(EventData value) {
                return (new KogitoEventModel<EventData>(
                    value.getId(), 
                    ValidationHandler.class.getName(), 
                    KOGITO_PROCESS_TYPE, 
                    value
                )).toJsonString();
            };
        })
        .peek((key, value) -> {
            LOGGER.debug("filter -> sending to exception -> key = " + key + ", value = " + value);
        })
        .to(KAFKA_TOPIC_EXCEPTION_EVENTS, Produced.with(Serdes.String(), Serdes.String()));
        ;

        // send to response topic
        rejected.filter((k,v) -> {
            LOGGER.debug("Filter -> Response -> key = " + k + ", exceptionAllow = " + v.isAllowException());
            // only send if exception is not allowed
            return !v.isAllowException();
        })
        .peek((k,v) ->{
            LOGGER.debug("Filter -> Sendig response -> key = " + k + ", exceptionAllow = " + v.isAllowException());
        })
        .to(KAFKA_TOPIC_CASA_RESPONSE_EVENTS, Produced.with(Serdes.String(), casaEventDataSerde));
        ;
        
        // send to audit
        rejected
        .peek((key, value) -> {
            LOGGER.debug("split-rejected -> sending audit -> key = " + key + ", value = " + value + ", value.getId() = " + value.getId() + ", status = " + value.getStatus());
        })
        .mapValues(new ValueMapper<EventData, EventData>(){
            public EventData apply(EventData value) {
                value.setEventSources(ValidationHandler.class.getName());
                value.setAuditTimestamp(new Date());
                return value;
            };
        })
        .to(KAFKA_TOPIC_AUDIT_EVENTS, Produced.with(Serdes.String(), casaEventDataSerde));
        

        return builder.build();
    }

}
