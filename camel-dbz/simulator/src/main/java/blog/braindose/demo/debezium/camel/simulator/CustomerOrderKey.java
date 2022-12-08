package blog.braindose.demo.debezium.camel.simulator;

import java.io.Serializable;

public class CustomerOrderKey implements Serializable{
    public int custId;
    public String orderId;

    public CustomerOrderKey(){}
    
    public CustomerOrderKey(int custId, String orderId){
        this.custId = custId;
        this.orderId = orderId;

    }
}
