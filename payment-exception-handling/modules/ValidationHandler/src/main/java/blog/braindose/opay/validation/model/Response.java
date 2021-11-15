package blog.braindose.opay.validation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private boolean status;
    private String reason;
    private boolean allowException;

    public Response() {
    }

    public Response(boolean status, String reason, boolean allowException) {
        this.status = status;
        this.reason = reason;
        this.allowException = allowException;
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
     * @return boolean return the allowException
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
