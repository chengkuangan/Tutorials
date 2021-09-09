package blog.braindose.opay.obxevent;

import java.time.Instant;
import java.beans.Transient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import blog.braindose.opay.model.PaymentTypes;
import blog.braindose.opay.model.Status;
import io.quarkus.runtime.annotations.RegisterForReflection;

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
     * Casa created timestamp to indicates the timestamp when the Casa create event is being fired.
     */
    private Instant createdTimestamp;
    /**
     * Core processed timestamp to indicates the timestamp when the core processed the transaction.
     */
    private Instant coreProcessedTimestamp;
    /**
     * Audit timestamp to indicates the timestamp when the audit event is being fired.
     */
    private Instant auditTimestamp;
    /**
     * Response received timestamp to indicates the timestamp when the response from core is received by origination.
     */
    private Instant responseReceivedTimestamp;
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
    private Instant eventTimestamp;

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
    public CasaEventData(String id, String recipientAccountNo, String sourceAccountNo, double amount, Instant createdTimestamp, Instant coreProcessedTimestamp, Instant auditTimestamp, 
    Instant responseReceivedTimestamp, String recipientReference, PaymentTypes paymentType, Status status, String responseMessages, String messageId) {
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
    public CasaEventData(String id, String recipientAccountNo, String sourceAccountNo, double amount, Instant createdTimestamp, Instant coreProcessedTimestamp,  
    Instant responseReceivedTimestamp, String recipientReference, PaymentTypes paymentType, Status status, String responseMessages, String messageId) {
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
    public CasaEventData(String id, String recipientAccountNo, String sourceAccountNo, double amount, Instant createdTimestamp, String recipientReference, PaymentTypes paymentType, Status status) {
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
     * @return Instant of the timestamp
     */
    @Transient
    public Instant getActualTimestamp(){
        Instant timestamp = null;
        if (this.status.equals(Status.SUBMITTED)){
            timestamp = this.createdTimestamp;
        }
        else if (this.status.equals(Status.COMPLETED) || this.status.equals(Status.FAILED)){
            timestamp = this.eventSources.indexOf(".core.") > 0 ? this.coreProcessedTimestamp : this.responseReceivedTimestamp;
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
     * @return Instant return the transactionTimestamp
     */
    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * @param createdTimestamp the transactionTimestamp to set
     */
    public void setCreatedTimestamp(Instant createdTimestamp) {
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
     * @return Instant return the coreProcessedTimestamp
     */
    public Instant getCoreProcessedTimestamp() {
        return coreProcessedTimestamp;
    }

    /**
     * @param coreProcessedTimestamp the coreProcessedTimestamp to set
     */
    public void setCoreProcessedTimestamp(Instant coreProcessedTimestamp) {
        this.coreProcessedTimestamp = coreProcessedTimestamp;
    }


    /**
     * @return Instant return the auditTimestamp
     */
    public Instant getAuditTimestamp() {
        return auditTimestamp;
    }

    /**
     * @param auditTimestamp the auditTimestamp to set
     */
    public void setAuditTimestamp(Instant auditTimestamp) {
        this.auditTimestamp = auditTimestamp;
    }


    /**
     * @return Instant return the responseReceivedTimestamp
     */
    public Instant getResponseReceivedTimestamp() {
        return responseReceivedTimestamp;
    }

    /**
     * @param responseReceivedTimestamp the responseReceivedTimestamp to set
     */
    public void setResponseReceivedTimestamp(Instant responseReceivedTimestamp) {
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
     * @return Instant return the eventTimestamp
     */
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    /**
     * @param eventTimestamp the eventTimestamp to set
     */
    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

}
