package blog.braindose.opay.obxevent;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import org.hibernate.annotations.Immutable;

/**
 * Outbox Event for Casa Audit. Captures the auditing information for Casa transaction.
 * Provides the implementation for Debezium Outbox Pattern.
 */
@Immutable
public class CasaAuditEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private final String id;
    private final JsonNode casa;
    private final Instant timestamp;
    private String aggregateType;
    private String type;

    public CasaAuditEvent(CasaEventData casa, String type, String aggregateType) {
        this.id = casa.getId();
        casa.setAuditTimestamp(new Date());
        casa.setEventTimestamp(casa.getAuditTimestamp());
        this.timestamp = Instant.ofEpochMilli(casa.getEventTimestamp().getTime());
        this.casa = convertToJson(casa);
        this.type = type;
        this.aggregateType = aggregateType;
    }

    private JsonNode convertToJson(CasaEventData casa) {
        ObjectNode asJson = mapper.createObjectNode()
                .put("id", casa.getId())
                .put("recipientAccountNo", casa.getRecipientAccountNo())
                .put("sourceAccountNo", casa.getSourceAccountNo())
                .put("amount", casa.getAmount())
                .put("responseMessages", casa.getResponseMessages())
                .put("messageId", casa.getMessageId())
                .put("paymentType", casa.getPaymentType().toString())
                .put("eventSources", casa.getEventSources().toString())
                .put("createdTimestamp", casa.getCreatedTimestampString())
                .put("recipientReference", casa.getRecipientReference())
                .put("coreProcessedTimestamp", casa.getCoreProcessedTimestampString())
                .put("responseReceivedTimestamp", casa.getResponseReceivedTimestampString())
                .put("auditTimestamp", casa.getAuditTimestampString())
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
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    @Override
    public JsonNode getPayload() {
        return casa;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

}
