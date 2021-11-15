package blog.braindose.opay.core.casa;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Entity object for casa table in Core service database
 */
@Entity
@Table(name="casa")
public class CasaCore  extends PanacheEntityBase {
    /**
     * Unique account number
     */
    @Id
    public String accountNo;
    /**
     * Account balance
     */
    public double balance;
    /**
     * Account created timestamp
     */
    @NotNull
    public Date createdTimestamp;
    /**
     * Account updated timestamp
     */
    public Date updatedTimestamp;
    /**
     * Keep track of daily total transacted amount as an originated account. Reset daily
     */
    public double dailyTotalAmount = 0;
    /**
     * Daily total amount last updated timestamp
     */
    @Column(nullable = true)
    public Date dtaUpdatedTimestamp;
}
