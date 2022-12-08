package blog.braindose.demo.dbz.camel.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomerOrder {
    private int custId;
    private String orderId;
    private String custName;
    private Date orderdate; 
    private String sku;

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");

    public CustomerOrder(){}

    public CustomerOrder(int custId, String orderId, String orderDate, String sku, String description, double amount){
        this.custId = custId;
        this.orderId = orderId;
        try{
            this.orderdate = df.parse(orderDate);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        this.sku = sku;
        this.description = description;
        this.amount = amount;
    }

    public CustomerOrder(String orderId, String orderDate, String sku, String description, double amount){
        this.orderId = orderId;
        try{
            this.orderdate = df.parse(orderDate);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        this.sku = sku;
        this.description = description;
        this.amount = amount;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    private String description; 
    private double amount;

    

    public int getCustId() {
        return custId;
    }
    public void setCustId(int custId) {
        this.custId = custId;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", custId=" + custId + ", custName=" + custName + ", orderdate=" + orderdate + ", sku=" + sku + "]";
    }

}
