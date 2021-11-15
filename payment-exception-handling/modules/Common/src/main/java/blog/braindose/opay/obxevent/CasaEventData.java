package blog.braindose.opay.obxevent;

import java.util.Date;
import java.beans.Transient;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import blog.braindose.opay.model.PaymentTypes;
import blog.braindose.opay.model.Status;
import io.quarkus.runtime.annotations.RegisterForReflection;

// TODO: Change this to more genric type as for EventData generic usage instead of Casa
/**
 * Provide the event data model for Casa outbox event implementation.
 * Provides the implementation for Debezium Outbox Pattern.
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CasaEventData {
    /**
     * Casa unique transaction id.
     */
    private String id;
    private String recipientAccountNo;
    private String sourceAccountNo;
    private double amount;
    /**
     * Exception approved or rejected reason
     */
    private String exceptionReason;
    /**
     * True if exception is approved
     */
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
    private PaymentTypes paymentType;
    /**
     * Status of casa processing. @see blog.braindose.paygate.model.Status
     */
    private Status status;
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

    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
    
    public CasaEventData() {
        super();
    }

    /**
     * A complete constructor for all fields. Used to intialize Casa Service auditing event data
     * @param id Casa transaction Id
     * @param recipientAccountNo Recipient account number
     * @param sourceAccountNo Source account number
     * @param amount Transaction amount
     * @param createdTimestamp Casa created timestamp
     * @param coreProcessedTimestamp Core Processed timestamp
     * @param auditTimestamp Audit timestamp
     * @param responseReceivedTimestamp Response received by Casa service timestamp
     * @param recipientReference Reference information for recipient
     * @param paymentType Payment type
     * @param status Status
     * @param responseMessages Response messages from Core or any error messages.
     * @param messageId The Kafka message key
     */
    public CasaEventData(String id, String recipientAccountNo, String sourceAccountNo, double amount, Date createdTimestamp, Date coreProcessedTimestamp, Date auditTimestamp, 
    Date responseReceivedTimestamp, String recipientReference, PaymentTypes paymentType, Status status, String responseMessages, String messageId) {
        super();
        this.id = id;
        this.recipientAccountNo = recipientAccountNo;
        this.sourceAccountNo = sourceAccountNo;
        this.amount = amount;
        this.createdTimestamp = createdTimestamp;
        this.coreProcessedTimestamp = coreProcessedTimestamp;
        this.auditTimestamp = auditTimestamp;
        this.responseReceivedTimestamp = responseReceivedTimestamp;
        this.recipientReference = recipientReference;
        this.paymentType = paymentType;
        this.status = status;
        this.responseMessages = responseMessages;
        this.messageId = messageId;
    }

    /**
     * A contructor for even data when Casa is being processed by Casa service:
     * - When Casa is first submitted/created
     * - OR when Response is received from Core Service
     * @param id Casa transaction Id
     * @param recipientAccountNo Recipient account number
     * @param sourceAccountNo Source account number
     * @param amount Transaction amount
     * @param createdTimestamp Casa created timestamp
     * @param coreProcessedTimestamp Core Processed timestamp
     * @param auditTimestamp Audit timestamp
     * @param responseReceivedTimestamp Response received by Casa service timestamp
     * @param recipientReference Reference information for recipient
     * @param paymentType Payment type
     * @param status Status
     * @param responseMessages Response messages from Core or any error messages.
     * @param messageId The Kafka message key
     */
    public CasaEventData(String id, String recipientAccountNo, String sourceAccountNo, double amount, Date createdTimestamp, Date coreProcessedTimestamp,  
    Date responseReceivedTimestamp, String recipientReference, PaymentTypes paymentType, Status status, String responseMessages, String messageId) {
        super();
        this.id = id;
        this.recipientAccountNo = recipientAccountNo;
        this.sourceAccountNo = sourceAccountNo;
        this.amount = amount;
        this.createdTimestamp = createdTimestamp;
        this.coreProcessedTimestamp = coreProcessedTimestamp;
        this.responseReceivedTimestamp = responseReceivedTimestamp;
        this.recipientReference = recipientReference;
        this.paymentType = paymentType;
        this.status = status;
        this.responseMessages = responseMessages;
        this.messageId = messageId;
    }

    /**
     * A contructor for even data when Casa is being processed by Casa service:
     * - When Casa is first submitted/created
     * - OR when Response is received from Core Service
     * @param id Casa transaction Id
     * @param recipientAccountNo Recipient account number
     * @param sourceAccountNo Source account number
     * @param amount Transaction amount
     * @param createdTimestamp Casa created timestamp
     * @param recipientReference Reference information for recipient
     * @param paymentType Payment type
     * @param status Status
     */
    public CasaEventData(String id, String recipientAccountNo, String sourceAccountNo, double amount, Date createdTimestamp, String recipientReference, PaymentTypes paymentType, Status status) {
        super();
        this.id = id;
        this.recipientAccountNo = recipientAccountNo;
        this.sourceAccountNo = sourceAccountNo;
        this.amount = amount;
        this.createdTimestamp = createdTimestamp;
        this.recipientReference = recipientReference;
        this.paymentType = paymentType;
        this.status = status;
    }

    /**
     * Get the event time stamp based on the status and event occurances
     * @return Date of the timestamp
     */
    @Transient
    public Date getActualTimestamp(){
        Date timestamp = null;
        if (this.status.equals(Status.SUBMITTED)){
            timestamp = this.createdTimestamp;
        }
        else if (this.status.equals(Status.COMPLETED) || this.status.equals(Status.FAILED)){
            timestamp = this.eventSources.indexOf(".core.") > 0 ? this.coreProcessedTimestamp : this.responseReceivedTimestamp;
        }
        else {
            timestamp = this.eventTimestamp;
        }
        return timestamp;
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
     * @return Date return the transactionTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    @JsonIgnore
    public String getCreatedTimestampString() {
        return createdTimestamp != null ? df.format(createdTimestamp) : null;
    }

    /**
     * @param createdTimestamp the transactionTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
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
     * @return PaymentTypes return the paymentType
     */
    public PaymentTypes getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(PaymentTypes paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * @return Status return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
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
     * @return Date return the coreProcessedTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getCoreProcessedTimestamp() {
        return coreProcessedTimestamp;
    }

    @JsonIgnore
    public String getCoreProcessedTimestampString() {
        return coreProcessedTimestamp != null ? df.format(coreProcessedTimestamp) : null;
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

    @JsonIgnore
    public String getAuditTimestampString() {
        return auditTimestamp != null ? df.format(auditTimestamp) : null;
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

    @JsonIgnore
    public String getResponseReceivedTimestampString() {
        return responseReceivedTimestamp != null ? df.format(responseReceivedTimestamp) : null;
    }

    /**
     * @param responseReceivedTimestamp the responseReceivedTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setResponseReceivedTimestamp(Date responseReceivedTimestamp) {
        this.responseReceivedTimestamp = responseReceivedTimestamp;
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

    @JsonIgnore
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public String getEventTimestampString() {
        return eventTimestamp != null ? df.format(eventTimestamp) : null;
    }

    /**
     * @param eventTimestamp the eventTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setEventTimestamp(Date eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }


    /**
     * @return String return the exceptionReason
     */
    public String getExceptionReason() {
        return exceptionReason;
    }

    /**
     * @param exceptionReason the exceptionReason to set
     */
    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    /**
     * @return boolean return the exceptionApproved
     */
    public boolean isExceptionApproved() {
        return exceptionApproved;
    }

    /**
     * @param exceptionApproved the exceptionApproved to set
     */
    public void setExceptionApproved(boolean exceptionApproved) {
        this.exceptionApproved = exceptionApproved;
    }

    @JsonIgnore
    public String toJsonString(){
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
            try{
            jsonString = mapper.writeValueAsString(this);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
        return jsonString;
    }

}
