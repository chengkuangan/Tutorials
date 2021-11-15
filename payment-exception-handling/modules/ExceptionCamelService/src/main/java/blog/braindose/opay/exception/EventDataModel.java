package blog.braindose.opay.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import blog.braindose.opay.obxevent.CasaEventData;
import blog.braindose.opay.model.PaymentTypes;
import blog.braindose.opay.model.Status;
import java.util.Date;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EventDataModelDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDataModel extends CasaEventData{

    public EventDataModel(){
        super();
    }

    public EventDataModel(
        String id, String recipientAccountNo, 
        String sourceAccountNo, double amount, 
        String exceptionReason, boolean exceptionApproved, 
        Date createdTimestamp, Date coreProcessedTimestamp,
        Date auditTimestamp, Date responseReceivedTimestamp, 
        String recipientReference, PaymentTypes paymentType, 
        Status status, String responseMessages, 
        String messageId, String eventSources, 
        Date eventTimestamp){

        super();

        setId(id);
        setRecipientAccountNo(recipientAccountNo);
        setSourceAccountNo(sourceAccountNo);
        setAmount(amount);
        setExceptionReason(exceptionReason);
        setExceptionApproved(exceptionApproved);
        setCreatedTimestamp(createdTimestamp);
        setCoreProcessedTimestamp(coreProcessedTimestamp);
        setAuditTimestamp(auditTimestamp);
        setResponseReceivedTimestamp(responseReceivedTimestamp);
        setRecipientReference(recipientReference);
        setPaymentType(paymentType);
        setStatus(status);
        setResponseMessages(responseMessages);
        setMessageId(messageId);
        setEventSources(eventSources);
        setEventTimestamp(eventTimestamp);

    }

}

