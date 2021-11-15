package blog.braindose.opay.model.audit;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AuditEntry{
    private String eventSource;
    private Date eventTimestamp;
    private String responseMessages;
    private String status;

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
     * @return Date return the eventTimestamp
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
     * @return Object return the responseMessages
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

}
