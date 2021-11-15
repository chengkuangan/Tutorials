package blog.braindose.opay.model.audit;

public class Payload{
    private String recipientAccountNo;
    private String sourceAccountNo;
    private double amount;
    private String recipientReference;

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