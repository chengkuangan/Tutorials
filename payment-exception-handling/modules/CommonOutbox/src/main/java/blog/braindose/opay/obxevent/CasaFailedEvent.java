package blog.braindose.opay.obxevent;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import org.hibernate.annotations.Immutable;

/**
 * Outbox Event for Casa Failed event. Captures the auditing information for Casa failed transaction.
 * Provides the implementation for Debezium Outbox Pattern.
 */
@Immutable
public class CasaFailedEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private final String id;
    private final JsonNode casa;
    private final Instant timestamp;

    public CasaFailedEvent(CasaEventData casa) {
        this.id = casa.getId();
        this.timestamp = Instant.ofEpochMilli(casa.getEventTimestamp().getTime());
        this.casa = convertToJson(casa);
    }

    private JsonNode convertToJson(CasaEventData casa) {
        ObjectNode asJson = mapper.createObjectNode().put("id", casa.getId())
                .put("recipientAccountNo", casa.getRecipientAccountNo())
                .put("sourceAccountNo", casa.getSourceAccountNo())
                .put("amount", casa.getAmount())
                .put("reason", casa.getResponseMessages())
                .put("paymentType", casa.getPaymentType().toString())
                .put("eventSources", casa.getEventSources().toString())
                .put("messageId", casa.getMessageId())
                .put("createdTimestamp", casa.getCreatedTimestampString())
                .put("coreProcessedTimestamp", casa.getCoreProcessedTimestampString())
                .put("eventTimestamp", casa.getEventTimestampString())
                .put("status", casa.getStatus().toString());
        return asJson;
    }

    @Override
    public String getAggregateId() {
        return id;
    }
    // TODO: Change all topic names to using hyphen instead of dot
    // TODO: parameterize the aggregateType and type using environmental variables.
    // TODO: Need to translate failed transaction into exception events
    @Override
    public String getAggregateType() {
        return "casa.failed";
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
