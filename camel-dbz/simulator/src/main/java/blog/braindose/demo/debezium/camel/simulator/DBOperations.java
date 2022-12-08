package blog.braindose.demo.debezium.camel.simulator;

import javax.transaction.Transactional;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Provides the implementation of db operation for Casa account
 * 
 */
@ApplicationScoped
@Transactional
public class DBOperations {
    
    private static final Logger LOGGER = Logger.getLogger(DBOperations.class);

    /**
     * Perform new order creation. Throws exceptions in case of problem to be captured by ComsumeCasa @see blog.braindose.core.casa.ComsumeCasa
     * Using Transactional.TxType.REQUIRES_NEW for this method so that any exception will cause the database transactions to be rolled back but 
     * the messaging consuming and producing events in ComsumeCasa will be interrupted.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public String createOrder(int custId, String sku, String description, double amount) {

        String orderId = TxnId.id();

        Order order = new Order(orderId, sku, description, amount);
        order.persist();
        
        CustomerOrder custOrder = new CustomerOrder(orderId, custId);
        custOrder.persist();

        return orderId;
    }
}
