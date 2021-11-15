package blog.braindose.opay.model.customer;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * Account
 */
public class Account{

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX");
    /**
     * Account No
     */
    private String accountNo;
    /**
     * Account type
     */
    private String accountType;
    private double balance;
    private Date createdTimestamp;
    /**
     * Account updated timestamp
     */
    private Date updatedTimestamp;

    private double dailyTotalAmount;
    
    private Date dtaUpdatedTimestamp;

    /**
     * A customer defined daily allowed transaction limit.
     */
    private double dailyLimit;

    /**
     * @return String return the accountNo
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo the accountNo to set
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * @return String return the accountType
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * @param accountType the accountType to set
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
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
     * @return Date return the createdTimestamp
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public String getCreatedTimestamp() {
        return df.format(createdTimestamp);
    }

    /**
     * @param createdTimestamp the createdTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * @return Date return the updatedTimestamp
     */
    public String getUpdatedTimestamp() {
        return updatedTimestamp != null ? df.format(updatedTimestamp) : "";
    }

    /**
     * @param updatedTimestamp the updatedTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
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
     * @return Date return the dtaUpdatedTimestamp
     */
    public String getDtaUpdatedTimestamp() {
        return (dtaUpdatedTimestamp != null ? df.format(dtaUpdatedTimestamp) : "");
        //return dtaUpdatedTimestamp;
    }

    /**
     * @param dtaUpdatedTimestamp the dtaUpdatedTimestamp to set
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setDtaUpdatedTimestamp(Date dtaUpdatedTimestamp) {
        this.dtaUpdatedTimestamp = dtaUpdatedTimestamp;
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

}
