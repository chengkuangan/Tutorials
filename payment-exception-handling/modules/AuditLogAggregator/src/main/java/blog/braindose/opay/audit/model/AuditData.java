package blog.braindose.opay.audit.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.beans.Transient;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import blog.braindose.opay.model.Status;
import blog.braindose.opay.obxevent.CasaEventData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jboss.logging.Logger;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Provides the root data structure for audit information to be captured from Kafka event messages
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditData<T>{

    /**
     * A unique transaction ID. Each completed or failed transaction will only have one entry in audit logs
     */
    private String id;  
    /**
     * Details outlines the audit events with event source, status and time stamps
     */
    private List<AuditEntry> auditEntries;
    /**
     * The actual payload of the transaction
     */
    private T payload;
    /**
     * The timestamp that this audit aggregation is performed.
     */
    private Date eventTimestamp;

    /**
     * The last status captured that this event occured.
     */
    private String lastStatus;

    final static ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private static final Logger LOGGER = Logger.getLogger(AuditData.class);

    public AuditData(){

    }

    public AuditData(String id, List<AuditEntry> auditEntries, T payload, Date eventTimestamp, String status){
        this.id = id;
        this.auditEntries = auditEntries;
        this.eventTimestamp = eventTimestamp;
        this.lastStatus = status;
        this.payload = payload;
    }


    public AuditData<T> add(CasaEventData casaEventData){
        
        if (this.auditEntries == null) {
            LOGGER.debug("Audit Result -> this.auditEntries : " + this.auditEntries);
            this.auditEntries = new ArrayList<>();
        }
        // TODO: Should allow configuration of this number via environmental variable
        else if (this.auditEntries.size() > 3){    // clear the history after 4 records. at this moment only have 4 audit trails
            this.auditEntries.clear();
        }
        
        this.id = casaEventData.getId();
        this.eventTimestamp = new Date();
        this.auditEntries.add(
            new AuditEntry(
                casaEventData.getEventSources(), 
                casaEventData.getActualTimestamp(),
                this.getResponseMessage(casaEventData), 
                casaEventData.getStatus().toString()));
        this.setLastStatus(lastStatus);
        //this.setLastStatus(this.getUpdatedLastStatus(this.getLastStatus()));
        return this;
    }

    @Transient
    private String getResponseMessage(CasaEventData casaEventData){
        String s = casaEventData.getExceptionReason();
        return (s != null && !s.equals("")) ? s : casaEventData.getResponseMessages();
    }

    /**
     * Reduce implementation for Kafka streams
     * @param s1 message 1
     * @param s2 message 2
     * @return
     */
    @Transient
    @Deprecated
    static public String reduce(String s1, String s2){
        LOGGER.debug("s1 = " + s1);
        LOGGER.debug("s2 = " + s2);
        try{
            AuditData a1 = mapper.readValue(s1, AuditData.class);
            AuditData a2 = mapper.readValue(s2, AuditData.class);
            
            a1.setEventTimestamp(new Date());
            a1.addAll(a2.getAuditEntries());
            
            a1.setLastStatus(a1.getUpdatedLastStatus(a2.getLastStatus()));
            String str = mapper.writeValueAsString(a1);
            LOGGER.debug("reduced message = " + str);
            return str;
        }
        catch(JsonProcessingException e){
            LOGGER.error("Error parsing Kafka message into JSON Object");
            throw new RuntimeException("Error parsing Kafka message into JSON Object", e);
        }
    }

    @Transient
    @Deprecated
    static public String reduce(AuditData a1, AuditData a2){
        try{
            a1.setEventTimestamp(new Date());
            a1.addAll(a2.getAuditEntries());
            
            a1.setLastStatus(a1.getUpdatedLastStatus(a2.getLastStatus()));
            String str = mapper.writeValueAsString(a1);
            LOGGER.debug("reduced message = " + str);
            return str;
        }
        catch(JsonProcessingException e){
            LOGGER.error("Error parsing Kafka message into JSON Object");
            throw new RuntimeException("Error parsing Kafka message into JSON Object", e);
        }
    }

    /**
     * Get the last status in the chain of audit trail
     * @param s2 Audit message
     * @return Last status
     */
    @Transient
    public String getUpdatedLastStatus(String s2){
        String s1 = this.getLastStatus();
        return ( 
            s1.equals(Status.COMPLETED.toString()) ||
            s1.equals(Status.FAILED.toString())
            ) 
            ? s1 : s2;
    }

    /**
     * Add all the AuditEntry to the existing List
     * @param a List of audit entries to be added.
     */
    public void addAll(List<AuditEntry> a){
        List<AuditEntry> newList = new ArrayList<>();
        for (Iterator<AuditEntry> iter = a.iterator(); iter.hasNext(); ){
            AuditEntry a1 = iter.next();
            if (!isExists(a1)){
                newList.add(a1);
            }
        }
        this.auditEntries.addAll(newList);
    }

    /**
     * Check if the AuditEntry exists in the existing list by comparing the unique string from AuditEntry @see blog.braindose.opay.audit.model.AuditEntry#getUniqueString
     * @param a Audit trail entry
     * @return True if the AuditEntry found in the existing list.
     */
    public boolean isExists(AuditEntry a){
        boolean found = false;
        for (Iterator<AuditEntry> iter = this.auditEntries.iterator(); iter.hasNext(); ){
            AuditEntry a1 = iter.next();
            if (a1.getUniqueString().equals(a.getUniqueString())){
                found = true;
                break;
            }
        }
        return found;
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
    public T getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(T payload) {
        this.payload = payload;
    }

    /**
     * @return String return the eventTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getEventTimestamp() {
        return eventTimestamp;
    }

    /**
     * @param eventTimestamp the eventTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setEventTimestamp(Date eventTimestamp) {
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

    @JsonIgnore
    public String toJsonString(){
        final ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error(e);
        }
        return json;
    }

}
