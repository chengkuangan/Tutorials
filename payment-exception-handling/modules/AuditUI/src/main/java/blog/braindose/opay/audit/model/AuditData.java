package blog.braindose.opay.audit.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.jboss.logging.Logger;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.codecs.pojo.annotations.BsonId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
/**
 * Provides the root data structure for audit information to be captured from Kafka event messages
 */
@MongoEntity(collection="payment")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditData extends PanacheMongoEntity{

    /**
     * A unique transaction ID. Each completed or failed transaction will only have one entry in audit logs
     */
    private String id; 
    
    @BsonProperty("_id")
    //@BsonId
    @BsonIgnore
    public ObjectId _id;
    
    /**
     * Details outlines the audit events with event source, status and time stamps
     */
    private List<AuditEntry> auditEntries;
    /**
     * The actual payload of the transaction
     */
    private Payload payload;
    /**
     * The timestamp that this audit aggregation is performed.
     */
    private String eventTimestamp;

    /**
     * The last status captured that this event occured.
     */
    private String lastStatus;

    private static final Logger LOGGER = Logger.getLogger(AuditData.class);

    public AuditData(){

    }

    public AuditData(String id, List<AuditEntry> auditEntries, Payload payload, String eventTimestamp, String status){
        this.id = id;
        this.auditEntries = auditEntries;
        this.eventTimestamp = eventTimestamp;
        this.lastStatus = status;
        this.payload = payload;
    }

    /**
     * @return String return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return List<AuditEntry> return the auditEntries
     */
    public List<AuditEntry> getAuditEntries() {
        return auditEntries;
    }

    /**
     * @param auditEntries the auditEvents to set
     */
    public void setAuditEntries(List<AuditEntry> auditEntries) {
        this.auditEntries = auditEntries;
    }

    /**
     * @return <T> return the payload
     */
    public Payload getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    /**
     * @return String return the eventTimestamp
     */
    public String getEventTimestamp() {
        return eventTimestamp;
    }

    /**
     * @param eventTimestamp the eventTimestamp to set
     */
    public void setEventTimestamp(String eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    /**
     * @return String return the lastStatus
     */
    public String getLastStatus() {
        return lastStatus;
    }

    /**
     * @param lastStatus the lastStatus to set
     */
    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

}
