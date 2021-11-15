package blog.braindose.opay.validation.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;

@JsonDeserialize(using = CheckResponseDeserializer.class)
public class CheckResponse {
    private String reason;
    private String transactionId;
    private boolean status;
    private Date timestamp;
    private boolean allowException;

    public CheckResponse(){}

    public CheckResponse(String reason, String transactionId, boolean status, Date timestamp, boolean allowException){
        this.reason = reason;
        this.transactionId = transactionId; 
        this.status = status;
        this.timestamp = timestamp;
        this.allowException = allowException;
    }

    /**
     * @return String return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return String return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return boolean return the status
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Do nothing and return the object itself
     * @return
     */
    public CheckResponse returnMyself(CheckResponse result){
        return result;
    }


    /**
     * @return Date return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * @return boolean return the allowException
     */
    public boolean isAllowException() {
        return allowException;
    }

    /**
     * @param allowException the allowException to set
     */
    public void setExceptionAllow(boolean allowException) {
        this.allowException = allowException;
    }

}
