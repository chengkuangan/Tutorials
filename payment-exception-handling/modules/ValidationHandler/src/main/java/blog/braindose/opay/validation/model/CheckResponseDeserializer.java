package blog.braindose.opay.validation.model;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
//import java.text.ParseException;
//import org.jboss.logging.Logger;

public class CheckResponseDeserializer extends StdDeserializer<CheckResponse> {

    // private static final Logger LOGGER =
    // Logger.getLogger(CheckResponseDeserializer.class);

    public CheckResponseDeserializer() {
        this(null);
    }

    public CheckResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * Custom deserialize to return CheckLimit from the Kogito response
     * 
     * @param jp
     * @param ctxt
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    public CheckResponse deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        JsonNode node = jp.getCodec().readTree(jp);
        CheckResponse result = null;
        
        if (node.get("data") == null) {
            //System.out.println("------>  timestamp = " + node.get("timestamp").textValue());
            result = new CheckResponse(
                node.get("reason").textValue(), 
                node.get("transactionId").textValue(), 
                node.get("status").booleanValue(),
                new Date(node.get("timestamp").longValue()),
                node.get("allowException").booleanValue());

        } else {

            Iterator<Map.Entry<String, JsonNode>> data = node.get("data").fields();
            
            while (data.hasNext()) {
                Map.Entry<String, JsonNode> n = data.next();
                if (n.getKey().equals("CheckLimit") || n.getKey().equals("CheckFraud")) {
                    JsonNode cdn = n.getValue();
                    result = new CheckResponse(cdn.get("reason").textValue(), cdn.get("transactionId").textValue(),
                            cdn.get("status").booleanValue(), new Date(cdn.get("timestamp").longValue()), cdn.get("allowException").booleanValue());
                }
            }
        }
        
        return result;
    }
}
