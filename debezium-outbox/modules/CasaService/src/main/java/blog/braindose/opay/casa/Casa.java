package blog.braindose.opay.casa;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import blog.braindose.opay.model.PaymentTypes;
import blog.braindose.opay.model.Status;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Casa entity model for Casa service.
 */
@Entity
@Table(name="casa")
public class Casa  extends PanacheEntityBase{
    /**
     * Unique referenceId for Casa Transaction. For new transaction, should be generated using @see blog.braindose.opay.util..GenTxnId#id(int type)
     */
    @Id
    public String id;
    /**
     * Recipient account number.
     */
    public String recipientAccountNo;
    /**
     * Source account number.
     */
    public String sourceAccountNo;
    /**
     * Amount to credit
     */
    public double amount;
    /**
     * Casa created timestamp when this record is being created in Casa database.
     */
    public Instant createdTimestamp;
    /**
     * The timestamp that the core process this request
     */
    public Instant coreProcessedTimestamp;
    /**
     * The timestamp the Casa service received response from Core services
     */
    public Instant responseTimestamp;
    /**
     * Reference notes for this transactions.
     */
    public String recipientReference;
    /**
     * Payment type. In this case defaulted to PaymentTypes.CASA in constructor. @see blog.braindose.opay.model.PaymentTypes
     */
    @Enumerated(EnumType.STRING)
    public PaymentTypes paymentType;
    /**
     * Status of casa processing. @see blog.braindose.opay.model.Status
     */
    @Enumerated(EnumType.STRING)
    public Status status;
    /**
     * Messages response from core backend if any. This could be error message when
     * processing failed or any message that need to be accompied with the Status @see Status
     */
    public String responseMessages;

    /**
     * Default constructor with paymentType initiated to PaymentTypes.CASA 
     * @see paymentType
     * @see blog.braindose.opay.model.PaymentTypes.CASA
     */
    public Casa() {
        this.paymentType = PaymentTypes.CASA;
    }
}
