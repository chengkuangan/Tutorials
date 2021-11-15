package blog.braindose.opay.model.audit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Audit{
    private String id;
    private List<AuditEntry> auditEntries;
    private Payload payload;
    private Date eventTimestamp;
    private String lastStatus;

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
     * @param auditEntries the auditEntries to set
     */
    public void setAuditEntries(List<AuditEntry> auditEntries) {
        this.auditEntries = auditEntries;
    }

    /**
     * @return Payload return the payload
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