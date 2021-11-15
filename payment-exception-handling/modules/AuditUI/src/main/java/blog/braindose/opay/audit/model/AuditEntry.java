package blog.braindose.opay.audit.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonCreator;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.RuntimeErrorException;

public class AuditEntry {
    /**
     * The value to indicate the FQDM for the Java class that initiated this event
     */
    private String eventSource;
    /**
     * The timestamp when this event is captured
     */
    //@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
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

    final private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");

    public AuditEntry(){}

    public AuditEntry(String eventSource, String eventTimestamp, String responseMessages, String status){
        this.eventSource = eventSource;
        this.responseMessages = responseMessages;
        this.eventTimestamp = eventTimestamp;
        this.status = status;
    }

   /*
    @BsonCreator
    public AuditEntry(
        @BsonProperty("eventSource") final String eventSource, 
        @BsonProperty("eventTimestamp") final String eventTimestamp, 
        @BsonProperty("responseMessages") final String responseMessages, 
        @BsonProperty("status") final String status){
        this.eventSource = eventSource;
        this.responseMessages = responseMessages;
        try{
            this.eventTimestamp = df.parse(eventTimestamp);
        }
        catch(java.text.ParseException e){
            throw new RuntimeException(e);
        }
        this.status = status;
    }
    */

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
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss.SSSX")
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
