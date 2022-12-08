package blog.braindose.demo.debezium.camel.simulator;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name="customer")
public class Customer extends PanacheEntityBase{
    @Id
    public int custId;
    public String name;
    
    @Override
    public String toString() {
        return "Customer [custId=" + custId + ", name=" + name +"]";
    }

}
