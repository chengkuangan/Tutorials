package blog.braindose.demo.debezium.camel.simulator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Entity object for order table
 */
@Entity
@Table(name="orders")
public class Order  extends PanacheEntityBase {

    @Id
    public String orderId; 
    @NotNull
    public Date orderDate; 
    @NotNull
    public String sku;
    @NotNull
    public String description; 
    @NotNull
    public double amount;

    public Order(){}

    public Order(String orderId, String sku, String description, double amount){
        this.orderId = orderId;
        this.orderDate = new Date();
        this.sku = sku;
        this.description = description;
        this.amount = amount;
    }
}
