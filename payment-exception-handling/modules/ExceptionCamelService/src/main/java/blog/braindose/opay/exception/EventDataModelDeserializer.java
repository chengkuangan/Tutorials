package blog.braindose.opay.exception;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import blog.braindose.opay.model.PaymentTypes;
import blog.braindose.opay.model.Status;
//import java.text.ParseException;
//import org.jboss.logging.Logger;

public class EventDataModelDeserializer extends StdDeserializer<EventDataModel> {

    // private static final Logger LOGGER =
    // Logger.getLogger(CheckResponseDeserializer.class);
    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");

    public EventDataModelDeserializer() {
        this(null);
    }

    public EventDataModelDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Custom deserialize to return EventDataModel from the Kogito response
     * 
     * @param jp
     * @param ctxt
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    public EventDataModel deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        EventDataModel result = null;

        if (node.get("data") != null) {

            JsonNode data = node.get("data");

            result = new EventDataModel(data.get("id").textValue(), data.get("recipientAccountNo").textValue(),
                    getStringValue(data.get("sourceAccountNo")), getDoubleValue(data.get("amount")),
                    getStringValue(data.get("exceptionReason")), getBooleanValue(data.get("exceptionApproved")),
                    getDateValue(data.get("createdTimestamp")), getDateValue(data.get("coreProcessedTimestamp")),
                    getDateValue(data.get("auditTimestamp")), getDateValue(data.get("responseReceivedTimestamp")),
                    getStringValue(data.get("recipientReference")),
                    PaymentTypes.valueOf(getStringValue(data.get("paymentType"))),
                    Status.valueOf(getStringValue(data.get("status"))), getStringValue(data.get("responseMessages")),
                    getStringValue(data.get("messageId")), getStringValue(data.get("eventSources")),
                    getDateValue(data.get("eventTimestamp")));

        }

        return result;
    }

    private String getStringValue(JsonNode node) {
        return node != null ? node.textValue() : null;
    }

    private double getDoubleValue(JsonNode node) {
        return node != null ? node.doubleValue() : null;
    }

    private boolean getBooleanValue(JsonNode node) {
        return node != null ? node.booleanValue() : false;
    }

    private Date getDateValue(JsonNode node) {
        try {
            return node != null ? df.parse(node.textValue()) : null;
        } catch (java.text.ParseException e) {
            throw new RuntimeException("Error processing the timestamp in the Event Data", e);
        }
    }

}
