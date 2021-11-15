package blog.braindose.opay.validation.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jboss.logging.Logger;

public class ValidationResult {

    private String transactionId;
    private boolean status = true;
    private boolean allowException;

    private static final Logger LOGGER = Logger.getLogger(ValidationResult.class);

    private List<Response> responses;

    public ValidationResult() {
        LOGGER.debug("Initialize ValidationResult ... : , this.status = " + this.status);
    }

    public ValidationResult(String transactionId, List<Response> responses) {
        this.transactionId = transactionId;
        this.responses = responses;
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
     * @return List<Response> return the responses
     */
    public List<Response> getResponses() {
        return responses;
    }

    /**
     * @param responses the responses to set
     */
    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public ValidationResult addResponse(CheckResponse response) {
        
        if (this.responses == null) {
            LOGGER.debug("Validation Result -> this.responses : " + this.responses);
            this.responses = new ArrayList<>();
        }
        // TODO: Should allow configuration of this number via environmental variable
        else if (this.responses.size() > 1){    // clear the history after 2 records. We only have 2 validations (credit check and fraud check) at the moment. 
            this.status = true;
            this.allowException = false;
            this.responses.clear();
        }
        LOGGER.debug("Validation Result -> this.responses.size " + this.responses.size());
        LOGGER.debug("Validation Result: " + response.getTransactionId() + " --> response.isStatus() = " + response.isStatus() + ", this.status = " + this.status + ", this.exceptionAllow = " + this.allowException + ", response.isExceptionAllow() = " + response.isAllowException());
        this.responses.add(new Response(response.isStatus(), response.getReason(), response.isAllowException()));
        this.status = ( this.status && response.isStatus());
        this.allowException = ( this.allowException || response.isAllowException());
        this.transactionId = response.getTransactionId();
        return this;
    }

    @JsonIgnore
    public String getReasons(){
        String r = "";
        Iterator<Response> res = this.responses.iterator();
        while (res.hasNext()){
            String r1 = res.next().getReason();
            r += (r1 != null ? r1 + ", " : "");
        }
        return r;
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
     * @return boolean return the exceptionAllow
     */
    public boolean isAllowException() {
        return allowException;
    }

    /**
     * @param allowException the allowException to set
     */
    public void setAllowException(boolean allowException) {
        this.allowException = allowException;
    }

}
