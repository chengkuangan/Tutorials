package blog.braindose.demo.debezium.camel.simulator;

public class Sku {
    public String sku;
    public String description;
    public double price;

    public String toString(){
        return "Sku [sku = " + sku + ", decription=" + description + ",price = " + price + "]";
    }
}
