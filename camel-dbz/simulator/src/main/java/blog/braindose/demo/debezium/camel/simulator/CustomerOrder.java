package blog.braindose.demo.debezium.camel.simulator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Entity object for order table
 */
@Entity
@Table(name="customerOrders")
@IdClass(CustomerOrderKey.class)
public class CustomerOrder  extends PanacheEntityBase {

    @Id
    public String orderId; 
    @Id
    public int custId;

    public CustomerOrder(){}

    public CustomerOrder(String orderId, int custId){
        this.orderId = orderId;
        this.custId = custId;
    }

}
