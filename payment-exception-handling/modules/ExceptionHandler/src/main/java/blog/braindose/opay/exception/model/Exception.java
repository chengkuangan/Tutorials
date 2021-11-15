package blog.braindose.opay.exception.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
//import java.time.Instant;
//import java.beans.Transient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
//import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Provide the event data model for Casa outbox event implementation.
 * Provides the implementation for Debezium Outbox Pattern.
 */
//@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Exception {
    /**
     * Casa unique transaction id.
     */
    private String id;
    private String recipientAccountNo;
    private String sourceAccountNo;
    private double amount;
    private String exceptionReason;
    private boolean exceptionApproved;

    /**
     * Casa created timestamp to indicates the timestamp when the Casa create event is being fired.
     */
    private Date createdTimestamp;
    /**
     * Core processed timestamp to indicates the timestamp when the core processed the transaction.
     */
    private Date coreProcessedTimestamp;
    /**
     * Audit timestamp to indicates the timestamp when the audit event is being fired.
     */
    private Date auditTimestamp;
    /**
     * Response received timestamp to indicates the timestamp when the response from core is received by origination.
     */
    private Date responseReceivedTimestamp;
    /**
     * Referecense information for recipient.
     */
    private String recipientReference;
    /**
     * Payment type. @see blog.braindose.paygate.model.PaymentTypes
     */
    private String paymentType;
    /**
     * Status of casa processing. @see blog.braindose.paygate.model.Status
     */
    private String status;
    /**
     * Messages response from core backend if any. This could be error message when processing failed.
     */
    private String responseMessages;

    /**
     * Kafka header id
     * This is an unique id for each kafka message. Can be used to perform deduplication in the event of duplicated message sent by producer in the event of message resent due to unforeseen failure.
     */
    private String messageId;

    /**
     * Event source. This should be unique identifiable value for auditing purpose. Suggest to use Class.getName()
     */
    private String eventSources;
    /**
     * Event timestamp to provides timestamp when the event is being fired.
     */
    private Date eventTimestamp;

    public Exception(){
        this.setEventSources("blog.braindose.opay.exception.ExceptionHandler");
    }


    @Override
    public String toString() {
        return "EvenData: [{\"id\": \"" + id + "\", \"recipientAccountNo:\": \"" + recipientAccountNo 
        + "\", \"sourceAccountNo\": \"" + sourceAccountNo + "\", \"amount\": \"" + amount + "\", \"status\": \"" + status + "\", \"responseMessages\": \"" + responseMessages + "\"}]";
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
     * @return String return the recipientAccountNo
     */
    public String getRecipientAccountNo() {
        return recipientAccountNo;
    }

    /**
     * @param recipientAccountNo the recipientAccountNo to set
     */
    public void setRecipientAccountNo(String recipientAccountNo) {
        this.recipientAccountNo = recipientAccountNo;
    }

    /**
     * @return String return the sourceAccountNo
     */
    public String getSourceAccountNo() {
        return sourceAccountNo;
    }

    /**
     * @param sourceAccountNo the sourceAccountNo to set
     */
    public void setSourceAccountNo(String sourceAccountNo) {
        this.sourceAccountNo = sourceAccountNo;
    }

    /**
     * @return double return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return Date return the createdTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * @param createdTimestamp the createdTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * @return Date return the coreProcessedTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getCoreProcessedTimestamp() {
        return coreProcessedTimestamp;
    }

    /**
     * @param coreProcessedTimestamp the coreProcessedTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setCoreProcessedTimestamp(Date coreProcessedTimestamp) {
        this.coreProcessedTimestamp = coreProcessedTimestamp;
    }

    /**
     * @return Date return the auditTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getAuditTimestamp() {
        return auditTimestamp;
    }

    /**
     * @param auditTimestamp the auditTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setAuditTimestamp(Date auditTimestamp) {
        this.auditTimestamp = auditTimestamp;
    }

    /**
     * @return Date return the responseReceivedTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getResponseReceivedTimestamp() {
        return responseReceivedTimestamp;
    }

    /**
     * @param responseReceivedTimestamp the responseReceivedTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setResponseReceivedTimestamp(Date responseReceivedTimestamp) {
        this.responseReceivedTimestamp = responseReceivedTimestamp;
    }

    /**
     * @return String return the recipientReference
     */
    public String getRecipientReference() {
        return recipientReference;
    }

    /**
     * @param recipientReference the recipientReference to set
     */
    public void setRecipientReference(String recipientReference) {
        this.recipientReference = recipientReference;
    }

    /**
     * @return String return the paymentType
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    /**
     * @return String return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return String return the eventSources
     */
    public String getEventSources() {
        return eventSources;
    }

    /**
     * @param eventSources the eventSources to set
     */
    public void setEventSources(String eventSources) {
        this.eventSources = eventSources;
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
     * @return String return the reason
     */
    public String getExceptionReason() {
        return exceptionReason;
    }

    /**
     * @param reason the reason to set
     */
    public void setExceptionReason(String reason) {
        this.exceptionReason = reason;
    }

    /**
     * @return boolean return the approved
     */
    public boolean isExceptionApproved() {
        return exceptionApproved;
    }

    /**
     * @param approved the approved to set
     */
    public void setExceptionApproved(boolean approved) {
        this.exceptionApproved = approved;
    }

}
