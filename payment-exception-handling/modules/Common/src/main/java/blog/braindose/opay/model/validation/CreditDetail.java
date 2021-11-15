package blog.braindose.opay.model.validation;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(include=As.WRAPPER_OBJECT, use=Id.NAME)
public class CreditDetail{
    private double amount;
    private String transactionId;
    private double dailyLimit;
    private double balance;
    private double dailyTotalAmount;
    private boolean exception;

    public CreditDetail(){}

    public CreditDetail(double amount, String transactionId, double dailyLimit, double balance, double dailyTotalAmount, boolean exception){
        this.amount = amount;
        this.transactionId = transactionId;
        this.dailyLimit = dailyLimit;
        this.balance = balance;
        this.dailyTotalAmount = dailyTotalAmount;
        this.exception = exception;
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
     * @return double return the dailyLimit
     */
    public double getDailyLimit() {
        return dailyLimit;
    }

    /**
     * @param dailyLimit the dailyLimit to set
     */
    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    /**
     * @return double return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * @return double return the dailyTotalAmount
     */
    public double getDailyTotalAmount() {
        return dailyTotalAmount;
    }

    /**
     * @param dailyTotalAmount the dailyTotalAmount to set
     */
    public void setDailyTotalAmount(double dailyTotalAmount) {
        this.dailyTotalAmount = dailyTotalAmount;
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