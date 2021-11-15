package blog.braindose.opay.model.validation;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class Transaction{

    private String transactionId;
    private double amount;
    private String originCountry;
    private String destCountry;
    private String transactionType;
    private boolean exception;
    
    public Transaction(){}

    public Transaction(String transactionId, double amount, String originCountry, String destCountry, String transactionType, boolean exception){
        this.transactionId = transactionId;
        this.amount = amount;
        this.originCountry = originCountry;
        this.destCountry = destCountry;
        this.transactionType = transactionType;
        this.exception = exception;
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
     * @return String return the originCountry
     */
    public String getOriginCountry() {
        return originCountry;
    }

    /**
     * @param originCountry the originCountry to set
     */
    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    /**
     * @return String return the destCountry
     */
    public String getDestCountry() {
        return destCountry;
    }

    /**
     * @param destCountry the destCountry to set
     */
    public void setDestCountry(String destCountry) {
        this.destCountry = destCountry;
    }

    /**
     * @return String return the transactionType
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType the transactionType to set
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * @return boolean return the exception
     */
    public boolean isException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(boolean exception) {
        this.exception = exception;
    }

}