package blog.braindose.opay.customer.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.GenerationType;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Account
 */
@Entity
@Table(name="account")
public class AccountEntity extends PanacheEntityBase{
    /**
     * Unigue ID
     */
    @Id 
    public String accountNo;
    /**
     * Account type
     */
    public String accountType;

    /**
     * Many to One entiry relationship with CustomerEntity
     */
    @ManyToOne
    @JoinColumn(name="custId", nullable=false)
    private CustomerEntity profile;

    /**
     * A customer defined daily allowed transaction limit.
     */
    public double dailyLimit;
}
