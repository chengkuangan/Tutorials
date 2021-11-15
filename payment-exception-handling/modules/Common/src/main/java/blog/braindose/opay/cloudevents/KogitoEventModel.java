package blog.braindose.opay.cloudevents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * CloudEvents data model for Kogito DM.
 * 
 * adhere to the following example data structure
 * 
 * {
    "specversion": "1.0",
    "id": "a89b61a2-5644-487a-8a86-144855c5dce8",
    "source": "opay-casa",
    "type": "DecisionRequest",
    "subject": "Check The Credit Limit",
    "kogitodmnmodelname": "checklimit",
    "kogitodmnmodelnamespace": "https://kiegroup.org/dmn/_1D3A94BF-BCD5-470A-B22A-38B9E60ED24E",
    "data": {"CreditDetail":{"amount": 10,"transactionId":"1-1290-123123-000111","dailyLimit": 100,"balance": 1500,"dailyTotalAmount": 95}}
  }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KogitoEventModel<T>{

    private String specversion;
    private String id;
    private String source;
    private String type;
    private String subject;
    private String kogitodmnmodelname;
    private String kogitodmnmodelnamespace;
    private T data;
    
    public KogitoEventModel(){}

    public KogitoEventModel(String specversion, String id, String source, String type, String subject, String kogitodmnmodelname, String kogitodmnmodelnamespace, T data){
        this.specversion = specversion;
        this.id = id;
        this.source = source;
        this.type = type;
        this.subject = subject;
        this.kogitodmnmodelname = kogitodmnmodelname;
        this.kogitodmnmodelnamespace = kogitodmnmodelnamespace;
        this.data = data;
    }

    public KogitoEventModel(String id, String source, String type, String subject, String kogitodmnmodelname, String kogitodmnmodelnamespace, T data){
        this("1.0", id, source, type, subject, kogitodmnmodelname, kogitodmnmodelnamespace, data);
    }

    public KogitoEventModel(String id, String source, String type, T data){
        this("1.0", id, source, type, null, null, null, data);
    }

    /**
     * @return String return the specversion
     */
    public String getSpecversion() {
        return specversion;
    }

    /**
     * @param specversion the specversion to set
     */
    public void setSpecversion(String specversion) {
        this.specversion = specversion;
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
     * @return String return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return String return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return String return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return String return the kogitodmnmodelname
     */
    public String getKogitodmnmodelname() {
        return kogitodmnmodelname;
    }

    /**
     * @param kogitodmnmodelname the kogitodmnmodelname to set
     */
    public void setKogitodmnmodelname(String kogitodmnmodelname) {
        this.kogitodmnmodelname = kogitodmnmodelname;
    }

    /**
     * @return String return the kogitodmnmodelnamespace
     */
    public String getKogitodmnmodelnamespace() {
        return kogitodmnmodelnamespace;
    }

    /**
     * @param kogitodmnmodelnamespace the kogitodmnmodelnamespace to set
     */
    public void setKogitodmnmodelnamespace(String kogitodmnmodelnamespace) {
        this.kogitodmnmodelnamespace = kogitodmnmodelnamespace;
    }

    /**
     * @return T return the payload
     */
    @JsonIgnore
    public T getData() {
        return data;
    }

    /**
     * @param payload the payload to set
     */
    public void setData(T data) {
        this.data = data;
    }

    public String toJsonString(){
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
            try{
            jsonString = mapper.writeValueAsString(this);
            //System.out.println("jsonString = " + jsonString);
            jsonString = jsonString.substring(0, jsonString.length()-1) + ",\"data\": " + mapper.writeValueAsString(this.data) + "}";
            
        }
        catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
        return jsonString;
    }

    /**
     * Create a JSON formatted data based on the Payload
     * "data": {"CreditDetail":{"amount": 10,"transactionId":"1-1290-123123-000111","dailyLimit": 100,"balance": 1500,"dailyTotalAmount": 95}}
     */
    /*
    public String getData(){
      ObjectMapper mapper = new ObjectMapper();
      String jsonString = "";
        try{
          jsonString = mapper.writeValueAsString(this.payload);
          System.out.println("jsonString = " + jsonString);
          //jsonString = "\"data\": " + jsonString;
          
      }
      catch(JsonProcessingException e){
          throw new RuntimeException(e);
      }
      return jsonString;
    }
    */


}

