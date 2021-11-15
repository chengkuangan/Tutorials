package blog.braindose.opay.casa;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import org.hibernate.annotations.Immutable;
import blog.braindose.opay.obxevent.CasaEventData;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Casa Event for Outbox pattern implementation using Debezium.
 */
@Immutable
public class CasaEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Unique Casa transaction id. @see blog.braindose.opay.casa.Casa#id
     */
    private final String id;
    /**
     * Payload to be sent to Kafka in JSON format.
     */
    private final JsonNode casa;
    /**
     * Timestamp for outbox pattern implementation. Defaulted to Casa transactionTimestamp. @see blog.braindose.opay.casa.Casa#transactionTimestamp
     */
    private final Instant timestamp;

    /**
     * Debezium Outbox aggregate type
     */
    @ConfigProperty(name = "outbox.aggregatetype")
    private String aggregateType;

    /**
     * Debezium outbox type
     */
    @ConfigProperty(name = "outbox.type")
    private String type;

    public CasaEvent(CasaEventData casa) {
        this.id = casa.getId();
        this.timestamp = Instant.ofEpochMilli(casa.getEventTimestamp().getTime());
        this.casa = convertToJson(casa);
    }

    private JsonNode convertToJson(CasaEventData casa) {
        ObjectNode asJson = mapper.createObjectNode()
                .put("id", casa.getId())
                .put("recipientAccountNo", casa.getRecipientAccountNo())
                .put("sourceAccountNo", casa.getSourceAccountNo())
                .put("amount", casa.getAmount())
                .put("recipientReference", casa.getRecipientReference())
                .put("paymentType", casa.getPaymentType().toString())
                .put("createdTimestamp", casa.getCreatedTimestampString())
                .put("eventSources", casa.getEventSources().toString())
                .put("eventTimestamp", casa.getEventTimestampString())
                .put("status", casa.getStatus().toString());
        return asJson;
    }

    @Override
    public String getAggregateId() {
        return id;
    }

    @Override
    public String getAggregateType() {
        return "casa.new";
    }

    @Override
    public JsonNode getPayload() {
        return casa;
    }

    @Override
    public String getType() {
        return "payment";
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    /*
    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CasaEvent other = (CasaEvent) obj;
        return Objects.equals(this.id, other.id);
    }
    */

}
