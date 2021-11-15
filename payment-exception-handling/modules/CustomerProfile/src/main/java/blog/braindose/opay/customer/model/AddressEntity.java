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
 * Address entity model for customer-profile service.
 */
@Entity
@Table(name="address")
public class AddressEntity extends PanacheEntityBase{

    @ManyToOne
    @JoinColumn(name="custId", nullable=false)
    private CustomerEntity profile;

    /**
     * Unigue ID
     */
    @Id 
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    public Long addressId;
    /**
     * Address line 1
     */
    public String address1;
    /**
     * Address line 2
     */
    public String address2;
    /**
     * postcode
     */
    public String postcode;
    /**
     * City
     */
    public String city;
    /**
     * State
     */
    public String state;
    /**
     * Country code
     */
    public String country;
    /**
     * Address type
     */
    public String type;
}
