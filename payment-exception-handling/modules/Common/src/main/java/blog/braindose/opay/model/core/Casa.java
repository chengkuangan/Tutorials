package blog.braindose.opay.model.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Entity object for casa table in Core service database
 */
public class Casa{

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

    /**
     * Unique account number
     */
    private String accountNo;
    /**
     * Account balance
     */
    private double balance;
    /**
     * Account created timestamp
     */
    private Date createdTimestamp;
    /**
     * Account updated timestamp
     */
    private Date updatedTimestamp;

    private double dailyTotalAmount;
    /**
     * Daily total amount last updated timestamp
     */
    private Date dtaUpdatedTimestamp;

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
     * @return Instant return the createdTimestamp
     */
    public String getCreatedTimestamp() {
        return df.format(createdTimestamp);
    }

    // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getCreatedDate() {
        return createdTimestamp;
    }

    /**
     * @param createdTimestamp the createdTimestamp to set
     */
    // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * @return Instant return the updatedTimestamp
     */
    public String getUpdatedTimestamp() {
        return df.format(updatedTimestamp);
        //return updatedTimestamp;
    }
    // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getUpdatedDate() {
        return updatedTimestamp;
    }

    /**
     * @param updatedTimestamp the updatedTimestamp to set
     */
    // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
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
    // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public Date getDtaUpdatedTimestamp() {
        return dtaUpdatedTimestamp;
    }

    /**
     * @param dtaUpdatedTimestamp the dtaUpdatedTimestamp to set
     */
    // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSX")
    public void setDtaUpdatedTimestamp(Date dtaUpdatedTimestamp) {
        this.dtaUpdatedTimestamp = dtaUpdatedTimestamp;
    }

}
