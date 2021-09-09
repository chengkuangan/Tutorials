package blog.braindose.opay.core.casa;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;

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
    public Instant createdTimestamp;
    /**
     * Account updated timestamp
     */
    public Instant updatedTimestamp;
}
