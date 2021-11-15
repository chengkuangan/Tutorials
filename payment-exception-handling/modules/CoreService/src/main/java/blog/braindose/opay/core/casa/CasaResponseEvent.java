package blog.braindose.opay.core.casa;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import org.hibernate.annotations.Immutable;
import blog.braindose.opay.obxevent.CasaEventData;

/**
 * Provides Outbox event implementation for response messages from Casa transaction processing.
 * This provides implementation based on Debezium Outbox pattern
 */
@Immutable
public class CasaResponseEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private final String id;
    private final JsonNode casa;
    private final Instant timestamp;

    public CasaResponseEvent(CasaEventData casa) {
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
                .put("responseMessages", casa.getResponseMessages())
                .put("paymentType", casa.getPaymentType().toString())
                .put("messageId", casa.getMessageId())
                .put("eventSources", casa.getEventSources().toString())
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

    @Override
    public String getAggregateType() {
        return "casa.response";
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
