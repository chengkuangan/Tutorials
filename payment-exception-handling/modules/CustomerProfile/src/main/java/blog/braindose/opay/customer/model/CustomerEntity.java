package blog.braindose.opay.customer.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.GenerationType;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

/**
 * Customer profile entity model for customer-profile service.
 */
@Entity
@Table(name="profile")
public class CustomerEntity extends PanacheEntityBase{
    /**
     * Unique auto generated record id
     */
    @Id 
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    public Long custId;
    /**
     * Customer first name
     */
    public String firstName;
    /**
     * Customer last name
     */
    public String lastName;
    /**
     * Mailing address
     */
    @OneToMany(mappedBy="profile")
    public List<AddressEntity> address;
    
    /**
     * Social ID
     */
    public String socialId;
    
    /**
     * Social ID type
     */
    public String socialIdType;

    /**
     * Accounts
     */
    @OneToMany(mappedBy="profile")
    public List<AccountEntity> accounts;

    public static CustomerEntity findByAccount(String accNo){
        return find("select c from CustomerEntity c left join c.accounts a where a.accountNo = ?1 ", accNo).firstResult();
    }
}
