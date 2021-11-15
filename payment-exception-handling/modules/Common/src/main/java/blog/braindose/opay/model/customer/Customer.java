package blog.braindose.opay.model.customer;

import java.util.List;

/**
 * Customer profile entity model for customer-profile service.
 */
public class Customer{
    /**
     * Unique auto generated record id
     */
    private Long custId;
    /**
     * Customer first name
     */
    private String firstName;
    /**
     * Customer last name
     */
    private String lastName;
    /**
     * Mailing address
     */
    private List<Address> address;
    
    /**
     * Social ID
     */
    private String socialId;
    
    /**
     * Social ID type
     */
    private String socialIdType;

    /**
     * Accounts
     */
    private List<Account> accounts;

    /**
     * @return Long return the custId
     */
    public Long getCustId() {
        return custId;
    }

    /**
     * @param custId the custId to set
     */
    public void setCustId(Long custId) {
        this.custId = custId;
    }

    /**
     * @return String return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return String return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return List<Address> return the address
     */
    public List<Address> getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(List<Address> address) {
        this.address = address;
    }

    /**
     * @return String return the socialId
     */
    public String getSocialId() {
        return socialId;
    }

    /**
     * @param socialId the socialId to set
     */
    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    /**
     * @return String return the socialIdType
     */
    public String getSocialIdType() {
        return socialIdType;
    }

    /**
     * @param socialIdType the socialIdType to set
     */
    public void setSocialIdType(String socialIdType) {
        this.socialIdType = socialIdType;
    }

    /**
     * @return List<Account> return the accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * @param accounts the accounts to set
     */
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

}
