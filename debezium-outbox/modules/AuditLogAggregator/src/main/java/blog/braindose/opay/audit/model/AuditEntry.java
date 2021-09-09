package blog.braindose.opay.audit.model;

import java.beans.Transient;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditEntry {
    /**
     * The value to indicate the FQDM for the Java class that initiated this event
     */
    private String eventSource;
    /**
     * The timestamp when this event is captured
     */
    private String eventTimestamp;
    
    /**
     * The response messages if any. Could be error messages and information from Core system processing
     */
    private String responseMessages;
    /**
     * The status of the transaction when this event occurs. 
     * If may have similar value for multiple different events depending on the logic of the business process.
     */
    private String status;

    public AuditEntry(){}

    public AuditEntry(String eventSource, String eventTimestamp, String responseMessages, String status){
        this.eventSource = eventSource;
        this.responseMessages = responseMessages;
        this.eventTimestamp = eventTimestamp;
        this.status = status;
    }

    /**
     * @return String return the eventSource
     */
    public String getEventSource() {
        return eventSource;
    }

    /**
     * @param eventSource the eventSource to set
     */
    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    /**
     * @return String return the eventTimestamp
     */
    public String getEventTimestamp() {
        return eventTimestamp;
    }

    
    /**
     * Set the event time stamp with String value
     * @param eventTimestamp The event timestamp in String format.
     */
    public void setEventTimestamp(String eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    /**
     * @return String return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Return a unique string for comparison
     * @return
     */
    @Transient
    public String getUniqueString(){
        return this.eventSource + this.status + this.eventTimestamp;
    }

    /**
     * @return String return the responseMessages
     */
    public String getResponseMessages() {
        return responseMessages;
    }

    /**
     * @param responseMessages the responseMessages to set
     */
    public void setResponseMessages(String responseMessages) {
        this.responseMessages = responseMessages;
    }

}
