package blog.braindose.opay.audit.model;

/**
 * Casa data model for audit purpose
 */
public class Casa{
    /**
     * Recipient account number.
     */
    private String recipientAccountNo;
    /**
     * Source account number.
     */
    private String sourceAccountNo;
    /**
     * Amount to credit
     */
    private double amount;
    /**
     * Reference notes for this transactions.
     */
    private String recipientReference;
    

    public Casa(){}

    public Casa(String recipientAccountNo, String sourceAccountNo, double amount, String recipientReference){
        this.recipientAccountNo = recipientAccountNo;
        this.sourceAccountNo = sourceAccountNo;
        this.amount = amount;
        this.recipientReference = recipientReference;
    }

    /**
     * @return String return the recipientAccountNo
     */
    public String getRecipientAccountNo() {
        return recipientAccountNo;
    }

    /**
     * @param recipientAccountNo the recipientAccountNo to set
     */
    public void setRecipientAccountNo(String recipientAccountNo) {
        this.recipientAccountNo = recipientAccountNo;
    }

    /**
     * @return String return the sourceAccountNo
     */
    public String getSourceAccountNo() {
        return sourceAccountNo;
    }

    /**
     * @param sourceAccountNo the sourceAccountNo to set
     */
    public void setSourceAccountNo(String sourceAccountNo) {
        this.sourceAccountNo = sourceAccountNo;
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
     * @return String return the recipientReference
     */
    public String getRecipientReference() {
        return recipientReference;
    }

    /**
     * @param recipientReference the recipientReference to set
     */
    public void setRecipientReference(String recipientReference) {
        this.recipientReference = recipientReference;
    }

}
