package blog.braindose.opay.validation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import blog.braindose.opay.obxevent.CasaEventData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventData extends CasaEventData{

    @JsonIgnore
    private boolean allowException;
    

    /**
     * @return boolean return the allowException
     */
    public boolean isAllowException() {
        return allowException;
    }

    /**
     * @param exceptionAllow the exceptionAllow to set
     */
    public void setAllowException(boolean allowException) {
        this.allowException = allowException;
    }

}
